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
    
    public List<Dossier> getAllDossiers() {
        try {
            return dossierDAO.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Dossier getDossierById(Long id) {
        try {
            return dossierDAO.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Dossier getDossierByNum(String numDossier) {
        try {
            return dossierDAO.findByNumDossier(numDossier);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveDossier(Dossier dossier) {
        try {
            if (dossier.getId() == null || dossier.getId() == 0) {
                return dossierDAO.save(dossier);
            } else {
                return dossierDAO.update(dossier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDossier(Long id) {
        try {
            return dossierDAO.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countDossiers() {
        try {
            return dossierDAO.count();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ==================== RECHERCHE ====================
    
    public List<Dossier> searchDossiers(String criteria, String value) {
        try {
            String field = mapCriteriaToField(criteria);
            if (field == null) {
                return new ArrayList<>();
            }
            return dossierDAO.searchByField(field, value);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

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
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private String mapCriteriaToField(String criteria) {
        switch (criteria) {
            case "رقم الملف": return "num_dossier";
            case "رقم المراسلة": return "num_messagerie";
            case "المصدر": return "source";
            case "المحامي": return "avocat";
            case "المبلغ": return "montant";
            case "الحالة": return "statut";
            case "التاريخ": return "date_dossier";
            default: return null;
        }
    }

    // ==================== IMPRESSION ====================
    
    public boolean printDossier(Dossier dossier) {
        // Logique d'impression
        System.out.println("Impression du dossier: " + dossier.getNumDossier());
        return true;
    }

    public boolean printDossiers(List<Dossier> dossiers) {
        for (Dossier dossier : dossiers) {
            printDossier(dossier);
        }
        return true;
    }
}