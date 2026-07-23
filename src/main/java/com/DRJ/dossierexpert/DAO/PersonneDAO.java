package com.DRJ.dossierexpert.DAO;

import com.DRJ.dossierexpert.model.Personne;
import com.DRJ.dossierexpert.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la table 'personnes'
 *
 * @author DossierExpert
 * @version 1.0
 */
public class PersonneDAO {

    // ==================== CONSTANTES SQL ====================

    private static final String TABLE_NAME = "personnes";

    // Requêtes SQL
    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME + " (nom, prenom, email, mot_de_passe, telephone, role, est_actif, date_inscription) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE " + TABLE_NAME + " SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, telephone = ?, " +
                    "role = ?, est_actif = ?, dernier_acces = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT * FROM " + TABLE_NAME + " WHERE email = ?";

    private static final String SQL_FIND_ALL =
            "SELECT * FROM " + TABLE_NAME + " ORDER BY nom, prenom";

    private static final String SQL_FIND_ACTIVE =
            "SELECT * FROM " + TABLE_NAME + " WHERE est_actif = TRUE ORDER BY nom, prenom";

    private static final String SQL_SEARCH =
            "SELECT * FROM " + TABLE_NAME + " WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ?";

    private static final String SQL_COUNT =
            "SELECT COUNT(*) FROM " + TABLE_NAME;

    private static final String SQL_COUNT_ACTIVE =
            "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE est_actif = TRUE";

    // ==================== CONNEXION ====================

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ==================== CRUD ====================

    /**
     * Enregistre une nouvelle personne dans la base de données
     *
     * @param personne La personne à enregistrer
     * @return La personne avec son ID généré
     * @throws SQLException En cas d'erreur SQL
     */
    public Personne save(Personne personne) throws SQLException {
        String query = SQL_INSERT;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, personne.getNom());
            stmt.setString(2, personne.getPrenom());
            stmt.setString(3, personne.getEmail());
            stmt.setString(4, personne.getMotDePasse());
            stmt.setString(5, personne.getTelephone());
            stmt.setString(6, personne.getRole() != null ? personne.getRole() : "UTILISATEUR");
            stmt.setBoolean(7, personne.isEstActif());
            stmt.setTimestamp(8, Timestamp.valueOf(personne.getDateInscription() != null ?
                    personne.getDateInscription() : LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de l'enregistrement, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    personne.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Échec de l'enregistrement, aucun ID généré.");
                }
            }

