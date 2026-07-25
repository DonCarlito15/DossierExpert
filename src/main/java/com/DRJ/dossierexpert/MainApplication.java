package com.DRJ.dossierexpert;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Charger la fenêtre de login
        FXMLLoader fxmlLoader = new FXMLLoader(
            MainApplication.class.getResource("/com/DRJ/dossierexpert/views/pages/login.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());

        // Charger le CSS
        try {
            scene.getStylesheets().add(
                MainApplication.class.getResource("/com/DRJ/dossierexpert/css/style.css").toExternalForm()
            );
        } catch (Exception e) {
            // CSS non trouvé, ignorer
            System.out.println("⚠️ CSS non trouvé, continuation sans style...");
        }

        // Configuration du stage
        stage.setTitle("خبير الملفات - تسجيل الدخول");
        stage.setScene(scene);
        stage.setMaximized(true);      // Ouvre en plein écran
        stage.setResizable(true);      // Permet de redimensionner
        stage.show();

        System.out.println("✅ Application lancée avec succès !");
    }

    /**
     * Change la scène vers une nouvelle vue
     * 
     * @param fxmlPath Chemin vers le fichier FXML
     * @param title Titre de la fenêtre
     * @param resizable Permet le redimensionnement
     */
    public static void changeScene(String fxmlPath, String title, boolean resizable) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource(fxmlPath)
            );
            Scene scene = new Scene(fxmlLoader.load());
            
            // Charger le CSS
            try {
                scene.getStylesheets().add(
                    MainApplication.class.getResource("/com/DRJ/dossierexpert/css/style.css").toExternalForm()
                );
            } catch (Exception e) {
                // CSS non trouvé, ignorer
            }
            
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setResizable(resizable);
            
            // Si resizable est true, maximiser la fenêtre
            if (resizable) {
                primaryStage.setMaximized(true);
            }
            
            primaryStage.show();
            
            System.out.println("✅ Changement de page vers : " + fxmlPath);
            
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de la page : " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Change la scène vers une nouvelle vue (version simplifiée)
     * 
     * @param fxmlPath Chemin vers le fichier FXML
     * @param title Titre de la fenêtre
     */
    public static void changeScene(String fxmlPath, String title) {
        changeScene(fxmlPath, title, true);
    }

    /**
     * Retourne le stage principal
     * 
     * @return Le stage principal
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Ferme l'application
     */
    public static void closeApplication() {
        if (primaryStage != null) {
            primaryStage.close();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}