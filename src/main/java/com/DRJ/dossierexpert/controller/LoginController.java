package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.model.Personne;
import com.DRJ.dossierexpert.service.AuthentificationService;
import com.DRJ.dossierexpert.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private AuthentificationService authService;
    private SessionManager sessionManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = AuthentificationService.getInstance();
        sessionManager = SessionManager.getInstance();

        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        setupKeyboardShortcuts();
        usernameField.requestFocus();
        System.out.println("✅ LoginController initialisé");
    }

    private void setupKeyboardShortcuts() {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (!validateInputs(username, password)) {
            return;
        }

        try {
            loginButton.setDisable(true);
            loginButton.setText("⏳ جاري الاتصال...");

            Personne personne = authService.verifierIdentite(username, password);

            if (personne != null) {
                sessionManager.createSession(personne);
                System.out.println("✅ Utilisateur stocké en session : " + personne.getPrenom() + " " + personne.getNom());
                redirectToMainPage(personne);
            } else {
                showError("❌ البريد الإلكتروني أو كلمة المرور غير صحيحة");
                passwordField.clear();
                passwordField.requestFocus();
            }

        } catch (Exception e) {
            showError("❌ خطأ: " + e.getMessage());
            e.printStackTrace();
        } finally {
            loginButton.setDisable(false);
            loginButton.setText("تسجيل الدخول");
        }
    }

    private boolean validateInputs(String username, String password) {
        if (username.isEmpty() && password.isEmpty()) {
            showError("⚠️ الرجاء إدخال البريد الإلكتروني وكلمة المرور");
            usernameField.requestFocus();
            return false;
        }
        if (username.isEmpty()) {
            showError("⚠️ الرجاء إدخال البريد الإلكتروني");
            usernameField.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            showError("⚠️ الرجاء إدخال كلمة المرور");
            passwordField.requestFocus();
            return false;
        }
        if (!isValidEmail(username)) {
            showError("⚠️ الرجاء إدخال بريد إلكتروني صحيح");
            usernameField.requestFocus();
            usernameField.selectAll();
            return false;
        }
        hideError();
        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private void redirectToMainPage(Personne personne) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/DRJ/dossierexpert/views/pages/main.fxml")
            );
            BorderPane mainRoot = loader.load();

            MainController mainController = loader.getController();
            mainController.setCurrentPersonne(personne);

            Scene scene = new Scene(mainRoot);

            try {
                scene.getStylesheets().add(
                        getClass().getResource("/com/DRJ/dossierexpert/views/css/style.css").toExternalForm()
                );
            } catch (Exception e) {
                // CSS non trouvé
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("📋 خبير الملفات - لوحة التحكم");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setResizable(true);
            stage.show();

            System.out.println("✅ Redirection vers la page principale réussie");

        } catch (IOException e) {
            e.printStackTrace();
            showError("❌ خطأ في تحميل الصفحة الرئيسية");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setText("");
    }
}