            return personne;
        }
    }

    /**
     * Met à jour une personne existante
     *
     * @param personne La personne à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws SQLException En cas d'erreur SQL
     */
    public boolean update(Personne personne) throws SQLException {
        String query = SQL_UPDATE;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, personne.getNom());
            stmt.setString(2, personne.getPrenom());
            stmt.setString(3, personne.getEmail());
            stmt.setString(4, personne.getMotDePasse());
            stmt.setString(5, personne.getTelephone());
            stmt.setString(6, personne.getRole());
            stmt.setBoolean(7, personne.isEstActif());

            if (personne.getDernierAcces() != null) {
                stmt.setTimestamp(8, Timestamp.valueOf(personne.getDernierAcces()));
            } else {
                stmt.setNull(8, Types.TIMESTAMP);
            }

            stmt.setLong(9, personne.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Supprime une personne par son ID
     *
     * @param id L'ID de la personne à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException En cas d'erreur SQL
     */
    public boolean delete(Long id) throws SQLException {
        String query = SQL_DELETE;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Supprime une personne
     *
     * @param personne La personne à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException En cas d'erreur SQL
     */
    public boolean delete(Personne personne) throws SQLException {
        return delete(personne.getId());
    }

    // ==================== RECHERCHES ====================

    /**
     * Trouve une personne par son ID
     *
     * @param id L'ID de la personne
     * @return La personne trouvée ou null
     * @throws SQLException En cas d'erreur SQL
     */
    public Personne findById(Long id) throws SQLException {
        String query = SQL_FIND_BY_ID;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPersonne(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve une personne par son email
     *
     * @param email L'email de la personne
     * @return La personne trouvée ou null
     * @throws SQLException En cas d'erreur SQL
     */
    public Personne findByEmail(String email) throws SQLException {
        String query = SQL_FIND_BY_EMAIL;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPersonne(rs);
                }
            }
        }
        return null;
    }

    /**
     * Récupère toutes les personnes
     *
     * @return Liste de toutes les personnes
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Personne> findAll() throws SQLException {
        List<Personne> personnes = new ArrayList<>();
        String query = SQL_FIND_ALL;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                personnes.add(mapResultSetToPersonne(rs));
            }
        }
        return personnes;
    }

    /**
     * Récupère toutes les personnes actives
     *
     * @return Liste des personnes actives
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Personne> findActive() throws SQLException {
        List<Personne> personnes = new ArrayList<>();
        String query = SQL_FIND_ACTIVE;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                personnes.add(mapResultSetToPersonne(rs));
            }
        }
        return personnes;
    }

    /**
     * Recherche des personnes par nom, prénom ou email
     *
     * @param searchTerm Le terme de recherche
     * @return Liste des personnes correspondantes
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Personne> search(String searchTerm) throws SQLException {
        List<Personne> personnes = new ArrayList<>();
        String query = SQL_SEARCH;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    personnes.add(mapResultSetToPersonne(rs));
                }
            }
        }
        return personnes;
    }

    /**
     * Vérifie si une personne existe avec cet email
     *
     * @param email L'email à vérifier
     * @return true si l'email existe
     * @throws SQLException En cas d'erreur SQL
     */
    public boolean existsByEmail(String email) throws SQLException {
        return findByEmail(email) != null;
    }

    /**
     * Vérifie si une personne existe avec cet ID
     *
     * @param id L'ID à vérifier
     * @return true si l'ID existe
     * @throws SQLException En cas d'erreur SQL
     */
    public boolean existsById(Long id) throws SQLException {
        return findById(id) != null;
    }

    // ==================== STATISTIQUES ====================

    /**
     * Compte le nombre total de personnes
     *
     * @return Nombre total de personnes
     * @throws SQLException En cas d'erreur SQL
     */
    public int count() throws SQLException {
        String query = SQL_COUNT;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Compte le nombre de personnes actives
     *
     * @return Nombre de personnes actives
     * @throws SQLException En cas d'erreur SQL
     */
    public int countActive() throws SQLException {
        String query = SQL_COUNT_ACTIVE;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ==================== MÉTHODES DE MAPPING ====================

    /**
     * Convertit un ResultSet en objet Personne
     *
     * @param rs Le ResultSet
     * @return L'objet Personne
     * @throws SQLException En cas d'erreur SQL
     */
    private Personne mapResultSetToPersonne(ResultSet rs) throws SQLException {
        Personne personne = new Personne();

        personne.setId(rs.getLong("id"));
        personne.setNom(rs.getString("nom"));
        personne.setPrenom(rs.getString("prenom"));
        personne.setEmail(rs.getString("email"));
        personne.setMotDePasse(rs.getString("mot_de_passe"));
        personne.setTelephone(rs.getString("telephone"));
        personne.setRole(rs.getString("role"));
        personne.setEstActif(rs.getBoolean("est_actif"));

        // Dates
        Timestamp dateInscription = rs.getTimestamp("date_inscription");
        if (dateInscription != null) {
            personne.setDateInscription(dateInscription.toLocalDateTime());
        }

        Timestamp dernierAcces = rs.getTimestamp("dernier_acces");
        if (dernierAcces != null) {
            personne.setDernierAcces(dernierAcces.toLocalDateTime());
        }

        personne.setCreatedAt(rs.getString("created_at"));
        personne.setUpdatedAt(rs.getString("updated_at"));

        return personne;
    }

    // ==================== MÉTHODES SPÉCIALES ====================

    /**
     * Met à jour le dernier accès d'une personne
     *
     * @param id L'ID de la personne
     * @throws SQLException En cas d'erreur SQL
     */
    public void updateDernierAcces(Long id) throws SQLException {
        String query = "UPDATE " + TABLE_NAME + " SET dernier_acces = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Active ou désactive une personne
     *
     * @param id L'ID de la personne
     * @param actif Nouvel état
     * @throws SQLException En cas d'erreur SQL
     */
    public void setActif(Long id, boolean actif) throws SQLException {
        String query = "UPDATE " + TABLE_NAME + " SET est_actif = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBoolean(1, actif);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Change le rôle d'une personne
     *
     * @param id L'ID de la personne
     * @param role Nouveau rôle
     * @throws SQLException En cas d'erreur SQL
     */
    public void updateRole(Long id, String role) throws SQLException {
        String query = "UPDATE " + TABLE_NAME + " SET role = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, role);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Change le mot de passe d'une personne
     *
     * @param id L'ID de la personne
     * @param nouveauMotDePasse Nouveau mot de passe
     * @throws SQLException En cas d'erreur SQL
     */
    public void updateMotDePasse(Long id, String nouveauMotDePasse) throws SQLException {
        String query = "UPDATE " + TABLE_NAME + " SET mot_de_passe = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nouveauMotDePasse);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    // ==================== MÉTHODES DE VALIDATION ====================

    /**
     * Valide une personne avant enregistrement
     *
     * @param personne La personne à valider
     * @throws IllegalArgumentException Si la personne n'est pas valide
     */
    public void validate(Personne personne) {
        if (personne.getNom() == null || personne.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (personne.getPrenom() == null || personne.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        if (personne.getEmail() == null || personne.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        if (personne.getMotDePasse() == null || personne.getMotDePasse().length() < 8) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères");
        }
        if (!personne.hasValidEmail()) {
            throw new IllegalArgumentException("L'email n'est pas valide");
        }
    }
}