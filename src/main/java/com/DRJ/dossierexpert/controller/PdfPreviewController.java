package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PdfPreviewController implements Initializable {

    // ==================== TOP BAR ====================
    @FXML private Text dossierNumberLabel;

    // ==================== APERÇU ====================
    @FXML private Text previewNumDossier;
    @FXML private Text previewNumMessagerie;
    @FXML private Text previewSource;
    @FXML private Text previewAvocat;
    @FXML private Text previewInteret;
    @FXML private Text previewMontant;
    @FXML private Text previewReferences;
    @FXML private Text previewDecision;
    @FXML private Text previewDate;
    @FXML private Text previewStatut;
    @FXML private Text previewStatutDetail;
    @FXML private Text previewRemarques;
    @FXML private Text previewEtat;
    @FXML private Text previewFooterDate;

    private Dossier currentDossier;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Date de création du rapport
        previewFooterDate.setText("تاريخ الإنشاء : " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    /**
     * Définit le dossier à afficher dans l'aperçu
     *
     * @param dossier Le dossier à afficher
     */
    public void setDossier(Dossier dossier) {
        this.currentDossier = dossier;

        if (dossier != null) {
            // Numéro du dossier dans la barre de titre
            dossierNumberLabel.setText("ملف رقم : " + dossier.getNumDossier());

            // Remplir les champs d'aperçu
            previewNumDossier.setText(dossier.getNumDossier() != null ? dossier.getNumDossier() : "—");
            previewNumMessagerie.setText(dossier.getNumMessagerie() != null ? dossier.getNumMessagerie() : "—");
            previewSource.setText(dossier.getSource() != null ? dossier.getSource() : "—");
            previewAvocat.setText(dossier.getAvocat() != null ? dossier.getAvocat() : "—");
            previewInteret.setText(dossier.getLInteret() != null ? String.format("%.2f", dossier.getLInteret()) : "0.00");
            previewMontant.setText(dossier.getMontant() != null ? String.format("%.2f", dossier.getMontant()) : "0.00");
            previewReferences.setText(dossier.getReferencesMessagerie() != null ? dossier.getReferencesMessagerie() : "—");
            previewDecision.setText(dossier.getDecision() != null ? dossier.getDecision() : "—");
            previewDate.setText(dossier.getDateDossier() != null ? dossier.getDateDossier() : "—");
            previewStatutDetail.setText(dossier.getStatut() != null ? dossier.getStatut() : "—");
            previewRemarques.setText(dossier.getRemarques() != null ? dossier.getRemarques() : "لا توجد ملاحظات");
            previewEtat.setText(dossier.isEtatDossier() ? "نشط" : "غير نشط");

            // Couleur du statut
            if ("جاهز".equals(dossier.getStatut())) {
                previewStatut.setStyle("-fx-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 13px;");
                previewStatut.setText("جاهز");
            } else {
                previewStatut.setStyle("-fx-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 13px;");
                previewStatut.setText("غير جاهز");
            }
        }
    }

    // ==================== ACTIONS ====================

    /**
     * Action d'impression du dossier
     */
    @FXML
    private void handlePrint() {
        if (currentDossier == null) {
            showAlert("⚠️ تنبيه", "لا يوجد ملف للطباعة");
            return;
        }

        try {
            // Simuler l'impression
            // Ici vous pouvez appeler votre service d'impression
            // printService.printDossier(currentDossier);

            showAlert("✅ نجاح", "تم إرسال الملف إلى الطابعة بنجاح");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ خطأ", "Erreur lors de l'impression: " + e.getMessage());
        }
    }

    /**
     * Action de notification
     */
    @FXML
    private void handleNotify() {
        if (currentDossier == null) {
            showAlert("⚠️ تنبيه", "لا يوجد ملف للإشعار");
            return;
        }

        try {
            // Simuler l'envoi de notification
            // Ici vous pouvez appeler votre service de notification
            // notificationService.sendNotification(currentDossier);

            showAlert("✅ نجاح", "تم إرسال الإشعار بنجاح");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ خطأ", "Erreur lors de l'envoi de l'notification: " + e.getMessage());
        }
    }

    /**
     * Action d'export du dossier
     */
    @FXML
    private void handleExport() {
        if (currentDossier == null) {
            showAlert("⚠️ تنبيه", "لا يوجد ملف للتصدير");
            return;
        }

        try {
            // Simuler l'export
            // Ici vous pouvez appeler votre service d'export
            // exportService.exportDossier(currentDossier);

            showAlert("✅ نجاح", "تم تصدير الملف بنجاح");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ خطأ", "Erreur lors de l'export: " + e.getMessage());
        }
    }

    /**
     * Retour à la page principale
     */
    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) dossierNumberLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/DRJ/dossierexpert/views/pages/main.fxml")
            );
            Scene scene = new Scene(loader.load());
            stage.setTitle("📋 خبير الملفات - لوحة التحكم");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("❌ خطأ", "Erreur lors du retour à la page principale");
        }
    }

    /**
     * Déconnexion
     */
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تأكيد الخروج");
        alert.setHeaderText("تسجيل الخروج");
        alert.setContentText("هل أنت متأكد من رغبتك في تسجيل الخروج ؟");

        if (alert.showAndWait().get() == ButtonType.OK) {
            // Détruire la session
            SessionManager session = SessionManager.getInstance();
            session.destroySession();

            // Retourner à l'écran de connexion
            try {
                Stage stage = (Stage) dossierNumberLabel.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/DRJ/dossierexpert/views/pages/login.fxml")
                );
                Scene scene = new Scene(loader.load());
                stage.setTitle("خبير الملفات - تسجيل الدخول");
                stage.setScene(scene);
                stage.setMaximized(false);
                stage.setResizable(false);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== AFFICHAGE ALERT ====================

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==================== GETTERS ====================

    public Dossier getCurrentDossier() {
        return currentDossier;
    }
}