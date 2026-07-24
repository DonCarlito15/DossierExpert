package com.DRJ.dossierexpert.service;

import com.DRJ.dossierexpert.DAO.PersonnelDAO;
import com.DRJ.dossierexpert.model.Personne;
import com.DRJ.dossierexpert.utils.SessionManager;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthentificationService {

    // ==================== LOGGER ====================
    private static final Logger LOGGER = Logger.getLogger(AuthentificationService.class.getName());

    // ==================== SINGLETON ====================
    private static AuthentificationService instance;
    private final PersonnelDAO personnelDAO;
    private final SessionManager sessionManager;

    private AuthentificationService() {
        this.personnelDAO = new PersonnelDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public static AuthentificationService getInstance() {
        if (instance == null) {
            instance = new AuthentificationService();
        }
        return instance;
    }

    // ==================== AUTHENTIFICATION ====================
    
    /**
     * Vérifie les identifiants de l'utilisateur
     * 
     * @param email Email de l'utilisateur
     * @param motDePasse Mot de passe de l'utilisateur
     * @return L'objet Personne si authentifié, sinon null
     */
    public Personne verifierIdentite(String email, String motDePasse) {
        try {
            Personne personne = personnelDAO.findByEmail(email);

            if (personne != null && personne.isEstActif()) {
                // En production, utiliser BCrypt : BCrypt.checkpw(motDePasse, personne.getMotDePasse())
                if (personne.getMotDePasse() != null &&
                    personne.getMotDePasse().equals(motDePasse)) {
                    
                    // Mettre à jour le dernier accès
                    personnelDAO.updateDernierAcces(personne.getId());
                    LOGGER.info("Authentification réussie pour : " + email);
                    return personne;
                }
            }
            LOGGER.warning("Échec d'authentification pour : " + email);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'authentification", e);
        }
        return null;
    }

    /**
     * Vérifie si un email existe dans la base
     * 
     * @param email Email à vérifier
     * @return true si l'email existe
     */
    public boolean verifierEmail(String email) {
        try {
            return personnelDAO.existsByEmail(email);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification de l'email", e);
            return false;
        }
    }

    /**
     * Change le mot de passe d'un utilisateur
     * 
     * @param personneId ID de l'utilisateur
     * @param ancienMotDePasse Ancien mot de passe
     * @param nouveauMotDePasse Nouveau mot de passe
     * @return true si le changement a réussi
     */
    public boolean changerMotDePasse(Long personneId, String ancienMotDePasse, String nouveauMotDePasse) {
        try {
            Personne personne = personnelDAO.findById(personneId);
            if (personne != null && personne.getMotDePasse().equals(ancienMotDePasse)) {
                personne.setMotDePasse(nouveauMotDePasse);
                boolean updated = personnelDAO.update(personne);
                if (updated) {
                    LOGGER.info("Mot de passe changé pour l'utilisateur ID: " + personneId);
                }
                return updated;
            }
            LOGGER.warning("Tentative de changement de mot de passe échouée pour ID: " + personneId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du changement de mot de passe", e);
        }
        return false;
    }

    /**
     * Récupère une personne par son ID
     * 
     * @param id ID de la personne
     * @return L'objet Personne ou null
     */
    public Personne getPersonneById(Long id) {
        try {
            return personnelDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la personne", e);
            return null;
        }
    }

    /**
     * Récupère une personne par son email
     * 
     * @param email Email de la personne
     * @return L'objet Personne ou null
     */
    public Personne getPersonneByEmail(String email) {
        try {
            return personnelDAO.findByEmail(email);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la personne", e);
            return null;
        }
    }

    /**
     * Vérifie si une session est valide
     * 
     * @return true si la session est active
     */
    public boolean isSessionValid() {
        return sessionManager.hasActiveSession();
    }

    /**
     * Récupère l'utilisateur connecté
     * 
     * @return L'utilisateur connecté ou null
     */
    public Personne getCurrentUser() {
        if (sessionManager.hasActiveSession()) {
            return sessionManager.getCurrentPersonne();
        }
        return null;
    }

    /**
     * Ferme la connexion (déconnexion)
     */
    public void fermerConnexion() {
        if (sessionManager.hasActiveSession()) {
            sessionManager.destroySession();
            LOGGER.info("Session fermée");
        }
    }

    /**
     * Déconnecte l'utilisateur
     */
    public void logout() {
        fermerConnexion();
    }
}