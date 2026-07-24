package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.service.DossierService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    // ==================== RÉFÉRENCES AUX BARRES ====================
    private TopBarController topBarController;
    private BottomBarController bottomBarController;
    private MainLayoutController mainLayoutController;

    // ==================== COMPOSANTS DE RECHERCHE ====================
    @FXML private ComboBox<String> searchCriteriaCombo;
    @FXML private TextField searchField;

    // ==================== TABLEAU ====================
    @FXML private TableView<Dossier> searchResultTable;
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
    @FXML private TableColumn<Dossier, String> dateColumn21;

    // ==================== COMPTEURS ====================
    @FXML private Text resultCountLabel;
    @FXML private Text pageLabel;

    // ==================== SERVICES ====================
    private DossierService dossierService;
    private ObservableList<Dossier> searchResults;
    private int currentPage = 1;
    private int itemsPerPage = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ SearchController initialisé");

        // ==================== COMBOBOX ====================
        searchCriteriaCombo.getItems().addAll(
                "رقم الملف",
                "رقم المراسلة",
                "المصدر",
                "المحامي",
                "المبلغ",
                "الحالة",
                "التاريخ"
        );
        searchCriteriaCombo.setValue("رقم الملف");

        // ==================== SERVICE ====================
        dossierService = new DossierService();

        // ==================== TABLEAU ====================
        setupTableColumns();

        // ==================== INITIALISATION ====================
        searchResults = FXCollections.observableArrayList();
        searchResultTable.setItems(searchResults);
        updateResultCount(0);

        // ==================== STATUT ====================
        if (bottomBarController != null) {
            bottomBarController.setStatus("🔍 جاهز للبحث");
        }
    }

    // ==================== SETTERS POUR LES BARRES ====================

    public void setTopBarController(TopBarController topBarController) {
        this.topBarController = topBarController;
        System.out.println("✅ topBarController injecté dans SearchController");
        updateTopBar();
    }

    public void setBottomBarController(BottomBarController bottomBarController) {
        this.bottomBarController = bottomBarController;
        System.out.println("✅ bottomBarController injecté dans SearchController");
        if (bottomBarController != null) {
            bottomBarController.setStatus("🔍 جاهز للبحث");
        }
    }

    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    /**
     * Met à jour la barre supérieure
     */
    private void updateTopBar() {
        if (topBarController != null) {
            topBarController.setPageTitle("🔍 Recherche");
            topBarController.updateDate();
            topBarController.updateUserInfo();
            System.out.println("✅ TopBar mise à jour dans SearchController");
        } else {
            System.out.println("⚠️ topBarController est NULL dans SearchController");
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
        dateColumn21.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    // ==================== ACTIONS RECHERCHE ====================

    @FXML
    private void handleFilter() {
        String criteria = searchCriteriaCombo.getValue();
        String searchValue = searchField.getText().trim();

        if (searchValue.isEmpty()) {
            showAlert("⚠️ تنبيه", "الرجاء إدخال قيمة للبحث");
            if (bottomBarController != null) {
                bottomBarController.setStatusWarning("الرجاء إدخال قيمة للبحث");
            }
            return;
        }

        try {
            if (bottomBarController != null) {
                bottomBarController.showProcessing("جاري البحث...");
            }

            List<Dossier> results = dossierService.searchDossiers(criteria, searchValue);

            if (results != null && !results.isEmpty()) {
                searchResults.setAll(results);
                updateResultCount(results.size());
                pageLabel.setText("Page 1 de " + (int) Math.ceil((double) results.size() / itemsPerPage));

                if (bottomBarController != null) {
                    bottomBarController.setStatusSuccess("تم العثور على " + results.size() + " ملف(ات)");
                }
                showAlert("✅ Résultat", "تم العثور على " + results.size() + " ملف(ات)");
            } else {
                searchResults.clear();
                updateResultCount(0);
                pageLabel.setText("Page 1 de 1");

                if (bottomBarController != null) {
                    bottomBarController.setStatusInfo("لا توجد نتائج مطابقة");
                }
                showAlert("ℹ️ Information", "لا توجد نتائج مطابقة للبحث");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (bottomBarController != null) {
                bottomBarController.setStatusError("Erreur: " + e.getMessage());
            }
            showAlert("❌ خطأ", "Erreur lors de la recherche: " + e.getMessage());
        }
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        searchCriteriaCombo.setValue("رقم الملف");
        searchResults.clear();
        updateResultCount(0);
        pageLabel.setText("Page 1 de 1");

        if (bottomBarController != null) {
            bottomBarController.setStatus("✅ تم إعادة ضبط البحث");
        }
    }

    // ==================== PAGINATION ====================

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePagination();
        }
    }

    @FXML
    private void handleNextPage() {
        int totalPages = (int) Math.ceil((double) searchResults.size() / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            updatePagination();
        }
    }

    private void updatePagination() {
        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, searchResults.size());

        if (fromIndex < toIndex) {
            List<Dossier> pageItems = searchResults.subList(fromIndex, toIndex);
            searchResultTable.setItems(FXCollections.observableArrayList(pageItems));
        }

        int totalPages = (int) Math.ceil((double) searchResults.size() / itemsPerPage);
        pageLabel.setText("Page " + currentPage + " de " + Math.max(1, totalPages));
    }

    // ==================== UPDATE RESULT COUNT ====================

    private void updateResultCount(int count) {
        resultCountLabel.setText("Nombre des resultats : " + count);
        if (bottomBarController != null) {
            bottomBarController.setInfo("Nombre des resultats : " + count);
        }
    }

    // ==================== RETOUR ====================

    @FXML
    private void handleBack() {
        if (mainLayoutController != null) {
            mainLayoutController.navigateToMain();
        } else {
            // Fallback
            try {
                Stage stage = (Stage) searchField.getScene().getWindow();
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

    public TableView<Dossier> getSearchResultTable() {
        return searchResultTable;
    }

    public ObservableList<Dossier> getSearchResults() {
        return searchResults;
    }

    public TopBarController getTopBarController() {
        return topBarController;
    }

    public BottomBarController getBottomBarController() {
        return bottomBarController;
    }
}