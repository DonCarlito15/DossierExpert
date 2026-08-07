package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.MainApplication;
import com.DRJ.dossierexpert.model.Personne;
import com.DRJ.dossierexpert.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TopBarController implements Initializable {

    @FXML private Text pageTitle;
    @FXML private Text dateLabel;
    @FXML private Text userLabel;

    private MainLayoutController mainLayoutController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ TopBarController initialisé");
        updateDate();
        updateUserInfo();
    }

    public void updateDate() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        dateLabel.setText("📅 " + today);
        System.out.println("✅ Date mise à jour : " + today);
    }

    public void updateUserInfo() {
        System.out.println("🔍 updateUserInfo appelé");
        SessionManager session = SessionManager.getInstance();
        if (session.hasActiveSession()) {
            Personne personne = session.getCurrentPersonne();
            if (personne != null) {
                String nomComplet = personne.getPrenom() + " " + personne.getNom();
                userLabel.setText("👤 " + nomComplet);
                System.out.println("✅ Utilisateur affiché : " + nomComplet);
                return;
            }
        }
        userLabel.setText("👤 Utilisateur");
        System.out.println("⚠️ Aucun utilisateur connecté");
    }

    public void setPageTitle(String title) {
        if (pageTitle != null) {
            pageTitle.setText(title);
            System.out.println("✅ Titre mis à jour : " + title);
        }
    }

    public void setPageTitleWithIcon(String icon, String title) {
        if (pageTitle != null) {
            pageTitle.setText(icon + " " + title);
        }
    }

    public String getPageTitle() {
        return pageTitle != null ? pageTitle.getText() : "";
    }

    public void setUserLabel(String user) {
        if (userLabel != null) {
            userLabel.setText("👤 " + user);
            System.out.println("✅ userLabel mis à jour : " + user);
        } else {
            System.out.println("❌ userLabel est NULL !");
        }
    }

    public void setDateLabel(String date) {
        if (dateLabel != null) {
            dateLabel.setText("📅 " + date);
        }
    }

    public void setMainLayoutController(MainLayoutController controller) {
        this.mainLayoutController = controller;
    }

    public void refresh() {
        updateDate();
        updateUserInfo();
    }

    @FXML
    private void handleLogout() {
        if (mainLayoutController != null) {
            mainLayoutController.logout();
        } else {
            SessionManager session = SessionManager.getInstance();
            session.destroySession();
            try {
                javafx.stage.Stage stage = (javafx.stage.Stage) userLabel.getScene().getWindow();
                MainApplication.logoutAndExit(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}