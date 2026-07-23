package com.DRJ.dossierexpert.service;

import com.DRJ.dossierexpert.DAO.PersonneDAO;
import com.DRJ.dossierexpert.model.Personne;
import com.DRJ.dossierexpert.utils.SessionManager;

public class AuthentificationService {

    private static AuthentificationService instance;
    private final PersonneDAO personneDAO;
    private final SessionManager sessionManager;
    private final PersonneDAO PersonneDAO;

    private AuthentificationService(PersonneDAO personneDAO) {
        this.personneDAO = personneDAO;
        this.PersonneDAO = new PersonneDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public static AuthentificationService getInstance() {
        if (instance == null) {
            instance = new AuthentificationService(new PersonneDAO());
        }
        return instance;
    }

    public Personne verifierIdentite(String email, String motDePasse) {
        // Récupérer le personnel par email
        Personne personne = personneDAO.findByEmail(email);

        if (personne != null) {
            // Vérifier le mot de passe (avec BCrypt)
            if (personne.getMotDePasse() != null &&
                    personne.getMotDePasse().equals(motDePasse)) {
                return personne;
            }
        }
        return null;
    }

    public void fermerConnexion() {
        // Logique de fermeture
    }
}