package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.model.Personne;
import com.DRJ.dossierexpert.service.DossierService;
import com.DRJ.dossierexpert.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DossierFormController implements Initializable {

    // ==================== RÉFÉRENCES ====================
    private MainController mainController;
    private Dossier currentDossier;
    private DossierService dossierService;

    // ==================== FXML COMPOSANTS ====================
    @FXML private Text formTitle;
    @FXML private TextField numDossierField;
    @FXML private TextField numMessagerieField;
    @FXML private TextField sourceField;
    @FXML private TextField avocatField;
    @FXML private TextField interetField;
    @FXML private TextField montantField;
    @FXML private TextField referencesField;
    @FXML private TextField decisionField;
    @FXML private TextField dateField;
    @FXML private TextField statutField;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private TextArea remarquesField;
    @FXML private Label statusLabel;

    // ==================== BOUTONS ====================
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button printButton;

    private boolean isNewDossier = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dossierService = new DossierService();

        // Remplir le ComboBox
        etatComboBox.getItems().addAll("جاهز", "غير جاهز");
        etatComboBox.setValue("جاهز");
        remarquesField.setStyle("-fx-text-alignment: right;");

        System.out.println("✅ DossierFormController initialisé");
    }

    /**
     * Définit le dossier à modifier
     */
    public void setDossier(Dossier dossier) {
        if (dossier != null) {
            this.currentDossier = dossier;
            this.isNewDossier = false;
            displayDossierData(dossier);
            formTitle.setText("📝 تعديل الملف - " + dossier.getNumDossier());
        } else {
            this.isNewDossier = true;
            formTitle.setText("📝 إضافة ملف جديد");
        }
    }

    /**
     * Définit le contrôleur principal
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Affiche les données du dossier dans le formulaire
     */
    private void displayDossierData(Dossier dossier) {
        numDossierField.setText(dossier.getNumDossier());
        numMessagerieField.setText(dossier.getNumMessagerie());
        sourceField.setText(dossier.getSource());
        avocatField.setText(dossier.getAvocat());
        interetField.setText(dossier.getLInteret() != null ? String.valueOf(dossier.getLInteret()) : "");
        montantField.setText(dossier.getMontant() != null ? String.valueOf(dossier.getMontant()) : "");
        referencesField.setText(dossier.getReferencesMessagerie());
        decisionField.setText(dossier.getDecision());
        
        // ✅ Afficher la date LocalDate correctement
        if (dossier.getDateDossier() != null) {
            dateField.setText(dossier.getDateDossier().toString());
        } else {
            dateField.setText("");
        }

        if (dossier.getStatut() != null) {
            if ("prêt".equals(dossier.getStatut())) {
                etatComboBox.setValue("جاهز");
            } else {
                etatComboBox.setValue("غير جاهز");
            }
        } else {
            etatComboBox.setValue("جاهز");
        }

        remarquesField.setText(dossier.getRemarques());
        statutField.setText(dossier.getStatut());
    }

    /**
     * Récupère les données du formulaire
     */
    private Dossier getDossierFromForm() {
        Dossier dossier = new Dossier();
        dossier.setNumDossier(numDossierField.getText().trim());
        dossier.setNumMessagerie(numMessagerieField.getText().trim());
        dossier.setSource(sourceField.getText().trim());
        dossier.setAvocat(avocatField.getText().trim());

        if (!interetField.getText().trim().isEmpty()) {
            dossier.setLInteret(Double.parseDouble(interetField.getText().trim()));
        }
        if (!montantField.getText().trim().isEmpty()) {
            dossier.setMontant(Double.parseDouble(montantField.getText().trim()));
        }

        dossier.setReferencesMessagerie(referencesField.getText().trim());
        dossier.setDecision(decisionField.getText().trim());
        
        // ✅ Gérer la date depuis le champ texte
        String dateStr = dateField.getText().trim();
        if (!dateStr.isEmpty()) {
            try {
                dossier.setDateDossier(LocalDate.parse(dateStr));
            } catch (Exception e) {
                dossier.setDateDossier(LocalDate.now());
            }
        } else {
            dossier.setDateDossier(LocalDate.now());
        }

        String statutValue = etatComboBox.getValue();
        if ("جاهز".equals(statutValue)) {
            dossier.setStatut("prêt");
        } else {
            dossier.setStatut("Pas prêt");
        }

        dossier.setRemarques(remarquesField.getText().trim());

        // Récupérer l'utilisateur connecté
        SessionManager session = SessionManager.getInstance();
        if (session.hasActiveSession()) {
            Personne personne = session.getCurrentPersonne();
            if (personne != null) {
                dossier.setPersonneId(personne.getId());
            }
        }

        return dossier;
    }

    /**
     * Valide les champs du formulaire
     */
    private boolean validateForm() {
        if (numDossierField.getText().trim().isEmpty()) {
            showStatus("⚠️ رقم الملف مطلوب", "error");
            numDossierField.requestFocus();
            return false;
        }

        if (sourceField.getText().trim().isEmpty()) {
            showStatus("⚠️ المصدر مطلوب", "error");
            sourceField.requestFocus();
            return false;
        }

        if (avocatField.getText().trim().isEmpty()) {
            showStatus("⚠️ المحامي مطلوب", "error");
            avocatField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Affiche un message de statut
     */
    private void showStatus(String message, String type) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            if ("error".equals(type)) {
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            } else if ("success".equals(type)) {
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            } else {
                statusLabel.setStyle("-fx-text-fill: #3498db;");
            }
            statusLabel.setVisible(true);
        }
    }

    // ==================== ACTIONS ====================

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            Dossier dossier = getDossierFromForm();

            // Si c'est une modification, garder l'ID
            if (!isNewDossier && currentDossier != null) {
                dossier.setId(currentDossier.getId());
            }

            boolean saved = dossierService.saveDossier(dossier);

            if (saved) {
                showStatus("✅ تم حفظ الملف بنجاح", "success");
                if (mainController != null) {
                    mainController.loadData(); // Rafraîchir le tableau
                }
                if (!isNewDossier) {
                    handleClose();
                } else {
                    clearForm();
                }
            } else {
                showStatus("❌ خطأ أثناء حفظ الملف", "error");
            }

        } catch (NumberFormatException e) {
            showStatus("❌ تأكد من صحة القيم الرقمية (الفائدة والمبلغ)", "error");
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("❌ " + e.getMessage(), "error");
        }
    }

    @FXML
    private void handlePrint() {
        if (currentDossier != null) {
            try {
                if (mainController != null) {
                    mainController.handlePrint();
                }
                handleClose();
            } catch (Exception e) {
                e.printStackTrace();
                showStatus("❌ Erreur lors de l'impression", "error");
            }
        } else {
            showStatus("⚠️ Enregistrez d'abord le fichier", "error");
        }
    }

    @FXML
    private void handleCancel() {
        if (hasUnsavedChanges()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("تأكيد");
            alert.setHeaderText("تغييرات غير محفوظة");
            alert.setContentText("هل أنت متأكد de vouloir fermer sans enregistrer ?");
            if (alert.showAndWait().get() != ButtonType.OK) {
                return;
            }
        }
        handleClose();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    // ================================================================
    // ✅ SUPPRESSION DÉFINITIVE
    // ================================================================

    @FXML
    private void handleClear() {
        // Si c'est un nouveau dossier (non sauvegardé), juste effacer les champs
        if (isNewDossier || currentDossier == null) {
            clearForm();
            showStatus("🗑️ تم مسح النموذج", "info");
            return;
        }

        // ✅ Pour un dossier existant : SUPPRESSION DÉFINITIVE
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تأكيد الحذف");
        alert.setHeaderText("⚠️ حذف نهائي");
        alert.setContentText("هل أنت متأكد de vouloir supprimer définitivement le dossier " + currentDossier.getNumDossier() + " ?\nCette action est irréversible !");

        ButtonType supprimerButton = new ButtonType("🗑️ Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType annulerButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(supprimerButton, annulerButton);

        if (alert.showAndWait().get() == supprimerButton) {
            try {
                boolean deleted = dossierService.deleteDossier(currentDossier.getId());
                if (deleted) {
                    showStatus("✅ تم حذف الملف نهائياً", "success");
                    if (mainController != null) {
                        mainController.loadData(); // Rafraîchir le tableau
                    }
                    handleClose(); // Fermer le formulaire
                } else {
                    showStatus("❌ Impossible de supprimer le fichier", "error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showStatus("❌ Erreur lors de la suppression : " + e.getMessage(), "error");
            }
        }
    }

    private void clearForm() {
        numDossierField.clear();
        numMessagerieField.clear();
        sourceField.clear();
        avocatField.clear();
        interetField.clear();
        montantField.clear();
        referencesField.clear();
        decisionField.clear();
        dateField.clear();
        statutField.clear();
        etatComboBox.setValue("جاهز");
        remarquesField.clear();
    }

    private boolean hasUnsavedChanges() {
        Dossier dossier = getDossierFromForm();
        return dossier != null &&
                (!dossier.getNumDossier().isEmpty() ||
                        !dossier.getSource().isEmpty() ||
                        !dossier.getAvocat().isEmpty());
    }
}