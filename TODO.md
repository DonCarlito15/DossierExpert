

## Step 1: Fix Dossier.java model
- [ ] Add `dossierNombre` field
- [ ] Fix `getDossierNombre()` to return the field
- [ ] Fix `setDossierNombre(String)` to properly set the field

## Step 2: Fix DAO package declarations and folder structure
- [ ] Ensure DAO folder matches package declaration (use lowercase `dao` OR change package to `DAO`)

## Step 3: Fix MainApplication.java resource paths
- [ ] Fix FXML path from `/com/DRJ/dossierexpert/pages/login.fxml` to `/com/DRJ/dossierexpert/views/pages/login.fxml`
- [ ] Fix CSS path from `/com/DRJ/dossierexpert/css/style.css` to `/com/DRJ/dossierexpert/views/css/style.css`

## Step 4: Fix MainLayoutController.java
- [ ] Remove reference to non-existent `DossierFormController`
- [ ] Clean up import statements

## Step 5: Fix controllers - Add missing setter methods
- [ ] Add `setTopBarController()` and `setBottomBarController()` to MainController
- [ ] Add `setTopBarController()` and `setBottomBarController()` to SearchController
- [ ] Add `setTopBarController()` and `setBottomBarController()` to PdfPreviewController

## Step 6: Fix personnelDAO.java - null safety
- [ ] Add null check for `personne.getDateInscription()` before Timestamp conversion

## Step 7: Fix FXML files
- [ ] Fix XML namespace versions in dossier-form.fxml and filter-panel.fxml
- [ ] Fix image paths in login.fxml
