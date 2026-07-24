-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jul 24, 2026 at 08:59 AM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- ============================================================
-- BASE DE DONNÉES : dossierexpert
-- ============================================================

CREATE DATABASE IF NOT EXISTS `dossierexpert` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `dossierexpert`;

-- ============================================================
-- PROCEDURES STOCKÉES
-- ============================================================

DELIMITER $$
--
-- Procédure : generer_num_dossier
--
CREATE PROCEDURE `generer_num_dossier` (OUT `p_num_dossier` VARCHAR(50))  
BEGIN
    DECLARE v_annee VARCHAR(4);
    DECLARE v_compteur INT;
    
    SET v_annee = DATE_FORMAT(NOW(), '%Y');
    
    SELECT COUNT(*) + 1 INTO v_compteur 
    FROM dossiers 
    WHERE YEAR(date_dossier) = YEAR(NOW());
    
    SET p_num_dossier = CONCAT('D', v_annee, '-', LPAD(v_compteur, 4, '0'));
END$$

--
-- Procédure : rechercher_dossiers
--
CREATE PROCEDURE `rechercher_dossiers` (
    IN `p_mot_cle` VARCHAR(255), 
    IN `p_source` VARCHAR(100), 
    IN `p_avocat` VARCHAR(100), 
    IN `p_statut` VARCHAR(50), 
    IN `p_date_debut` DATE, 
    IN `p_date_fin` DATE, 
    IN `p_montant_min` DECIMAL(15,2), 
    IN `p_montant_max` DECIMAL(15,2)
)  
BEGIN
    SELECT * FROM v_dossiers_complets
    WHERE 
        (p_mot_cle IS NULL OR 
         num_dossier LIKE CONCAT('%', p_mot_cle, '%') OR
         source LIKE CONCAT('%', p_mot_cle, '%') OR
         avocat LIKE CONCAT('%', p_mot_cle, '%') OR
         remarques LIKE CONCAT('%', p_mot_cle, '%') OR
         num_messagerie LIKE CONCAT('%', p_mot_cle, '%'))
        AND (p_source IS NULL OR source LIKE CONCAT('%', p_source, '%'))
        AND (p_avocat IS NULL OR avocat LIKE CONCAT('%', p_avocat, '%'))
        AND (p_statut IS NULL OR statut = p_statut)
        AND (p_date_debut IS NULL OR date_dossier >= p_date_debut)
        AND (p_date_fin IS NULL OR date_dossier <= p_date_fin)
        AND (p_montant_min IS NULL OR montant >= p_montant_min)
        AND (p_montant_max IS NULL OR montant <= p_montant_max)
    ORDER BY date_dossier DESC;
END$$

--
-- Procédure : statistiques_par_avocat
--
CREATE PROCEDURE `statistiques_par_avocat` ()  
BEGIN
    SELECT 
        avocat,
        COUNT(*) as total_dossiers,
        SUM(CASE WHEN etat_dossier = TRUE THEN 1 ELSE 0 END) as dossiers_actifs,
        SUM(montant) as montant_total,
        AVG(montant) as montant_moyen,
        MIN(date_dossier) as premier_dossier,
        MAX(date_dossier) as dernier_dossier
    FROM dossiers
    WHERE avocat IS NOT NULL AND avocat != ''
    GROUP BY avocat
    ORDER BY total_dossiers DESC;
END$$

--
-- Procédure : nettoyer_sessions
--
CREATE PROCEDURE `nettoyer_sessions` ()  
BEGIN
    UPDATE sessions SET est_active = FALSE
    WHERE date_expiration < NOW() AND est_active = TRUE;
    
    DELETE FROM sessions 
    WHERE date_expiration < DATE_SUB(NOW(), INTERVAL 30 DAY);
END$$

DELIMITER ;

-- ============================================================
-- TABLE : personnels
-- ============================================================

