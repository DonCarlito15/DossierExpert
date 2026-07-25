package com.DRJ.dossierexpert.service;

import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.DAO.DossierDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DossierService {

    private final DossierDAO dossierDAO;

    public DossierService() {
        this.dossierDAO = new DossierDAO();
    }

    // ==================== CRUD ====================

    /**
     * Récupère tous les dossiers de la base de données
     *
     * @return Liste de tous les dossiers
     */
    public List<Dossier> getAllDossiers() {
        try {
            return dossierDAO.findAll();
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des dossiers: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Récupère un dossier par son ID
     *
     * @param id ID du dossier
     * @return Le dossier trouvé ou null
     */
    public Dossier getDossierById(Long id) {
        try {
            return dossierDAO.findById(id);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du dossier: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Récupère un dossier par son numéro
     *
     * @param numDossier Numéro du dossier
     * @return Le dossier trouvé ou null
     */
    public Dossier getDossierByNum(String numDossier) {
        try {
            return dossierDAO.findByNumDossier(numDossier);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du dossier: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sauvegarde un dossier (insertion ou mise à jour)
     *
     * @param dossier Le dossier à sauvegarder
     * @return true si la sauvegarde a réussi
     */
    public boolean saveDossier(Dossier dossier) {
        try {
            if (dossier.getId() == null || dossier.getId() == 0) {
                // Nouveau dossier
                return dossierDAO.save(dossier);
            } else {
                // Mise à jour
                return dossierDAO.update(dossier);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la sauvegarde du dossier: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un dossier par son ID
     *
     * @param id ID du dossier à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteDossier(Long id) {
        try {
            return dossierDAO.delete(id);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du dossier: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un dossier
     *
     * @param dossier Le dossier à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteDossier(Dossier dossier) {
        if (dossier == null || dossier.getId() == null) {
            return false;
        }
        return deleteDossier(dossier.getId());
    }

    /**
     * Compte le nombre total de dossiers
     *
     * @return Nombre de dossiers
     */
    public int countDossiers() {
        try {
            return dossierDAO.count();
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du comptage des dossiers: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // ==================== RECHERCHE ====================

    /**
     * Recherche des dossiers par critère
     *
     * @param criteria Le critère de recherche (nom du champ)
     * @param value La valeur à rechercher
     * @return Liste des dossiers correspondants
     */
    public List<Dossier> searchDossiers(String criteria, String value) {
        try {
            String field = mapCriteriaToField(criteria);
            if (field == null) {
                System.err.println("⚠️ Critère de recherche non reconnu: " + criteria);
                return new ArrayList<>();
            }
            return dossierDAO.searchByField(field, value);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche des dossiers: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Recherche des dossiers avec plusieurs critères
     *
     * @param criteria Map des critères (nom du champ -> valeur)
     * @return Liste des dossiers correspondants
     */
    public List<Dossier> searchDossiers(Map<String, String> criteria) {
        try {
            Map<String, String> mappedCriteria = new HashMap<>();
            for (Map.Entry<String, String> entry : criteria.entrySet()) {
                String field = mapCriteriaToField(entry.getKey());
                if (field != null && entry.getValue() != null && !entry.getValue().isEmpty()) {
                    mappedCriteria.put(field, entry.getValue());
                }
            }
            return dossierDAO.searchWithCriteria(mappedCriteria);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche des dossiers: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Mappe un critère en français/arabe vers un champ de la base de données
     *
     * @param criteria Le critère
     * @return Le nom du champ dans la base
     */
    private String mapCriteriaToField(String criteria) {
        if (criteria == null) return null;

        switch (criteria) {
            // Français
            case "Num dossier":
            case "num_dossier":
            case "رقم الملف":
                return "num_dossier";

            case "Num messagerie":
            case "num_messagerie":
            case "رقم المراسلة":
                return "num_messagerie";

            case "Source":
            case "source":
            case "المصدر":
                return "source";

            case "Avocat":
            case "avocat":
            case "المحامي":
                return "avocat";

            case "Montant":
            case "montant":
            case "المبلغ":
                return "montant";

            case "Statut":
            case "statut":
            case "الحالة":
                return "statut";

            case "Date":
            case "date":
            case "date_dossier":
            case "التاريخ":
                return "date_dossier";

            case "Décision":
            case "decision":
            case "القرار":
                return "decision";

            case "Références":
            case "references":
            case "references_messagerie":
            case "مراجع المراسلة":
                return "references_messagerie";

            default:
                return criteria;
        }
    }

    // ==================== IMPRESSION ====================

    /**
     * Imprime un dossier
     *
     * @param dossier Le dossier à imprimer
     * @return true si l'impression a réussi
     */
    public boolean printDossier(Dossier dossier) {
        if (dossier == null) {
            System.err.println("❌ Impossible d'imprimer un dossier null");
            return false;
        }

        try {
            // Logique d'impression
            System.out.println("🖨️ Impression du dossier: " + dossier.getNumDossier());
            System.out.println("   Titre: " + dossier.getSource());
            System.out.println("   Avocat: " + dossier.getAvocat());
            System.out.println("   Montant: " + dossier.getMontant());

            // Ici, vous pouvez ajouter la génération PDF
            // generatePDF(dossier);

            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'impression: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Imprime plusieurs dossiers
     *
     * @param dossiers Liste des dossiers à imprimer
     * @return true si toutes les impressions ont réussi
     */
    public boolean printDossiers(List<Dossier> dossiers) {
        if (dossiers == null || dossiers.isEmpty()) {
            System.err.println("⚠️ Aucun dossier à imprimer");
            return false;
        }

        boolean success = true;
        for (Dossier dossier : dossiers) {
            if (!printDossier(dossier)) {
                success = false;
            }
        }
        return success;
    }

    // ==================== EXPORT ====================

    /**
     * Exporte les dossiers en CSV
     *
     * @param dossiers Liste des dossiers à exporter
     * @param filePath Chemin du fichier
     * @return true si l'export a réussi
     */
    public boolean exportToCSV(List<Dossier> dossiers, String filePath) {
        if (dossiers == null || dossiers.isEmpty()) {
            System.err.println("⚠️ Aucun dossier à exporter");
            return false;
        }

        try {
            StringBuilder sb = new StringBuilder();
            // En-tête
            sb.append("Numéro Dossier,Source,Avocat,Montant,Statut,Date,Intérêt,Décision,Références\n");

            // Données
            for (Dossier d : dossiers) {
                sb.append(d.getNumDossier()).append(",");
                sb.append(d.getSource() != null ? d.getSource() : "").append(",");
                sb.append(d.getAvocat() != null ? d.getAvocat() : "").append(",");
                sb.append(d.getMontant() != null ? d.getMontant() : 0).append(",");
                sb.append(d.getStatut() != null ? d.getStatut() : "").append(",");
                sb.append(d.getDateDossier() != null ? d.getDateDossier() : "").append(",");
                sb.append(d.getLInteret() != null ? d.getLInteret() : 0).append(",");
                sb.append(d.getDecision() != null ? d.getDecision() : "").append(",");
                sb.append(d.getReferencesMessagerie() != null ? d.getReferencesMessagerie() : "").append("\n");
            }

            // Écrire dans le fichier
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), sb.toString().getBytes());
            System.out.println("✅ Export CSV réussi: " + filePath);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'export CSV: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== STATISTIQUES ====================

    /**
     * Calcule le montant total de tous les dossiers
     *
     * @param dossiers Liste des dossiers
     * @return Montant total
     */
    public double getTotalMontant(List<Dossier> dossiers) {
        if (dossiers == null || dossiers.isEmpty()) {
            return 0;
        }

        double total = 0;
        for (Dossier d : dossiers) {
            if (d.getMontant() != null) {
                total += d.getMontant();
            }
        }
        return total;
    }

    /**
     * Calcule la moyenne des montants
     *
     * @param dossiers Liste des dossiers
     * @return Moyenne des montants
     */
    public double getAverageMontant(List<Dossier> dossiers) {
        if (dossiers == null || dossiers.isEmpty()) {
            return 0;
        }
        return getTotalMontant(dossiers) / dossiers.size();
    }

    /**
     * Compte les dossiers par statut
     *
     * @param dossiers Liste des dossiers
     * @param statut Le statut à compter
     * @return Nombre de dossiers avec ce statut
     */
    public int countByStatut(List<Dossier> dossiers, String statut) {
        if (dossiers == null || dossiers.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Dossier d : dossiers) {
            if (d.getStatut() != null && d.getStatut().equals(statut)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Récupère les dossiers d'une personne spécifique
     *
     * @param personneId ID de la personne
     * @return Liste des dossiers de la personne
     */
    public List<Dossier> getDossiersByPersonne(Long personneId) {
        try {
            // TODO: Implémenter la recherche par personne
            return dossierDAO.findAll(); // Temporaire
        } catch (SQLException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}