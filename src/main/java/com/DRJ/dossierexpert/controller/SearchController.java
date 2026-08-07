package com.DRJ.dossierexpert.controller;

import com.DRJ.dossierexpert.MainApplication;
import com.DRJ.dossierexpert.model.Dossier;
import com.DRJ.dossierexpert.service.DossierService;
import com.DRJ.dossierexpert.utils.SessionManager;
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

    // ==================== RÉFÉRENCES ====================
    private TopBarController topBarController;
    private BottomBarController bottomBarController;
    private MainController mainController;

    // ==================== COMPOSANTS ====================
    @FXML private ComboBox<String> searchCriteriaCombo;
    @FXML private TextField searchField;
    @FXML private Text dateLabel;
    @FXML private Text resultCountLabel;
    @FXML private Text pageLabel;
    @FXML private Text statusLabel;
    @FXML private Text infoLabel;

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

    // ==================== SERVICES ====================
    private DossierService dossierService;
    private ObservableList<Dossier> searchResults;
    private int currentPage = 1;
    private int itemsPerPage = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ SearchController initialisé");

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        dateLabel.setText("Date : " + today);

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

        dossierService = new DossierService();

        setupTableColumns();
        searchResults = FXCollections.observableArrayList();
        searchResultTable.setItems(searchResults);
        updateResultCount(0);

        if (statusLabel != null) {
            statusLabel.setText("✅ جاهز للبحث");
        }
        if (infoLabel != null) {
            infoLabel.setText("Nombre des resultats : 0");
        }

        searchResultTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Dossier selected = searchResultTable.getSelectionModel().getSelectedItem();
                if (selected != null && mainController != null) {
                    Stage stage = (Stage) searchResultTable.getScene().getWindow();
                    stage.close();
                    mainController.openDossierForm(selected);
                }
            }
        });
    }

    // ==================== SETTERS ====================

    public void setTopBarController(TopBarController topBarController) {
        this.topBarController = topBarController;
    }

    public void setBottomBarController(BottomBarController bottomBarController) {
        this.bottomBarController = bottomBarController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        System.out.println("✅ MainController injecté dans SearchController");
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

    // ==================== ACTIONS ====================

    @FXML
    private void handleFilter() {
        String criteria = searchCriteriaCombo.getValue();
        String searchValue = searchField.getText().trim();

        if (searchValue.isEmpty()) {
            showAlert("⚠️ تنبيه", "الرجاء إدخال قيمة للبحث");
            if (statusLabel != null) {
                statusLabel.setText("⚠️ الرجاء إدخال قيمة للبحث");
            }
            return;
        }

        try {
            if (statusLabel != null) {
                statusLabel.setText("⏳ جاري البحث...");
            }

            List<Dossier> results = dossierService.searchDossiers(criteria, searchValue);

            if (results != null && !results.isEmpty()) {
                searchResults.setAll(results);
                updateResultCount(results.size());
                pageLabel.setText("Page 1 de " + (int) Math.ceil((double) results.size() / itemsPerPage));

                if (statusLabel != null) {
                    statusLabel.setText("✅ تم العثور على " + results.size() + " ملف(ات)");
                }
                showAlert("✅ Résultat", "تم العثور على " + results.size() + " ملف(ات)");
            } else {
                searchResults.clear();
                updateResultCount(0);
                pageLabel.setText("Page 1 de 1");

                if (statusLabel != null) {
                    statusLabel.setText("ℹ️ لا توجد نتائج");
                }
                showAlert("ℹ️ Information", "لا توجد نتائج مطابقة للبحث");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (statusLabel != null) {
                statusLabel.setText("❌ Erreur: " + e.getMessage());
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

        if (statusLabel != null) {
            statusLabel.setText("✅ Filtres réinitialisés");
        }
    }

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

    private void updateResultCount(int count) {
        resultCountLabel.setText("Nombre des resultats : " + count);
        if (infoLabel != null) {
            infoLabel.setText("Nombre des resultats : " + count);
        }
    }

    // ==================== RETOUR ====================

    @FXML
    private void handleBack() {
        if (mainController != null) {
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.close();
            mainController.loadData();
            if (mainController.getBottomBarController() != null) {
                mainController.getBottomBarController().setStatusInfo("🔍 Retour à la page principale");
            }
        } else {
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

    // ================================================================
    // ✅ AJOUT : handleClose() pour fermer la fenêtre
    // ================================================================

    /**
     * ✅ Ferme la fenêtre de recherche
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) searchField.getScene().getWindow();
        stage.close();
        System.out.println("✅ Fenêtre de recherche fermée");
    }

    // ================================================================

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تأكيد الخروج");
        alert.setHeaderText("تسجيل الخروج");
        alert.setContentText("هل أنت متأكد de vouloir vous déconnecter ?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            SessionManager session = SessionManager.getInstance();
            session.destroySession();

            Stage stage = (Stage) searchField.getScene().getWindow();
            MainApplication.logoutAndExit(stage);
        }
    }

    // ==================== ALERT ====================

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
}