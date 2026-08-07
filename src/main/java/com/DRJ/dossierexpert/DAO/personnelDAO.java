package com.DRJ.dossierexpert.DAO;

import com.DRJ.dossierexpert.model.Personne;
import com.DRJ.dossierexpert.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonnelDAO {

    private static final String SQL_FIND_ALL = "SELECT * FROM personnels ORDER BY nom, prenom";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM personnels WHERE id = ?";
    private static final String SQL_FIND_BY_EMAIL = "SELECT * FROM personnels WHERE email = ?";
    private static final String SQL_INSERT = "INSERT INTO personnels (nom, prenom, email, mot_de_passe, telephone, role, est_actif, date_inscription) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE personnels SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, telephone = ?, role = ?, est_actif = ?, dernier_acces = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM personnels WHERE id = ?";
    private static final String SQL_COUNT = "SELECT COUNT(*) FROM personnels";
    private static final String SQL_COUNT_ACTIVE = "SELECT COUNT(*) FROM personnels WHERE est_actif = TRUE";

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Personne> findAll() throws SQLException {
        List<Personne> personnes = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_FIND_ALL)) {
            while (rs.next()) {
                personnes.add(mapResultSetToPersonne(rs));
            }
        }
        return personnes;
    }

    public Personne findById(Long id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPersonne(rs);
                }
            }
        }
        return null;
    }

    public Personne findByEmail(String email) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPersonne(rs);
                }
            }
        }
        return null;
    }

    public boolean save(Personne personne) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, personne.getNom());
            stmt.setString(2, personne.getPrenom());
            stmt.setString(3, personne.getEmail());
            stmt.setString(4, personne.getMotDePasse());
            stmt.setString(5, personne.getTelephone());
            stmt.setString(6, personne.getRole() != null ? personne.getRole() : "UTILISATEUR");
            stmt.setBoolean(7, personne.isEstActif());

            Timestamp dateInscription = personne.getDateInscription() != null
                    ? Timestamp.valueOf(personne.getDateInscription())
                    : Timestamp.valueOf(java.time.LocalDateTime.now());
            stmt.setTimestamp(8, dateInscription);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        personne.setId(rs.getLong(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean update(Personne personne) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, personne.getNom());
            stmt.setString(2, personne.getPrenom());
            stmt.setString(3, personne.getEmail());
            stmt.setString(4, personne.getMotDePasse());
            stmt.setString(5, personne.getTelephone());
            stmt.setString(6, personne.getRole());
            stmt.setBoolean(7, personne.isEstActif());

            Timestamp dernierAcces = personne.getDernierAcces() != null
                    ? Timestamp.valueOf(personne.getDernierAcces())
                    : null;
            stmt.setTimestamp(8, dernierAcces);
            stmt.setLong(9, personne.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean delete(Long id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        return findByEmail(email) != null;
    }

    public boolean existsById(Long id) throws SQLException {
        return findById(id) != null;
    }

    public int count() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_COUNT)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countActive() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_COUNT_ACTIVE)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public void updateDernierAcces(Long id) throws SQLException {
        String query = "UPDATE personnels SET dernier_acces = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public void setActif(Long id, boolean actif) throws SQLException {
        String query = "UPDATE personnels SET est_actif = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, actif);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateRole(Long id, String role) throws SQLException {
        String query = "UPDATE personnels SET role = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, role);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    private Personne mapResultSetToPersonne(ResultSet rs) throws SQLException {
        Personne personne = new Personne();
        personne.setId(rs.getLong("id"));
        personne.setNom(rs.getString("nom"));
        personne.setPrenom(rs.getString("prenom"));
        personne.setEmail(rs.getString("email"));
        personne.setMotDePasse(rs.getString("mot_de_passe"));

        try {
            personne.setTelephone(rs.getString("telephone"));
        } catch (SQLException e) {
            personne.setTelephone(null);
        }

        try {
            personne.setRole(rs.getString("role"));
        } catch (SQLException e) {
            personne.setRole("UTILISATEUR");
        }

        personne.setEstActif(rs.getBoolean("est_actif"));

        try {
            Timestamp dateInscription = rs.getTimestamp("date_inscription");
            if (dateInscription != null) {
                personne.setDateInscription(dateInscription.toLocalDateTime());
            }
        } catch (SQLException e) {
            personne.setDateInscription(java.time.LocalDateTime.now());
        }

        Timestamp dernierAcces = rs.getTimestamp("dernier_acces");
        if (dernierAcces != null) {
            personne.setDernierAcces(dernierAcces.toLocalDateTime());
        }

        try {
            personne.setCreatedAt(rs.getString("created_at"));
        } catch (SQLException e) {
            personne.setCreatedAt(null);
        }

        try {
            personne.setUpdatedAt(rs.getString("updated_at"));
        } catch (SQLException e) {
            personne.setUpdatedAt(null);
        }

        return personne;
    }
}