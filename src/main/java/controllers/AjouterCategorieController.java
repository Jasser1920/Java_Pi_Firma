package controllers;

import Models.Categorie;
import Services.CategorieService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AjouterCategorieController {

    @FXML
    private ComboBox<String> categorieComboBox;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAnnuler;
    @FXML
    private Button retourFX;

    private AfficherCategorieController afficherCategorieController;

    public void setAfficherCategorieController(AfficherCategorieController controller) {
        this.afficherCategorieController = controller;
    }

    @FXML
    public void initialize() {
        categorieComboBox.setItems(FXCollections.observableArrayList(
                "Fruits", "Légumes", "Céréales", "Fruits secs", "Apiculture", "Oeufs", "Produits laitiers",
                "Viandes", "Volailles", "Plantes aromatiques", "Plantes médicales", "Champignons", "Huiles", "Épices"
        ));
    }

    @FXML
    public void ajouterCategorie() {
        String nom = categorieComboBox.getValue();

        if (nom == null || nom.isEmpty()) {
            showAlert("Veuillez sélectionner une catégorie.");
            return;
        }

        try {
            CategorieService service = new CategorieService();

            // Vérification d'existence
            if (service.existeCategorie(nom)) {
                showAlert("Cette catégorie existe déjà !");
                return;
            }

            Categorie categorie = new Categorie(0, nom);
            service.ajouter(categorie);
            showSuccess("Catégorie ajoutée avec succès !");
            if (afficherCategorieController != null) {
                afficherCategorieController.rafraichirTable();
            }
            closeStage();
        } catch (Exception e) {
            showAlert("Erreur lors de l'ajout : " + e.getMessage());
        }
    }


    @FXML
    public void annulerAjout() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void retour() {
        Stage stage = (Stage) retourFX.getScene().getWindow();
        stage.close();
    }
}
