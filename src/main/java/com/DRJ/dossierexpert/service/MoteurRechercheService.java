package com.DRJ.dossierexpert.service;

import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.DAO.DossierDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoteurRechercheService {

    private final DossierDAO dossierDAO;

    public MoteurRechercheService() {
        this.dossierDAO = new DossierDAO();
    }

    /**
     * Recherche multicritères avancée
     */
    public List<Dossier> rechercheAvancee(String motCle, String source, String avocat, 
                                          String statut, String dateDebut, String dateFin,
                                          Double montantMin, Double montantMax) {
        try {
            Map<String, String> criteria = new HashMap<>();
            
            if (motCle != null && !motCle.trim().isEmpty()) {
                // Recherche dans plusieurs champs
                return rechercheParMotCle(motCle);
            }
            
            if (source != null && !source.trim().isEmpty()) {
                criteria.put("source", source);
            }
            if (avocat != null && !avocat.trim().isEmpty()) {
                criteria.put("avocat", avocat);
            }
            if (statut != null && !statut.trim().isEmpty()) {
                criteria.put("statut", statut);
            }
            
            // TODO: Ajouter la recherche par date et montant avec des requêtes personnalisées
            
            return dossierDAO.searchWithCriteria(criteria);
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche avancée: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Recherche par mot-clé dans plusieurs champs
     */
    public List<Dossier> rechercheParMotCle(String motCle) {
        List<Dossier> results = new ArrayList<>();
        try {
            String[] champs = {"num_dossier", "source", "avocat", "num_messagerie", "references_messagerie"};
            for (String champ : champs) {
                List<Dossier> resultats = dossierDAO.searchByField(champ, motCle);
                for (Dossier d : resultats) {
                    if (!results.contains(d)) {
                        results.add(d);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche par mot-clé: " + e.getMessage());
        }
        return results;
    }

    /**
     * Filtre les dossiers par statut
     */
    public List<Dossier> filtrerParStatut(String statut) {
        try {
            Map<String, String> criteria = new HashMap<>();
            criteria.put("statut", statut);
            return dossierDAO.searchWithCriteria(criteria);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du filtrage par statut: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Filtre les dossiers par période
     */
    public List<Dossier> filtrerParPeriode(String dateDebut, String dateFin) {
        // TODO: Implémenter la recherche par période
        try {
            return dossierDAO.findAll();
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du filtrage par période: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}