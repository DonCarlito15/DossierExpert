package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.utils.SessionManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PdfPreviewController implements Initializable {

    // ==================== RÉFÉRENCES AUX BARRES ====================
    private TopBarController topBarController;
    private BottomBarController bottomBarController;

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
    @FXML private VBox printNode;

    private Dossier currentDossier;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ PdfPreviewController initialisé");

        previewFooterDate.setText("تاريخ الإنشاء : " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    // ==================== SETTERS POUR LES BARRES ====================

    public void setTopBarController(TopBarController topBarController) {
        this.topBarController = topBarController;
        System.out.println("✅ topBarController injecté dans PdfPreviewController");
        updateTopBar();
    }

    public void setBottomBarController(BottomBarController bottomBarController) {
        this.bottomBarController = bottomBarController;
        System.out.println("✅ bottomBarController injecté dans PdfPreviewController");
        if (bottomBarController != null) {
            bottomBarController.setStatus("📄 Aperçu du dossier");
        }
    }

    private void updateTopBar() {
        if (topBarController != null) {
            topBarController.setPageTitle("📄 معاينة الملف");
            topBarController.updateDate();
            topBarController.updateUserInfo();
        }
    }

    public void setDossier(Dossier dossier) {
        this.currentDossier = dossier;
        System.out.println("✅ Dossier reçu dans PdfPreviewController : " + (dossier != null ? dossier.getNumDossier() : "null"));

        if (dossier != null) {
            dossierNumberLabel.setText("ملف رقم : " + dossier.getNumDossier());

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

            if ("prêt".equals(dossier.getStatut())) {
                previewStatut.setStyle("-fx-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 13px;");
                previewStatut.setText("جاهز");
            } else {
                previewStatut.setStyle("-fx-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 13px;");
                previewStatut.setText("غير جاهز");
            }

            if (bottomBarController != null) {
                bottomBarController.setStatus("📄 الملف : " + dossier.getNumDossier());
                bottomBarController.setInfo("ملف : " + dossier.getNumDossier());
            }

            System.out.println("✅ Aperçu chargé pour le dossier : " + dossier.getNumDossier());
        }
    }

    // ==================== ACTIONS ====================

    /**
     * ✅ Export PDF avec snapshot
     */
    @FXML
    private void handlePrint() {
        if (currentDossier == null) {
            showAlert("⚠️ تنبيه", "لا يوجد ملف للتصدير");
            return;
        }

        try {
            if (bottomBarController != null) {
                bottomBarController.showProcessing("جاري التصدير...");
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("تصدير الملف كـ PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            fileChooser.setInitialFileName("dossier_" + currentDossier.getNumDossier() + ".pdf");

            File file = fileChooser.showSaveDialog(dossierNumberLabel.getScene().getWindow());
            if (file == null) {
                if (bottomBarController != null) {
                    bottomBarController.setStatus("⏹️ تم إلغاء التصدير");
                }
                return;
            }

            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }

            // Forcer le layout avant le snapshot
            printNode.applyCss();
            printNode.layout();

            WritableImage snapshot = printNode.snapshot(new SnapshotParameters(), null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 FileOutputStream outputStream = new FileOutputStream(file)) {
                ImageIO.write(bufferedImage, "png", baos);
                baos.flush();

                Document document = new Document(PageSize.A4, 36, 36, 36, 36);
                PdfWriter.getInstance(document, outputStream);
                document.open();

                Image pdfImage = Image.getInstance(baos.toByteArray());
                float availableWidth = PageSize.A4.getWidth() - document.leftMargin() - document.rightMargin();
                float availableHeight = PageSize.A4.getHeight() - document.topMargin() - document.bottomMargin();
                pdfImage.scaleToFit(availableWidth, availableHeight);
                pdfImage.setAlignment(Image.ALIGN_CENTER);

                document.add(pdfImage);
                document.close();
            }

            if (bottomBarController != null) {
                bottomBarController.setStatusSuccess("تم تصدير الملف بنجاح");
            }
            showAlert("✅ نجاح", "تم تصدير الملف بنجاح\n" + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            if (bottomBarController != null) {
                bottomBarController.setStatusError("Erreur: " + e.getMessage());
            }
            showAlert("❌ خطأ", "Erreur lors de l'export: " + e.getMessage());
        }
    }

    /**
     * ✅ Exporter en image (alternative)
     */
    @FXML
    private void handleExportAsImage() {
        if (currentDossier == null) {
            showAlert("⚠️ تنبيه", "لا يوجد ملف للتصدير");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter le dossier");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Image", "*.png")
            );
            fileChooser.setInitialFileName("dossier_" + currentDossier.getNumDossier() + ".png");

            File file = fileChooser.showSaveDialog(dossierNumberLabel.getScene().getWindow());

            if (file != null) {
                if (bottomBarController != null) {
                    bottomBarController.showProcessing("جاري التصدير...");
                }

                // Capturer le noeud
                WritableImage snapshot = printNode.snapshot(new SnapshotParameters(), null);

                // Convertir en BufferedImage
                BufferedImage bufferedImage = new BufferedImage(
                        (int) snapshot.getWidth(),
                        (int) snapshot.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );

                // Sauvegarder
                ImageIO.write(bufferedImage, "png", file);

                if (bottomBarController != null) {
                    bottomBarController.setStatusSuccess("تم تصدير الملف كصورة");
                }
                showAlert("✅ نجاح", "تم تصدير الملف بنجاح !\n" + file.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (bottomBarController != null) {
                bottomBarController.setStatusError("Erreur: " + e.getMessage());
            }
            showAlert("❌ خطأ", "Erreur lors de l'export: " + e.getMessage());
        }
    }

    @FXML
    private void handleNotify() {
        if (currentDossier == null) {
            showAlert("⚠️ تنبيه", "لا يوجد ملف للإشعار");
            return;
        }

        try {
            if (bottomBarController != null) {
                bottomBarController.showProcessing("جاري إرسال الإشعار...");
            }

            if (bottomBarController != null) {
                bottomBarController.setStatusSuccess("تم إرسال الإشعار");
            }
            showAlert("✅ نجاح", "تم إرسال الإشعار بنجاح");

        } catch (Exception e) {
            e.printStackTrace();
            if (bottomBarController != null) {
                bottomBarController.setStatusError("Erreur: " + e.getMessage());
            }
            showAlert("❌ خطأ", "Erreur lors de l'envoi de l'notification: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) dossierNumberLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/DRJ/dossierexpert/views/pages/main.fxml")
            );
            Scene scene = new Scene(loader.load());

            MainController mainController = loader.getController();
            SessionManager session = SessionManager.getInstance();
            if (session.hasActiveSession()) {
                mainController.setCurrentPersonne(session.getCurrentPersonne());
            }

            stage.setTitle("📋 خبير الملفات - لوحة التحكم");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            System.out.println("✅ Retour à la page principale");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("❌ خطأ", "Erreur lors du retour à la page principale");
        }
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تأكيد الخروج");
        alert.setHeaderText("تسجيل الخروج");
        alert.setContentText("هل أنت متأكد من رغبتك في تسجيل الخروج ؟");

        if (alert.showAndWait().get() == ButtonType.OK) {
            SessionManager session = SessionManager.getInstance();
            session.destroySession();

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
                System.out.println("✅ Déconnexion réussie");
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