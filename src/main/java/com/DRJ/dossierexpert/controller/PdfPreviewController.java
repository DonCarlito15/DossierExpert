package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.MainApplication;
import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.model.Personne;
import com.DRJ.dossierexpert.service.DossierService;
import com.DRJ.dossierexpert.service.WordPrintService;
import com.DRJ.dossierexpert.service.WordTemplateService;
import com.DRJ.dossierexpert.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class PdfPreviewController implements Initializable {

    // ==================== RÉFÉRENCES AUX BARRES ====================
    private TopBarController topBarController;
    private BottomBarController bottomBarController;

    // ==================== SERVICES ====================
    private DossierService dossierService;

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

        // Initialisation du service
        dossierService = new DossierService();

        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));
        previewFooterDate.setText("تاريخ الإنشاء : " + dateNow);
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
     * ✅ Exporter en Word à partir du template
     */
    @FXML
    private void handleExport() {
        if (currentDossier == null) {
            showAlert("⚠️ تنبيه", "لا يوجد ملف للتصدير");
            return;
        }

        try {
            if (bottomBarController != null) {
                bottomBarController.showProcessing("جاري التصدير...");
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le rapport");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Word Document", "*.docx")
            );
            fileChooser.setInitialFileName("dossier_" + currentDossier.getNumDossier() + ".docx");

            File file = fileChooser.showSaveDialog(dossierNumberLabel.getScene().getWindow());

            if (file != null) {
                WordTemplateService templateService = WordTemplateService.getInstance();
                boolean success = templateService.generateWordFromTemplate(
                        currentDossier,
                        file.getAbsolutePath()
                );

                if (success) {
                    WordPrintService printService = new WordPrintService();
                    printService.openWordFile(file.getAbsolutePath());

                    if (bottomBarController != null) {
                        bottomBarController.setStatusSuccess("تم تصدير الملف بنجاح");
                    }
                    showAlert("✅ نجاح", "تم تصدير الملف بنجاح !\n" + file.getAbsolutePath());
                } else {
                    showAlert("❌ خطأ", "Erreur lors de la génération du fichier Word");
                }
            } else {
                if (bottomBarController != null) {
                    bottomBarController.setStatus("⏹️ Annulé par l'utilisateur");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (bottomBarController != null) {
                bottomBarController.setStatusError("Erreur: " + e.getMessage());
            }
            showAlert("❌ خطأ", "Erreur lors de l'export: " + e.getMessage());
        }
    }

    /**
     * ✅ Impression directe via Word
     */
    @FXML
    private void handlePrint() {
        if (currentDossier == null) {
            showAlert("⚠️ تنبيه", "لا يوجد ملف للطباعة");
            return;
        }

        try {
            if (bottomBarController != null) {
                bottomBarController.showProcessing("جاري الطباعة...");
            }

            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "dossier_" + currentDossier.getNumDossier() + ".docx";
            String filePath = tempDir + File.separator + fileName;

            WordTemplateService templateService = WordTemplateService.getInstance();
            boolean success = templateService.generateWordFromTemplate(
                    currentDossier,
                    filePath
            );

            if (success) {
                WordPrintService printService = new WordPrintService();
                boolean printed = printService.printWordFile(filePath);

                if (printed) {
                    if (bottomBarController != null) {
                        bottomBarController.setStatusSuccess("تم طباعة الملف بنجاح");
                    }
                    showAlert("✅ نجاح", "تم طباعة الملف بنجاح");
                } else {
                    printService.openWordFile(filePath);
                    showAlert("ℹ️ Information", "Le fichier Word a été ouvert, vous pouvez l'imprimer manuellement");
                }
            } else {
                showAlert("❌ خطأ", "Erreur lors de la génération du fichier Word");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (bottomBarController != null) {
                bottomBarController.setStatusError("Erreur: " + e.getMessage());
            }
            showAlert("❌ خطأ", "Erreur lors de l'impression: " + e.getMessage());
        }
    }

    /**
     * ✅ Exporter en image PNG (Alternative)
     */
    @FXML
    private void handleExportAsImage() {
        if (currentDossier == null) {
            showAlert("⚠️ تنبيه", "لا يوجد ملف للتصدير");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter le dossier en image");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Image", "*.png")
            );
            fileChooser.setInitialFileName("dossier_" + currentDossier.getNumDossier() + ".png");

            File file = fileChooser.showSaveDialog(dossierNumberLabel.getScene().getWindow());

            if (file != null) {
                if (bottomBarController != null) {
                    bottomBarController.showProcessing("جاري التصدير...");
                }

                WritableImage snapshot = printNode.snapshot(new SnapshotParameters(), null);

                BufferedImage bufferedImage = new BufferedImage(
                        (int) snapshot.getWidth(),
                        (int) snapshot.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );

                ImageIO.write(bufferedImage, "png", file);

                if (bottomBarController != null) {
                    bottomBarController.setStatusSuccess("تم تصدير الملف كصورة");
                }
                showAlert("✅ نجاح", "تم تصدير الملف كصورة بنجاح !\n" + file.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (bottomBarController != null) {
                bottomBarController.setStatusError("Erreur: " + e.getMessage());
            }
            showAlert("❌ خطأ", "Erreur lors de l'export: " + e.getMessage());
        }
    }

    /**
     * ✅ Envoyer une notification (génère un Word depuis Dossiertemplate.docx)
     */
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

            SessionManager session = SessionManager.getInstance();
            Personne destinataire = session.getCurrentPersonne();

            if (destinataire == null) {
                showAlert("⚠️ تنبيه", "Aucun destinataire trouvé");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer la notification");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Word Document", "*.docx")
            );
            fileChooser.setInitialFileName("notification_" + currentDossier.getNumDossier() + ".docx");

            File file = fileChooser.showSaveDialog(dossierNumberLabel.getScene().getWindow());

            if (file != null) {
                WordTemplateService templateService = WordTemplateService.getInstance();
                boolean success = templateService.generateWordFromTemplate(
                        currentDossier,
                        "templates/Dossiertemplate.docx",
                        file.getAbsolutePath()
                );

                if (success) {
                    WordPrintService printService = new WordPrintService();
                    printService.openWordFile(file.getAbsolutePath());

                    if (bottomBarController != null) {
                        bottomBarController.setStatusSuccess("تم إرسال الإشعار بنجاح");
                    }
                    showAlert("✅ نجاح", "تم إرسال الإشعار بنجاح !\n" + file.getAbsolutePath());
                } else {
                    showAlert("❌ خطأ", "Erreur lors de la génération de la notification");
                }
            } else {
                if (bottomBarController != null) {
                    bottomBarController.setStatus("⏹️ Annulé par l'utilisateur");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (bottomBarController != null) {
                bottomBarController.setStatusError("Erreur: " + e.getMessage());
            }
            showAlert("❌ خطأ", "Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }

    // ================================================================
    // ✅ SUPPRESSION DE DOSSIER
    // ================================================================

    /**
     * ✅ Supprimer le dossier définitivement
     */
    @FXML
    private void handleDelete() {
        if (currentDossier == null) {
            showAlert("⚠️ تنبيه", "لا يوجد ملف للحذف");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تأكيد الحذف");
        alert.setHeaderText("⚠️ حذف نهائي");
        alert.setContentText("هل أنت متأكد de vouloir supprimer définitivement le dossier " + currentDossier.getNumDossier() + " ?\nCette action est irréversible !");

        ButtonType supprimerButton = new ButtonType("🗑️ Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType annulerButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(supprimerButton, annulerButton);

        try {
            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/com/DRJ/dossierexpert/css/style.css").toExternalForm()
            );
        } catch (Exception e) {
            // CSS non trouvé
        }

        if (alert.showAndWait().get() == supprimerButton) {
            try {
                boolean deleted = dossierService.deleteDossier(currentDossier.getId());

                if (deleted) {
                    if (bottomBarController != null) {
                        bottomBarController.setStatusSuccess("تم حذف الملف نهائياً");
                    }
                    showAlert("✅ نجاح", "تم حذف الملف نهائياً");

                    // Fermer la fenêtre d'aperçu
                    Stage stage = (Stage) dossierNumberLabel.getScene().getWindow();
                    stage.close();

                } else {
                    showAlert("❌ خطأ", "Impossible de supprimer le fichier");
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (bottomBarController != null) {
                    bottomBarController.setStatusError("Erreur: " + e.getMessage());
                }
                showAlert("❌ خطأ", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    // ================================================================
    // ✅ FERMETURE DE LA FENÊTRE
    // ================================================================

    /**
     * ✅ Ferme la fenêtre d'aperçu (appelée par le bouton "✖ إغلاق")
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) dossierNumberLabel.getScene().getWindow();
        stage.close();
        System.out.println("✅ Fenêtre d'aperçu fermée");
    }

    /**
     * ✅ Ferme la fenêtre d'aperçu (alias pour compatibilité)
     */
    @FXML
    private void handleBack() {
        handleClose();
    }

    // ================================================================
    // FIN DES MODIFICATIONS
    // ================================================================

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تأكيد الخروج");
        alert.setHeaderText("تسجيل الخروج");
        alert.setContentText("هل أنت متأكد من رغبتك في تسجيل الخروج ؟");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            SessionManager session = SessionManager.getInstance();
            session.destroySession();

            Stage stage = (Stage) dossierNumberLabel.getScene().getWindow();
            MainApplication.logoutAndExit(stage);
            System.out.println("✅ Déconnexion réussie");
        }
    }

    // ==================== AFFICHAGE ALERT ====================

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        try {
            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/com/DRJ/dossierexpert/css/style.css").toExternalForm()
            );
        } catch (Exception e) {
            // CSS non trouvé
        }

        alert.showAndWait();
    }

    // ==================== GETTERS ====================

    public Dossier getCurrentDossier() {
        return currentDossier;
    }
}