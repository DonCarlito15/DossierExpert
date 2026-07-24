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
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);

        // Charger le CSS
        try {
            scene.getStylesheets().add(
                MainApplication.class.getResource("/com/DRJ/dossierexpert/views/css/style.css").toExternalForm()
            );
        } catch (Exception e) {
            // CSS non trouvé, ignorer
        }

        stage.setTitle("خبير الملفات - تسجيل الدخول");
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void changeScene(String fxmlPath, String title, boolean resizable) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource(fxmlPath)
            );
            Scene scene = new Scene(fxmlLoader.load());
            
            try {
                scene.getStylesheets().add(
                    MainApplication.class.getResource("/com/DRJ/dossierexpert/views/css/style.css").toExternalForm()
                );
            } catch (Exception e) {
                // CSS non trouvé, ignorer
            }
            
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setResizable(resizable);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
