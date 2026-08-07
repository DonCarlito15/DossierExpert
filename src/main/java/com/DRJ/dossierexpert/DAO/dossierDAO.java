package com.DRJ.dossierexpert.DAO;

import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DossierDAO {

    private static final String SQL_FIND_ALL = "SELECT * FROM dossiers ORDER BY date_dossier DESC";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM dossiers WHERE id = ?";
    private static final String SQL_FIND_BY_NUM = "SELECT * FROM dossiers WHERE num_dossier = ?";
    private static final String SQL_INSERT = "INSERT INTO dossiers (num_dossier, num_messagerie, source, avocat, l_interet, montant, dossier_nombre, decision, date_dossier, references_messagerie, etat_dossier, remarques, statut, personne_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE dossiers SET num_dossier = ?, num_messagerie = ?, source = ?, avocat = ?, l_interet = ?, montant = ?, dossier_nombre = ?, decision = ?, date_dossier = ?, references_messagerie = ?, etat_dossier = ?, remarques = ?, statut = ?, personne_id = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM dossiers WHERE id = ?";
    private static final String SQL_COUNT = "SELECT COUNT(*) FROM dossiers";

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Dossier> findAll() throws SQLException {
        List<Dossier> dossiers = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_FIND_ALL)) {
            while (rs.next()) {
                dossiers.add(mapResultSetToDossier(rs));
            }
        }
        return dossiers;
    }

    public Dossier findById(Long id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDossier(rs);
                }
            }
        }
        return null;
    }

    public Dossier findByNumDossier(String numDossier) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_NUM)) {
            stmt.setString(1, numDossier);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDossier(rs);
                }
            }
        }
        return null;
    }

    public List<Dossier> findByIds(List<Long> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<Dossier> dossiers = new ArrayList<>();
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        String query = "SELECT * FROM dossiers WHERE id IN (" + placeholders.toString() + ") ORDER BY date_dossier DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < ids.size(); i++) {
                stmt.setLong(i + 1, ids.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dossiers.add(mapResultSetToDossier(rs));
                }
            }
        }
        return dossiers;
    }

    public boolean save(Dossier dossier) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, dossier.getNumDossier());
            stmt.setString(2, dossier.getNumMessagerie());
            stmt.setString(3, dossier.getSource());
            stmt.setString(4, dossier.getAvocat());
            stmt.setBigDecimal(5, dossier.getLInteret() != null ? BigDecimal.valueOf(dossier.getLInteret()) : null);
            stmt.setBigDecimal(6, dossier.getMontant() != null ? BigDecimal.valueOf(dossier.getMontant()) : null);
            stmt.setString(7, dossier.getDossierNombre());
            stmt.setString(8, dossier.getDecision());

            // ✅ Gestion correcte de LocalDate
            LocalDate date = dossier.getDateDossier();
            if (date != null) {
                stmt.setDate(9, Date.valueOf(date));
            } else {
                stmt.setNull(9, Types.DATE);
            }

            stmt.setString(10, dossier.getReferencesMessagerie());
            stmt.setBoolean(11, dossier.isEtatDossier());
            stmt.setString(12, dossier.getRemarques());
            stmt.setString(13, dossier.getStatut());
            stmt.setObject(14, dossier.getPersonneId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        dossier.setId(rs.getLong(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean update(Dossier dossier) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            
            stmt.setString(1, dossier.getNumDossier());
            stmt.setString(2, dossier.getNumMessagerie());
            stmt.setString(3, dossier.getSource());
            stmt.setString(4, dossier.getAvocat());
            stmt.setBigDecimal(5, dossier.getLInteret() != null ? BigDecimal.valueOf(dossier.getLInteret()) : null);
            stmt.setBigDecimal(6, dossier.getMontant() != null ? BigDecimal.valueOf(dossier.getMontant()) : null);
            stmt.setString(7, dossier.getDossierNombre());
            stmt.setString(8, dossier.getDecision());

            // ✅ Gestion correcte de LocalDate
            LocalDate date = dossier.getDateDossier();
            if (date != null) {
                stmt.setDate(9, Date.valueOf(date));
            } else {
                stmt.setNull(9, Types.DATE);
            }

            stmt.setString(10, dossier.getReferencesMessagerie());
            stmt.setBoolean(11, dossier.isEtatDossier());
            stmt.setString(12, dossier.getRemarques());
            stmt.setString(13, dossier.getStatut());
            stmt.setObject(14, dossier.getPersonneId());
            stmt.setLong(15, dossier.getId());

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

    public List<Dossier> searchByField(String field, String value) throws SQLException {
        List<Dossier> dossiers = new ArrayList<>();
        String query = "SELECT * FROM dossiers WHERE " + field + " LIKE ? ORDER BY date_dossier DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + value + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dossiers.add(mapResultSetToDossier(rs));
                }
            }
        }
        return dossiers;
    }

    public List<Dossier> searchWithCriteria(Map<String, String> criteria) throws SQLException {
        List<Dossier> dossiers = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM dossiers WHERE 1=1");
        List<Object> params = new ArrayList<>();
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                query.append(" AND ").append(entry.getKey()).append(" LIKE ?");
                params.add("%" + entry.getValue() + "%");
            }
        }
        query.append(" ORDER BY date_dossier DESC");
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i + 1, (String) params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dossiers.add(mapResultSetToDossier(rs));
                }
            }
        }
        return dossiers;
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

    private Dossier mapResultSetToDossier(ResultSet rs) throws SQLException {
        Dossier dossier = new Dossier();
        dossier.setId(rs.getLong("id"));
        dossier.setNumDossier(rs.getString("num_dossier"));
        dossier.setNumMessagerie(rs.getString("num_messagerie"));
        dossier.setSource(rs.getString("source"));
        dossier.setAvocat(rs.getString("avocat"));
        BigDecimal interet = rs.getBigDecimal("l_interet");
        if (interet != null) dossier.setLInteret(interet.doubleValue());
        BigDecimal montant = rs.getBigDecimal("montant");
        if (montant != null) dossier.setMontant(montant.doubleValue());
        dossier.setDossierNombre(rs.getString("dossier_nombre"));
        dossier.setDecision(rs.getString("decision"));
        
        // ✅ Récupération correcte de LocalDate
        Date date = rs.getDate("date_dossier");
        if (date != null) {
            dossier.setDateDossier(date.toLocalDate());
        } else {
            dossier.setDateDossier(null);
        }
        
        dossier.setReferencesMessagerie(rs.getString("references_messagerie"));
        dossier.setEtatDossier(rs.getBoolean("etat_dossier"));
        dossier.setRemarques(rs.getString("remarques"));
        dossier.setStatut(rs.getString("statut"));
        Long personneId = rs.getLong("personne_id");
        if (!rs.wasNull()) dossier.setPersonneId(personneId);
        dossier.setCreatedAt(rs.getString("created_at"));
        dossier.setUpdatedAt(rs.getString("updated_at"));
        return dossier;
    }
}