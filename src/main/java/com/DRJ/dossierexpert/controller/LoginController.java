package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.model.Personne;
import com.DRJ.dossierexpert.service.AuthentificationService;
import com.DRJ.dossierexpert.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button closeButton;
    @FXML private Label errorLabel;
    @FXML private Hyperlink forgotPasswordLink;

    private AuthentificationService authService;
    private SessionManager sessionManager;

    // ==================== EMAIL CONFIGURATION ====================
    // ⚠️ REMPLACEZ PAR VOTRE EMAIL GMAIL (celui qui enverra les notifications)
    private static final String SENDER_EMAIL = "Aymenmallah2001@gmail.com";     // Votre email Gmail
    private static final String SENDER_PASSWORD = "zziq iqnp vukm mwiy "; // Mot de passe d'application Gmail
    
    // ✅ DESTINATAIRES : VOUS ET VOTRE COLLÈGUE (les développeurs)
    private static final String RECIPIENT_1 = "Aymenmallah2001@gmail.com";
    private static final String RECIPIENT_2 = "Houdatafaye@gmail.com";

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
    private void handleClose() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Fermer l'application");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment quitter ?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            com.DRJ.dossierexpert.MainApplication.closeApplication();
        }
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

    // ================================================================
    // ✅ MOT DE PASSE OUBLIÉ - AVEC CHAMP EMAIL UTILISATEUR
    // L'email est envoyé DE l'utilisateur VERS les développeurs
    // ================================================================

    /**
     * ✅ Affiche une pop-up avec un champ pour que l'utilisateur entre son email
     * et envoie une notification aux développeurs (vous et votre collègue)
     */
    @FXML
    private void handleForgotPassword() {
        // Créer un Dialog personnalisé
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("🔒 استعادة كلمة المرور");
        dialog.setHeaderText(null);
        dialog.setResizable(false);
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        
        // Appliquer le CSS
        try {
            dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/DRJ/dossierexpert/views/css/style.css").toExternalForm()
            );
        } catch (Exception e) {
            // CSS non trouvé
        }

        // ==================== CONTENU RTL ====================
        VBox content = new VBox();
        content.setSpacing(12.0);
        content.setPadding(new Insets(20, 25, 15, 25));
        content.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        // Titre
        Text title = new Text("📋 استعادة كلمة المرور");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #2c3e50;");

        // Sous-titre
        Text subtitle = new Text("نسيت كلمة المرور");
        subtitle.setStyle("-fx-font-size: 14px; -fx-fill: #7f8c8d; -fx-padding: 0 0 10 0;");

        // Message
        Text message = new Text("الرجاء إدخال بريدك الإلكتروني لإرسال طلب استعادة كلمة المرور :");
        message.setStyle("-fx-font-size: 13px; -fx-fill: #34495e;");

        // ✅ CHAMP EMAIL UTILISATEUR
        TextField userEmailField = new TextField();
        userEmailField.setPromptText("example@email.com");
        userEmailField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #bdc3c7; -fx-border-width: 1.5; -fx-padding: 8 12 8 12; -fx-font-size: 13px; -fx-pref-width: 280px;");
        userEmailField.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        // Ligne de séparation
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #ecf0f1;");

        // Note
        Text note = new Text("💡 سيتم إرسال إشعار إلى المسؤولين وسيتم التواصل معكم في أقرب وقت ممكن.");
        note.setStyle("-fx-font-size: 12px; -fx-fill: #7f8c8d; -fx-font-style: italic;");

        content.getChildren().addAll(
            title, 
            subtitle,
            new Label(" "),
            message,
            new Label(" "),
            userEmailField,
            new Label(" "),
            separator,
            new Label(" "),
            note
        );

        dialog.getDialogPane().setContent(content);

        // ==================== BOUTONS ====================
        ButtonType sendButton = new ButtonType("📧 إرسال الطلب", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButton = new ButtonType("❌ إغلاق", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButton, closeButton);

        // Styliser les boutons
        Button sendBtn = (Button) dialog.getDialogPane().lookupButton(sendButton);
        sendBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        sendBtn.setOnMouseEntered(e -> sendBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20 8 20; -fx-cursor: hand;"));
        sendBtn.setOnMouseExited(e -> sendBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20 8 20; -fx-cursor: hand;"));

        Button closeBtn = (Button) dialog.getDialogPane().lookupButton(closeButton);
        closeBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20 8 20; -fx-cursor: hand;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20 8 20; -fx-cursor: hand;"));

        // ==================== RÉSULTAT ====================
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButton) {
                String userEmail = userEmailField.getText().trim();
                if (userEmail.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("⚠️ تنبيه");
                    alert.setHeaderText(null);
                    alert.setContentText("⚠️ الرجاء إدخال بريدك الإلكتروني");
                    alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    alert.showAndWait();
                    return null;
                }
                if (!isValidEmail(userEmail)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("⚠️ تنبيه");
                    alert.setHeaderText(null);
                    alert.setContentText("⚠️ الرجاء إدخال بريد إلكتروني صحيح");
                    alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    alert.showAndWait();
                    return null;
                }
                sendPasswordRecoveryEmail(userEmail);
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * ✅ Envoie un email de demande de récupération de mot de passe
     * DE l'utilisateur VERS les développeurs (vous et votre collègue)
     * @param userEmail L'email de l'utilisateur qui demande la récupération
     */
    private void sendPasswordRecoveryEmail(String userEmail) {
        // Afficher un indicateur de chargement
        Alert loadingAlert = new Alert(Alert.AlertType.INFORMATION);
        loadingAlert.setTitle("⏳ جاري الإرسال");
        loadingAlert.setHeaderText(null);
        loadingAlert.setContentText("⏳ جاري إرسال طلب استعادة كلمة المرور...");
        loadingAlert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        loadingAlert.show();

        try {
            // Configuration SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");

            // Créer une session avec les identifiants de l'expéditeur (vous)
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            // Créer le message
            MimeMessage message = new MimeMessage(session);
            
            // ✅ L'expéditeur est l'utilisateur qui a demandé la récupération
            message.setFrom(new InternetAddress(userEmail));
            
            // ✅ Les destinataires sont vous et votre collègue (les développeurs)
            message.setRecipients(Message.RecipientType.TO, 
                InternetAddress.parse(RECIPIENT_1 + ", " + RECIPIENT_2));
            
            // ✅ Répondre à l'utilisateur
            message.setReplyTo(InternetAddress.parse(userEmail));
            
            message.setSubject("🔒 طلب استعادة كلمة المرور - نظام خبير الملفات");

            // ==================== CORPS DU MESSAGE (ARABE) ====================
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

            String messageBody = """
                بسم الله الرحمن الرحیم

                📋 طلب استعادة كلمة المرور

                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                📌 تم تقديم طلب استعادة كلمة المرور من قبل المستخدم التالي:

                📧 البريد الإلكتروني : %s

                📅 تاريخ الطلب : %s

                ⏰ وقت الطلب : %s

                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                🔑 يرجى التواصل مع المستخدم لتوفير كلمة مرور جديدة.

                📧 للرد على المستخدم : %s

                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                هذا البريد تم إرساله تلقائياً من نظام خبير الملفات.

                مع خالص التحية،
                فريق خبير الملفات
                """.formatted(userEmail, currentDate, currentTime, userEmail);

            message.setText(messageBody, "UTF-8");

            // Envoyer le message
            Transport.send(message);

            // Fermer l'alert de chargement
            loadingAlert.close();

            // Afficher un message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("✅ تم الإرسال");
            successAlert.setHeaderText(null);
            successAlert.setContentText("✅ تم إرسال طلب استعادة كلمة المرور بنجاح إلى المسؤولين.\nسيتم التواصل معكم في أقرب وقت ممكن.");
            successAlert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            successAlert.showAndWait();

            System.out.println("✅ Email de récupération envoyé de : " + userEmail);
            System.out.println("   Vers : " + RECIPIENT_1 + ", " + RECIPIENT_2);

        } catch (Exception e) {
            // Fermer l'alert de chargement
            loadingAlert.close();

            // Afficher une erreur
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("❌ خطأ");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("❌ حدث خطأ أثناء إرسال الطلب : " + e.getMessage() + "\n\nيرجى التحقق من اتصال الإنترنت أو الاتصال بالمسؤول.");
            errorAlert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            errorAlert.showAndWait();
        }
    }

    // ================================================================
    // FIN DES MODIFICATIONS
    // ================================================================

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