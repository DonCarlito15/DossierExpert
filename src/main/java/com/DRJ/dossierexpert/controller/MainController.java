package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.model.Personne;
import com.DRJ.dossierexpert.service.DossierService;
import com.DRJ.dossierexpert.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // ==================== RÉFÉRENCES AUX BARRES ====================
    private TopBarController topBarController;
    private BottomBarController bottomBarController;

    // ==================== TABLEAU ====================
    @FXML private TableView<Dossier> dossierTable;
    @FXML private TableColumn<Dossier, String> numDossierColumn;
    @FXML private TableColumn<Dossier, String> numMessagerieColumn;
    @FXML private TableColumn<Dossier, String> sourceColumn;
    @FXML private TableColumn<Dossier, String> avocatColumn;
    @FXML private TableColumn<Dossier, Double> interetColumn;
    @FXML private TableColumn<Dossier, Double> montantColumn;
    @FXML private TableColumn<Dossier, String> statutColumn;
    @FXML private TableColumn<Dossier, String> dateColumn;
    @FXML private TableColumn<Dossier, String> dateColumn1;
    @FXML private TableColumn<Dossier, String> dateColumn2;
    @FXML private TableColumn<Dossier, String> dateColumn3;

    @FXML private Text countLabel;
    @FXML private TextField searchField;

    // ==================== FORMULAIRE ====================
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

    // ==================== SERVICES ====================
    private DossierService dossierService;
    private ObservableList<Dossier> dossierList;
    private FilteredList<Dossier> filteredData;
    private Personne currentPersonne;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ MainController initialisé");
        dossierService = new DossierService();

        etatComboBox.getItems().addAll("جاهز", "غير جاهز");
        etatComboBox.setValue("جاهز");

        setupTableColumns();
        loadData();

        dossierTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        displayDossierDetails(newValue);
                    }
                }
        );
    }

    // ==================== SETTERS POUR LES BARRES ====================

    public void setTopBarController(TopBarController topBarController) {
        this.topBarController = topBarController;
        System.out.println("✅ topBarController injecté dans MainController");
        updateTopBar();
    }

    public void setBottomBarController(BottomBarController bottomBarController) {
        this.bottomBarController = bottomBarController;
        System.out.println("✅ bottomBarController injecté dans MainController");
        if (bottomBarController != null) {
            bottomBarController.setStatus("✅ جاهز");
        }
    }

    public void setCurrentPersonne(Personne personne) {
        this.currentPersonne = personne;
        System.out.println("✅ Personne reçue dans MainController : " + personne.getPrenom() + " " + personne.getNom());
        updateTopBar();
    }

    public void updateTopBar() {
        if (topBarController != null) {
            System.out.println("🔍 Mise à jour de la TopBar...");

            topBarController.updateDate();

            if (currentPersonne != null) {
                String nomComplet = currentPersonne.getPrenom() + " " + currentPersonne.getNom();
                topBarController.setUserLabel(nomComplet);
                System.out.println("✅ Utilisateur affiché : " + nomComplet);
            } else {
                SessionManager session = SessionManager.getInstance();
                if (session.hasActiveSession()) {
                    Personne p = session.getCurrentPersonne();
                    if (p != null) {
                        String nomComplet = p.getPrenom() + " " + p.getNom();
                        topBarController.setUserLabel(nomComplet);
                        System.out.println("✅ Utilisateur depuis session : " + nomComplet);
                    }
                }
            }
            topBarController.setPageTitle("📋 لوحة التحكم");
        } else {
            System.out.println("⚠️ topBarController est NULL !");
        }
    }

    // ==================== CONFIGURATION DU TABLEAU ====================

    private void setupTableColumns() {
        numDossierColumn.setCellValueFactory(new PropertyValueFactory<>("numDossier"));
        numMessagerieColumn.setCellValueFactory(new PropertyValueFactory<>("numMessagerie"));
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        avocatColumn.setCellValueFactory(new PropertyValueFactory<>("avocat"));
        interetColumn.setCellValueFactory(new PropertyValueFactory<>("lInteret"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("decision"));
        dateColumn1.setCellValueFactory(new PropertyValueFactory<>("dateDossier"));
        dateColumn2.setCellValueFactory(new PropertyValueFactory<>("referencesMessagerie"));
        dateColumn3.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    // ==================== CHARGEMENT DES DONNÉES ====================

    private void loadData() {
        try {
            List<Dossier> dossiers = dossierService.getAllDossiers();
            if (dossiers != null) {
                dossierList = FXCollections.observableArrayList(dossiers);
            } else {
                dossierList = FXCollections.observableArrayList();
            }

            filteredData = new FilteredList<>(dossierList, p -> true);
            SortedList<Dossier> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(dossierTable.comparatorProperty());

            dossierTable.setItems(sortedData);
            updateCount();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ Erreur", "Erreur lors du chargement des données");
        }
    }

    // ==================== AFFICHAGE DES DÉTAILS ====================

    private void displayDossierDetails(Dossier dossier) {
        if (dossier == null) return;

        numDossierField.setText(dossier.getNumDossier());
        numMessagerieField.setText(dossier.getNumMessagerie());
        sourceField.setText(dossier.getSource());
        avocatField.setText(dossier.getAvocat());
        interetField.setText(dossier.getLInteret() != null ? String.valueOf(dossier.getLInteret()) : "");
        montantField.setText(dossier.getMontant() != null ? String.valueOf(dossier.getMontant()) : "");
        referencesField.setText(dossier.getReferencesMessagerie());
        decisionField.setText(dossier.getDecision());
        dateField.setText(dossier.getDateDossier() != null ? dossier.getDateDossier() : "");
        statutField.setText(dossier.getStatut());

        if (dossier.getStatut() != null) {
            etatComboBox.setValue(dossier.getStatut());
        } else {
            etatComboBox.setValue("جاهز");
        }

        remarquesField.setText(dossier.getRemarques());

        if (bottomBarController != null) {
            bottomBarController.setStatus("📄 الملف : " + dossier.getNumDossier());
        }
    }

    // ==================== UPDATE COUNT ====================

    private void updateCount() {
        int count = dossierTable.getItems().size();
        countLabel.setText("عدد الملفات : " + count);
        if (bottomBarController != null) {
            bottomBarController.setInfoWithCount(count);
        }
    }

    // ==================== ACTIONS ====================

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            filteredData.setPredicate(p -> true);
            if (bottomBarController != null) {
                bottomBarController.setStatus("✅ تم عرض جميع الملفات");
            }
        } else {
            filteredData.setPredicate(dossier -> {
                return dossier.getNumDossier().toLowerCase().contains(searchText.toLowerCase()) ||
                        (dossier.getSource() != null && dossier.getSource().toLowerCase().contains(searchText.toLowerCase())) ||
                        (dossier.getAvocat() != null && dossier.getAvocat().toLowerCase().contains(searchText.toLowerCase())) ||
                        (dossier.getNumMessagerie() != null && dossier.getNumMessagerie().toLowerCase().contains(searchText.toLowerCase()));
            });
            if (bottomBarController != null) {
                bottomBarController.setStatus("🔍 نتائج البحث عن : " + searchText);
            }
        }

        updateCount();
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();
        filteredData.setPredicate(p -> true);
        updateCount();
        if (bottomBarController != null) {
            bottomBarController.setStatus("✅ تم إعادة ضبط البحث");
        }
    }

    @FXML
    private void handleSave() {
        if (numDossierField.getText().trim().isEmpty()) {
            showAlert("⚠️ خطأ", "رقم الملف مطلوب");
            return;
        }

        try {
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
            dossier.setDateDossier(dateField.getText().trim());
            dossier.setStatut(etatComboBox.getValue());
            dossier.setRemarques(remarquesField.getText().trim());

            if (currentPersonne != null) {
                dossier.setPersonneId(currentPersonne.getId());
            }

            boolean saved = dossierService.saveDossier(dossier);

            if (saved) {
                if (bottomBarController != null) {
                    bottomBarController.setStatusSuccess("تم حفظ الملف بنجاح");
                }
                showAlert("✅ نجاح", "تم حفظ الملف بنجاح");
                loadData();
                clearForm();
            } else {
                if (bottomBarController != null) {
                    bottomBarController.setStatusError("خطأ في الحفظ");
                }
                showAlert("❌ خطأ", "حدث خطأ أثناء حفظ الملف");
            }
        } catch (NumberFormatException e) {
            if (bottomBarController != null) {
                bottomBarController.setStatusError("تأكد من صحة الأرقام");
            }
            showAlert("❌ خطأ", "تأكد من صحة القيم الرقمية");
        } catch (Exception e) {
            if (bottomBarController != null) {
                bottomBarController.setStatusError(e.getMessage());
            }
            showAlert("❌ خطأ", e.getMessage());
        }
    }

    @FXML
    private void handlePrint() {
        Dossier selectedDossier = dossierTable.getSelectionModel().getSelectedItem();

        if (selectedDossier == null) {
            showAlert("⚠️ تنبيه", "الرجاء اختيار ملف من الجدول");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/DRJ/dossierexpert/views/pages/preview.fxml")
            );
            Scene scene = new Scene(loader.load());

            PdfPreviewController previewController = loader.getController();
            previewController.setDossier(selectedDossier);

            Stage stage = new Stage();
            stage.setTitle("📄 معاينة الملف - " + selectedDossier.getNumDossier());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            if (bottomBarController != null) {
                bottomBarController.setStatus("🖨️ Ouverture de l'aperçu");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("❌ خطأ", "Erreur lors de l'ouverture de l'aperçu");
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        if (bottomBarController != null) {
            bottomBarController.setStatus("🗑️ تم مسح النموذج");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}