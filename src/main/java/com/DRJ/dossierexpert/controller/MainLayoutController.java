package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.MainApplication;
import com.DRJ.dossierexpert.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainLayoutController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(MainLayoutController.class.getName());

    @FXML private BorderPane mainLayout;
    @FXML private TopBarController topBarController;
    @FXML private BottomBarController bottomBarController;

    private String currentPage = "";
    private Object currentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ MainLayoutController initialisé");

        if (topBarController != null) {
            topBarController.setMainLayoutController(this);
            System.out.println("✅ topBarController injecté dans MainLayoutController");
        } else {
            System.out.println("❌ topBarController est NULL !");
        }

        // Charger la page par défaut
        setPageContent("/com/DRJ/dossierexpert/views/pages/main.fxml", "📋 لوحة التحكم");
    }

    public void setPageContent(String fxmlPath, String title) {
        try {
            System.out.println("🔍 Chargement de : " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();

            Object controller = loader.getController();
            this.currentController = controller;

            // Injecter les barres dans le contrôleur de la page
            if (controller != null) {
                injectBarControllers(controller);
            }

            // Mettre à jour le titre
            if (topBarController != null) {
                topBarController.setPageTitle(title);
            }

            mainLayout.setCenter(content);
            currentPage = fxmlPath;

            LOGGER.info("Page chargée : " + fxmlPath);
            System.out.println("✅ Page chargée : " + fxmlPath);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la page : " + fxmlPath, e);
            setStatusError("⚠️ Erreur de chargement de la page");
            e.printStackTrace();
        }
    }

    private void injectBarControllers(Object controller) {
        System.out.println("🔍 Injection des barres dans : " + controller.getClass().getSimpleName());

        if (controller instanceof MainController) {
            MainController mainCtrl = (MainController) controller;
            mainCtrl.setTopBarController(topBarController);
            mainCtrl.setBottomBarController(bottomBarController);
            System.out.println("✅ Barres injectées dans MainController");
        } else if (controller instanceof SearchController) {
            SearchController searchCtrl = (SearchController) controller;
            searchCtrl.setTopBarController(topBarController);
            searchCtrl.setBottomBarController(bottomBarController);
            System.out.println("✅ Barres injectées dans SearchController");
        } else {
            System.out.println("⚠️ Contrôleur non reconnu : " + controller.getClass().getSimpleName());
        }
    }

    // ==================== NAVIGATION ====================

    public void navigateToMain() {
        setPageContent("/com/DRJ/dossierexpert/views/pages/main.fxml", "📋 لوحة التحكم");
        setStatusInfo("📋 Page principale");
    }

    public void navigateToSearch() {
        setPageContent("/com/DRJ/dossierexpert/views/pages/search.fxml", "🔍 Recherche");
        setStatusInfo("🔍 Recherche de dossiers");
    }

    // ==================== GESTION DU STATUT ====================

    public void setStatus(String status) {
        if (bottomBarController != null) {
            bottomBarController.setStatus(status);
        }
    }

    public void setStatusSuccess(String message) {
        if (bottomBarController != null) {
            bottomBarController.setStatusSuccess(message);
        }
    }

    public void setStatusError(String message) {
        if (bottomBarController != null) {
            bottomBarController.setStatusError(message);
        }
    }

    public void setStatusInfo(String message) {
        if (bottomBarController != null) {
            bottomBarController.setStatusInfo(message);
        }
    }

    public void setStatusWarning(String message) {
        if (bottomBarController != null) {
            bottomBarController.setStatusWarning(message);
        }
    }

    public void setInfo(String info) {
        if (bottomBarController != null) {
            bottomBarController.setInfo(info);
        }
    }

    public void setInfoWithCount(int count) {
        if (bottomBarController != null) {
            bottomBarController.setInfoWithCount(count);
        }
    }

    // ==================== DÉCONNEXION ====================

    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تأكيد الخروج");
        alert.setHeaderText("تسجيل الخروج");
        alert.setContentText("هل أنت متأكد من رغبتك في تسجيل الخروج ؟");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            SessionManager session = SessionManager.getInstance();
            session.destroySession();
            System.out.println("✅ Session détruite");

            Stage stage = (Stage) mainLayout.getScene().getWindow();
            MainApplication.logoutAndExit(stage);
            LOGGER.info("Déconnexion réussie");
        }
    }

    // ==================== GETTERS ====================

    public BorderPane getMainLayout() {
        return mainLayout;
    }

    public TopBarController getTopBarController() {
        return topBarController;
    }

    public BottomBarController getBottomBarController() {
        return bottomBarController;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public Object getCurrentController() {
        return currentController;
    }
}