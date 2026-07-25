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
import javafx.scene.layout.BorderPane;
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

    // ==================== BOUTONS ====================
    @FXML private Button searchButton;
    @FXML private Button refreshButton;

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
    private Stage filterStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ MainController initialisé");
        dossierService = new DossierService();

        etatComboBox.getItems().addAll("جاهز", "غير جاهز");
        etatComboBox.setValue("جاهز");

        setupTableColumns();
        
        // ✅ CHARGEMENT AUTOMATIQUE DES DONNÉES
        loadData();
        System.out.println("✅ Données chargées automatiquement");

        // Double-clic sur le tableau pour ouvrir le formulaire
        dossierTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Dossier selectedDossier = dossierTable.getSelectionModel().getSelectedItem();
                if (selectedDossier != null) {
                    openDossierForm(selectedDossier);
                }
            }
        });

        // Sélection simple pour afficher les détails
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
            topBarController.updateDate();
            if (currentPersonne != null) {
                topBarController.setUserLabel(currentPersonne.getPrenom() + " " + currentPersonne.getNom());
            } else {
                SessionManager session = SessionManager.getInstance();
                if (session.hasActiveSession()) {
                    Personne p = session.getCurrentPersonne();
                    if (p != null) {
                        topBarController.setUserLabel(p.getPrenom() + " " + p.getNom());
                    }
                }
            }
            topBarController.setPageTitle("📋 لوحة التحكم");
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
    
    /**
     * ✅ Charge les données depuis la base de données
     */
    public void loadData() {
        try {
            System.out.println("🔄 Chargement des données...");
            List<Dossier> dossiers = dossierService.getAllDossiers();
            
            if (dossiers != null && !dossiers.isEmpty()) {
                System.out.println("✅ " + dossiers.size() + " dossier(s) chargé(s)");
                if (dossierList == null) {
                    dossierList = FXCollections.observableArrayList(dossiers);
                } else {
                    dossierList.setAll(dossiers);
                }
            } else {
                System.out.println("⚠️ Aucun dossier trouvé dans la base");
                if (dossierList == null) {
                    dossierList = FXCollections.observableArrayList();
                } else {
                    dossierList.clear();
                }
            }

            // Initialiser le filtre si nécessaire
            if (filteredData == null) {
                filteredData = new FilteredList<>(dossierList, p -> true);
            } else {
                filteredData = new FilteredList<>(dossierList, p -> true);
            }

            SortedList<Dossier> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(dossierTable.comparatorProperty());

            dossierTable.setItems(sortedData);
            updateCount();

            if (bottomBarController != null) {
                bottomBarController.setStatusSuccess("✅ " + dossierList.size() + " dossier(s) chargé(s)");
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des données: " + e.getMessage());
            e.printStackTrace();
            if (bottomBarController != null) {
                bottomBarController.setStatusError("❌ Erreur de chargement: " + e.getMessage());
            }
            showAlert("❌ Erreur", "Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    /**
     * ✅ Rafraîchit le tableau
     */
    @FXML
    public void handleRefresh() {
        System.out.println("🔄 Rafraîchissement du tableau...");
        if (bottomBarController != null) {
            bottomBarController.showProcessing("جاري التحميل...");
        }
        loadData();
        if (bottomBarController != null) {
            bottomBarController.setStatusSuccess("✅ Données mises à jour");
        }
    }

    /**
     * ✅ Ouvre le panneau de filtrage
     */

    @FXML
    public void handleSearch() {
        try {
            // Vérifier si la fenêtre est déjà ouverte
            if (filterStage != null && filterStage.isShowing()) {
                filterStage.requestFocus();
                return;
            }

            // ✅ Ouvrir search.fxml
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/DRJ/dossierexpert/views/pages/filter-panel.fxml")
            );
            BorderPane searchRoot = loader.load();

            // Récupérer le contrôleur
            SearchController searchController = loader.getController();

            // ✅ CORRECTION : Utiliser setMainController(this) au lieu de setMainLayoutController
            searchController.setMainController(this);  // 👈 Utiliser setMainController
            searchController.setTopBarController(topBarController);
            searchController.setBottomBarController(bottomBarController);

            filterStage = new Stage();
            filterStage.setTitle("🔍 Recherche avancée");
            filterStage.setScene(new Scene(searchRoot));
            filterStage.setResizable(true);
            filterStage.setWidth(900);
            filterStage.setHeight(700);
            filterStage.show();

            if (bottomBarController != null) {
                bottomBarController.setStatusInfo("🔍 Ouverture de la recherche");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("❌ خطأ", "Erreur lors de l'ouverture de la recherche");
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
            if ("prêt".equals(dossier.getStatut())) {
                etatComboBox.setValue("جاهز");
            } else {
                etatComboBox.setValue("غير جاهز");
            }
        } else {
            etatComboBox.setValue("جاهز");
        }
        
        remarquesField.setText(dossier.getRemarques());

        if (bottomBarController != null) {
            bottomBarController.setStatus("📄 الملف : " + dossier.getNumDossier());
        }
    }

    /**
     * ✅ Ouvre le formulaire de dossier avec les informations du dossier sélectionné
     */
    public void openDossierForm(Dossier dossier) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/DRJ/dossierexpert/views/pages/dossier-form.fxml")
            );
            BorderPane formRoot = loader.load();

            // Récupérer le contrôleur et passer le dossier
            DossierFormController formController = loader.getController();
            formController.setDossier(dossier);
            formController.setMainController(this);

            Stage formStage = new Stage();
            formStage.setTitle("📝 تعديل الملف - " + dossier.getNumDossier());
            formStage.setScene(new Scene(formRoot));
            formStage.setResizable(true);
            formStage.setWidth(800);
            formStage.setHeight(700);
            formStage.show();

            if (bottomBarController != null) {
                bottomBarController.setStatusInfo("📝 Ouverture du formulaire pour : " + dossier.getNumDossier());
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("❌ خطأ", "Erreur lors de l'ouverture du formulaire");
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
    private void handleSearchLocal() {
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

            String statutValue = etatComboBox.getValue();
            if ("جاهز".equals(statutValue)) {
                dossier.setStatut("prêt");
            } else {
                dossier.setStatut("Pas prêt");
            }

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
                loadData(); // Recharger les données
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
            e.printStackTrace();
            if (bottomBarController != null) {
                bottomBarController.setStatusError(e.getMessage());
            }
            showAlert("❌ خطأ", e.getMessage());
        }
    }

    @FXML
    public void handlePrint() {
        Dossier selectedDossier = dossierTable.getSelectionModel().getSelectedItem();

        if (selectedDossier == null) {
            showAlert("⚠️ تنبيه", "الرجاء اختيار ملف من الجدول");
            return;
        }

        try {
            // ✅ Load the dedicated preview FXML (dossier-preview.fxml)
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/DRJ/dossierexpert/views/pages/dossier-preview.fxml")
            );
            Scene scene = new Scene(loader.load());

            // ✅ Récupérer le contrôleur (PdfPreviewController)
            PdfPreviewController previewController = loader.getController();

            // ✅ Injecter les barres
            previewController.setTopBarController(topBarController);
            previewController.setBottomBarController(bottomBarController);

            // ✅ Passer le dossier
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

    // ==================== GETTERS ====================

    public TableView<Dossier> getDossierTable() {
        return dossierTable;
    }

    public FilteredList<Dossier> getFilteredData() {
        return filteredData;
    }

    public BottomBarController getBottomBarController() {
        return bottomBarController;
    }

    public DossierService getDossierService() {
        return dossierService;
    }
}