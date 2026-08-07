package com.DRJ.dossierexpert;

import com.DRJ.dossierexpert.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainApplication extends Application {

    private static Stage primaryStage;
    private static boolean shuttingDown = false;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Charger la fenêtre de login
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource("/com/DRJ/dossierexpert/views/pages/login.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);

        // Charger le CSS
        try {
            scene.getStylesheets().add(
                    MainApplication.class.getResource("/com/DRJ/dossierexpert/views/css/style.css").toExternalForm()
            );
        } catch (Exception e) {
            System.out.println("⚠️ CSS non trouvé, continuation sans style...");
        }

        // Configuration du stage
        stage.setTitle("خبير الملفات - تسجيل الدخول");
        stage.setScene(scene);
        stage.setWidth(1280);
        stage.setHeight(800);
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.setOnCloseRequest(event -> {
            event.consume();
            closeApplication();
        });

        // ✅ Animation de transition
        FadeTransition ft = new FadeTransition(Duration.millis(500), scene.getRoot());
        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        stage.show();
        ft.play();

        System.out.println("✅ Application lancée avec succès !");
    }

    /**
     * Change la scène vers une nouvelle vue avec animation
     */
    public static void changeScene(String fxmlPath, String title, boolean resizable) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource(fxmlPath)
            );
            Scene scene = new Scene(fxmlLoader.load(), 1400, 900);

            // Charger le CSS
            try {
                scene.getStylesheets().add(
                        MainApplication.class.getResource("/com/DRJ/dossierexpert/views/css/style.css").toExternalForm()
                );
            } catch (Exception e) {
                // CSS non trouvé
            }

            // ✅ Animation de transition
            FadeTransition ft = new FadeTransition(Duration.millis(400), scene.getRoot());
            ft.setFromValue(0.0);
            ft.setToValue(1.0);

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setResizable(resizable);

            if (resizable) {
                primaryStage.setMaximized(true);
            }

            ft.play();
            primaryStage.show();

            System.out.println("✅ Changement de page vers : " + fxmlPath);

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de la page : " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void changeScene(String fxmlPath, String title) {
        changeScene(fxmlPath, title, true);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void closeApplication() {
        if (shuttingDown) {
            return;
        }
        shuttingDown = true;
        if (primaryStage != null) {
            primaryStage.close();
        }
        Platform.exit();
        System.exit(0);
    }

    public static void showLoginScene(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource("/com/DRJ/dossierexpert/views/pages/login.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1280, 800);

            try {
                scene.getStylesheets().add(
                        MainApplication.class.getResource("/com/DRJ/dossierexpert/views/css/style.css").toExternalForm()
                );
            } catch (Exception e) {
                System.out.println("⚠️ CSS non trouvé, continuation sans style...");
            }

            if (stage != null) {
                stage.setTitle("خبير الملفات - تسجيل الدخول");
                stage.setScene(scene);
                stage.setWidth(1280);
                stage.setHeight(800);
                stage.setMinWidth(1100);
                stage.setMinHeight(720);
                stage.centerOnScreen();
                stage.setResizable(true);
                stage.setMaximized(false);
                stage.show();
            }

            FadeTransition ft = new FadeTransition(Duration.millis(500), scene.getRoot());
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

            System.out.println("✅ Retour vers l’écran de connexion");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour vers la page de connexion");
            e.printStackTrace();
        }
    }

    public static void logoutAndShowLogin(Stage stage) {
        SessionManager session = SessionManager.getInstance();
        session.destroySession();
        showLoginScene(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}