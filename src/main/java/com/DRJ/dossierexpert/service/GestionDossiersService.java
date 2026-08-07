package com.DRJ.dossierexpert.service;

import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.DAO.DossierDAO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionDossiersService {

    private final DossierDAO dossierDAO;

    public GestionDossiersService() {
        this.dossierDAO = new DossierDAO();
    }

    /**
     * Génère des statistiques sur les dossiers
     */
    public Map<String, Object> generateStatistics() {
        Map<String, Object> stats = new HashMap<>();
        try {
            List<Dossier> dossiers = dossierDAO.findAll();
            
            int total = dossiers.size();
            int actifs = 0;
            int nonActifs = 0;
            int prets = 0;
            int nonPrets = 0;
            double montantTotal = 0;
            double interetTotal = 0;
            
            for (Dossier d : dossiers) {
                if (d.isEtatDossier()) actifs++;
                else nonActifs++;
                
                if (d.isPret()) prets++;
                else nonPrets++;
                
                if (d.getMontant() != null) montantTotal += d.getMontant();
                if (d.getLInteret() != null) interetTotal += d.getLInteret();
            }
            
            stats.put("total", total);
            stats.put("actifs", actifs);
            stats.put("nonActifs", nonActifs);
            stats.put("prets", prets);
            stats.put("nonPrets", nonPrets);
            stats.put("montantTotal", montantTotal);
            stats.put("interetTotal", interetTotal);
            stats.put("montantMoyen", total > 0 ? montantTotal / total : 0);
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la génération des statistiques: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Exporte les dossiers en CSV
     */
    public boolean exportToCSV(String filePath) {
        try {
            List<Dossier> dossiers = dossierDAO.findAll();
            if (dossiers.isEmpty()) {
                System.err.println("⚠️ Aucun dossier à exporter");
                return false;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Numéro Dossier,Source,Avocat,Montant,Statut,Date,Intérêt,Décision,Références\n");

            for (Dossier d : dossiers) {
                sb.append(escapeCSV(d.getNumDossier())).append(",");
                sb.append(escapeCSV(d.getSource())).append(",");
                sb.append(escapeCSV(d.getAvocat())).append(",");
                sb.append(d.getMontant() != null ? d.getMontant() : 0).append(",");
                sb.append(escapeCSV(d.getStatut())).append(",");
                sb.append(escapeCSV(d.getDateDossierAsString())).append(",");
                sb.append(d.getLInteret() != null ? d.getLInteret() : 0).append(",");
                sb.append(escapeCSV(d.getDecision())).append(",");
                sb.append(escapeCSV(d.getReferencesMessagerie())).append("\n");
            }

            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), sb.toString().getBytes());
            System.out.println("✅ Export CSV réussi: " + filePath);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'export CSV: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}