CREATE TABLE `personnels` (
    `id` int NOT NULL,
    `nom` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
    `prenom` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
    `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    `mot_de_passe` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `telephone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `role` enum('ADMIN','UTILISATEUR','INVITE') COLLATE utf8mb4_unicode_ci DEFAULT 'UTILISATEUR',
    `est_actif` tinyint(1) DEFAULT '1',
    `date_inscription` datetime DEFAULT CURRENT_TIMESTAMP,
    `dernier_acces` datetime DEFAULT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

-- ============================================================
-- TABLE : dossiers (avec dossier_nombre)
-- ============================================================

CREATE TABLE `dossiers` (
    `id` int NOT NULL,
    `num_dossier` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
    `num_messagerie` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `source` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `avocat` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `l_interet` decimal(15,2) DEFAULT '0.00',
    `montant` decimal(15,2) DEFAULT '0.00',
    `dossier_nombre` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,  -- ✅ NOUVELLE COLONNE
    `decision` text COLLATE utf8mb4_unicode_ci,
    `date_dossier` date DEFAULT NULL,
    `references_messagerie` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `etat_dossier` tinyint(1) DEFAULT '0',
    `remarques` text COLLATE utf8mb4_unicode_ci,
    `statut` enum('prêt','Pas prêt') COLLATE utf8mb4_unicode_ci DEFAULT 'Pas prêt',
    `personne_id` int DEFAULT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

-- ============================================================
-- TABLE : historique_modifications
-- ============================================================

CREATE TABLE `historique_modifications` (
    `id` int NOT NULL,
    `dossier_id` int NOT NULL,
    `personne_id` int DEFAULT NULL,
    `action` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
    `anciennes_valeurs` text COLLATE utf8mb4_unicode_ci,
    `nouvelles_valeurs` text COLLATE utf8mb4_unicode_ci,
    `date_modification` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

-- ============================================================
-- TABLE : sessions
-- ============================================================

CREATE TABLE `sessions` (
    `id` int NOT NULL,
    `token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `personne_id` int NOT NULL,
    `date_creation` datetime DEFAULT CURRENT_TIMESTAMP,
    `date_expiration` datetime DEFAULT NULL,
    `adresse_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `user_agent` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `est_active` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

-- ============================================================
-- VUES
-- ============================================================

-- Vue : v_dossiers_complets
CREATE VIEW `v_dossiers_complets` AS
SELECT 
    d.*,
    p.id as personnel_id,
    p.nom as personnel_nom,
    p.prenom as personnel_prenom,
    p.email as personnel_email,
    CONCAT(p.prenom, ' ', p.nom) as personnel_complet,
    DATE_FORMAT(d.date_dossier, '%d/%m/%Y') as date_dossier_format
FROM dossiers d
LEFT JOIN personnels p ON d.personne_id = p.id;

-- Vue : v_statistiques_dossiers
CREATE VIEW `v_statistiques_dossiers` AS
SELECT 
    statut,
    COUNT(*) as nombre,
    SUM(montant) as total_montant,
    AVG(montant) as montant_moyen,
    MIN(montant) as montant_min,
    MAX(montant) as montant_max,
    SUM(CASE WHEN etat_dossier = TRUE THEN 1 ELSE 0 END) as dossiers_actifs
FROM dossiers
GROUP BY statut;

-- Vue : v_statistiques_globales
CREATE VIEW `v_statistiques_globales` AS
SELECT 
    (SELECT COUNT(*) FROM personnels) as total_personnels,
    (SELECT COUNT(*) FROM dossiers) as total_dossiers,
    (SELECT COUNT(*) FROM dossiers WHERE etat_dossier = TRUE) as dossiers_actifs,
    (SELECT SUM(montant) FROM dossiers) as montant_total,
    (SELECT AVG(montant) FROM dossiers) as montant_moyen,
    (SELECT COUNT(*) FROM dossiers WHERE DATE(date_dossier) = CURDATE()) as dossiers_aujourdhui;

-- Vue : v_dossiers_recents
CREATE VIEW `v_dossiers_recents` AS
SELECT * FROM v_dossiers_complets
WHERE date_dossier >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
ORDER BY date_dossier DESC;

-- --------------------------------------------------------

-- ============================================================
-- COMPTE ADMIN PAR DÉFAUT (Mot de passe : Admin123)
-- ============================================================

INSERT INTO `personnels` (`id`, `nom`, `prenom`, `email`, `mot_de_passe`, `role`, `est_actif`, `date_inscription`, `dernier_acces`, `created_at`, `updated_at`) VALUES
(1, 'Admin', 'Système', 'admin@dossierexpert.com', '$2a$10$NkM2XKJ/k/.QZBmZHHJm8Oe.aF8NPE4oP3SqSGLw.1OxB5fG1HJV2', 'ADMIN', 1, '2026-07-16 11:15:55', NULL, '2026-07-16 10:15:55', '2026-07-16 10:15:55');

-- --------------------------------------------------------

-- ============================================================
-- TRIGGERS
-- ============================================================

DELIMITER $$

-- Trigger : before_insert_dossiers
CREATE TRIGGER `before_insert_dossiers` 
BEFORE INSERT ON `dossiers` 
FOR EACH ROW
BEGIN
    IF NEW.num_dossier IS NULL OR NEW.num_dossier = '' THEN
        CALL generer_num_dossier(@new_num);
        SET NEW.num_dossier = @new_num;
    END IF;
    
    IF NEW.date_dossier IS NULL THEN
        SET NEW.date_dossier = CURDATE();
    END IF;
END$$

-- Trigger : after_insert_dossiers
CREATE TRIGGER `after_insert_dossiers` 
AFTER INSERT ON `dossiers` 
FOR EACH ROW
BEGIN
    INSERT INTO historique_modifications (
        dossier_id,
        personne_id,
        action,
        nouvelles_valeurs
    ) VALUES (
        NEW.id,
        NEW.personne_id,
        'CREATION',
        CONCAT('num_dossier:', NEW.num_dossier, 
               '|source:', IFNULL(NEW.source, ''), 
               '|avocat:', IFNULL(NEW.avocat, ''), 
               '|montant:', NEW.montant)
    );
END$$

-- Trigger : after_update_dossiers
CREATE TRIGGER `after_update_dossiers` 
AFTER UPDATE ON `dossiers` 
FOR EACH ROW
BEGIN
    IF OLD.num_dossier != NEW.num_dossier OR 
       OLD.source != NEW.source OR 
       OLD.avocat != NEW.avocat OR 
       OLD.montant != NEW.montant OR 
       OLD.statut != NEW.statut OR
       OLD.etat_dossier != NEW.etat_dossier THEN
        
        INSERT INTO historique_modifications (
            dossier_id,
            personne_id,
            action,
            anciennes_valeurs,
            nouvelles_valeurs
        ) VALUES (
            NEW.id,
            NEW.personne_id,
            'MODIFICATION',
            CONCAT('num_dossier:', OLD.num_dossier, 
                   '|source:', IFNULL(OLD.source, ''), 
                   '|avocat:', IFNULL(OLD.avocat, ''), 
                   '|montant:', OLD.montant,
                   '|statut:', OLD.statut,
                   '|etat:', OLD.etat_dossier),
            CONCAT('num_dossier:', NEW.num_dossier, 
                   '|source:', IFNULL(NEW.source, ''), 
                   '|avocat:', IFNULL(NEW.avocat, ''), 
                   '|montant:', NEW.montant,
                   '|statut:', NEW.statut,
                   '|etat:', NEW.etat_dossier)
        );
    END IF;
END$$

-- Trigger : after_login_update
CREATE TRIGGER `after_login_update` 
AFTER UPDATE ON `sessions` 
FOR EACH ROW
BEGIN
    IF NEW.est_active = TRUE AND OLD.est_active = FALSE THEN
        UPDATE personnels 
        SET dernier_acces = NOW() 
        WHERE id = NEW.personne_id;
    END IF;
END$$

DELIMITER ;

-- ============================================================
-- INDEXES
-- ============================================================

-- Index pour table dossiers
ALTER TABLE `dossiers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `num_dossier` (`num_dossier`),
  ADD KEY `idx_num_dossier` (`num_dossier`),
  ADD KEY `idx_source` (`source`),
  ADD KEY `idx_avocat` (`avocat`),
  ADD KEY `idx_date_dossier` (`date_dossier`),
  ADD KEY `idx_statut` (`statut`),
  ADD KEY `idx_etat_dossier` (`etat_dossier`),
  ADD KEY `idx_personne` (`personne_id`);

-- Index pour table personnels
ALTER TABLE `personnels`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_nom_prenom` (`nom`,`prenom`),
  ADD KEY `idx_est_actif` (`est_actif`);

-- Index pour table historique_modifications
ALTER TABLE `historique_modifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_dossier` (`dossier_id`),
  ADD KEY `idx_personne` (`personne_id`),
  ADD KEY `idx_date_modification` (`date_modification`),
  ADD KEY `idx_action` (`action`);

-- Index pour table sessions
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `idx_token` (`token`),
  ADD KEY `idx_personne_session` (`personne_id`),
  ADD KEY `idx_est_active` (`est_active`),
  ADD KEY `idx_date_expiration` (`date_expiration`);

-- ============================================================
-- AUTO_INCREMENT
-- ============================================================

ALTER TABLE `dossiers`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

ALTER TABLE `historique_modifications`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

ALTER TABLE `personnels`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

ALTER TABLE `sessions`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

-- ============================================================
-- CONTRAINTES DE CLÉS ÉTRANGÈRES
-- ============================================================

ALTER TABLE `dossiers`
  ADD CONSTRAINT `dossiers_ibfk_1` FOREIGN KEY (`personne_id`) REFERENCES `personnels` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `historique_modifications`
  ADD CONSTRAINT `historique_modifications_ibfk_1` FOREIGN KEY (`dossier_id`) REFERENCES `dossiers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `historique_modifications_ibfk_2` FOREIGN KEY (`personne_id`) REFERENCES `personnels` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `sessions`
  ADD CONSTRAINT `sessions_ibfk_1` FOREIGN KEY (`personne_id`) REFERENCES `personnels` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- ============================================================
-- FIN DU SCRIPT
-- ============================================================

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;