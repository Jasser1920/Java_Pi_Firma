package Controllers;

import Models.Categorie;
import Services.CategorieService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DetailCategorieController {

    @FXML
    private ComboBox<String> categorieComboBox;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnAnnuler;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button retourFX;

    private Categorie categorie;
    private AfficherCategorieController afficherCategorieController;

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public void setAfficherCategorieController(AfficherCategorieController controller) {
        this.afficherCategorieController = controller;
    }

    public void initComboBox() {
        categorieComboBox.setItems(FXCollections.observableArrayList(
                "Fruits", "Légumes", "Céréales", "Fruits secs", "Apiculture", "Oeufs", "Produits laitiers",
                "Viandes", "Volailles", "Plantes aromatiques", "Plantes médicales", "Champignons", "Huiles", "Épices"
        ));
        if (categorie != null) {
            categorieComboBox.setValue(categorie.getNomCategorie());
        }
    }

    @FXML
    public void initialize() {
        btnModifier.setOnAction(event -> modifierCategorie());
        btnAnnuler.setOnAction(event -> closeWindow());
        btnSupprimer.setOnAction(event -> supprimerCategorie());
    }

    private void modifierCategorie() {
        String nouveauNom = categorieComboBox.getValue();
        if (nouveauNom == null || nouveauNom.isEmpty()) {
            showAlert("Veuillez sélectionner une catégorie.");
            return;
        }

        try {
            CategorieService service = new CategorieService();

            if (!nouveauNom.equals(categorie.getNomCategorie()) && service.existeCategorie(nouveauNom)) {
                showAlert("Une catégorie avec ce nom existe déjà.");
                return;
            }

            categorie.setNomCategorie(nouveauNom);
            service.modifier(categorie);
            showSuccess("Catégorie modifiée avec succès !");
            if (afficherCategorieController != null) {
                afficherCategorieController.rafraichirTable();
            }
            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur lors de la modification : " + e.getMessage());
        }
    }

    private void supprimerCategorie() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette catégorie ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    new CategorieService().supprimer(categorie);
                    showSuccess("Catégorie supprimée avec succès !");
                    if (afficherCategorieController != null) {
                        afficherCategorieController.rafraichirTable();
                    }
                    closeWindow();
                } catch (Exception e) {
                    showAlert("Erreur lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) btnModifier.getScene().getWindow();
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
        closeWindow();
    }
}
