package controllers;


import Models.Categorie;
import Models.Produit;
import Services.CategorieService;
import Services.ProduitService;
import Utils.CohereTextImprover;
import Utils.PrixScraper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterProduitController {

    @FXML private TextField nomFX;
    @FXML private Button btnAjouterFX;
    @FXML private Label titreFx;
    @FXML private TextField prixFX;
    @FXML private TextField quantiteFX;
    @FXML private DatePicker dateExpirationFX;
    @FXML private TextArea imageFX;
    @FXML private TextArea descriptionFX;
    @FXML private ComboBox<Categorie> menuCategorieFX;
    @FXML private Button retourFX;
    @FXML private TextArea descriptionAmelioreeFX;
    @FXML private Button btnAmeliorerFX;
    @FXML private Button btnRevenirFX;
    private String ancienneDescription = "";
    @FXML
    private Label prixSuggereLabel;





    private controllers.AfficherProduitController afficherProduitController;

    public void setAfficherProduitController(controllers.AfficherProduitController controller) {
        this.afficherProduitController = controller;
    }

    @FXML
    public void initialize() throws SQLException {
        CategorieService cs = new CategorieService();
        menuCategorieFX.getItems().addAll(cs.rechercher());
        nomFX.textProperty().addListener((obs, oldVal, newVal) -> {
            String suggestion = PrixScraper.getPrixPourProduit(newVal);
            prixSuggereLabel.setText(suggestion != null ? "💡 Prix suggéré : " + suggestion : "");
        });

    }

    @FXML
    public void AjouterProduit(ActionEvent actionEvent) throws SQLException {
        String nom = nomFX.getText().trim();
        String prixStr = prixFX.getText().trim();
        String quantiteStr = quantiteFX.getText().trim();
        String image = imageFX.getText().trim();
        String description = descriptionFX.getText().trim();
        LocalDate dateExp = dateExpirationFX.getValue();

        // Vérification
        if (nom.isEmpty()) {
            showAlert("Le nom du produit est obligatoire.");
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(prixStr);
            if (prix < 0) {
                showAlert("Le prix ne peut pas être négatif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Le prix doit être un nombre valide.");
            return;
        }

        if (description.isEmpty()) {
            showAlert("La description est obligatoire.");
            return;
        }

        if (image.isEmpty()) {
            showAlert("L’image est obligatoire.");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(quantiteStr);
            if (quantite <= 0) {
                showAlert("La quantité doit être un entier strictement positif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("La quantité doit être un nombre entier.");
            return;
        }

        if (dateExp == null || !dateExp.isAfter(LocalDate.now())) {
            showAlert("La date d’expiration doit être ultérieure à aujourd’hui.");
            return;
        }

        if (dateExp.isAfter(LocalDate.now().plusMonths(3))) {
            showAlert("La date d’expiration ne doit pas dépasser 3 mois.");
            return;
        }

        Categorie selectedCategorie = menuCategorieFX.getValue();
        if (selectedCategorie == null) {
            showAlert("Veuillez sélectionner une catégorie.");
            return;
        }

        Produit produit = new Produit(
                0,
                selectedCategorie,
                null,
                nom,
                prix,
                description,
                image,
                quantite,
                Date.valueOf(dateExp)
        );


        ProduitService ps = new ProduitService();
        ps.ajouter(produit);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Produit ajouté avec succès !");
        alert.showAndWait();

        if (afficherProduitController != null) {
            afficherProduitController.rafraichirTable();
        }

        Stage stage = (Stage) btnAjouterFX.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Champ manquant");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void retour() {
        Stage stage = (Stage) retourFX.getScene().getWindow();
        stage.close(); // ou une autre logique si tu veux revenir à une vue précise
    }

    @FXML
    private void handleImproveDescription() {
        String input = descriptionFX.getText();
        if (input == null || input.isEmpty()) {
            descriptionAmelioreeFX.setText("Écris une description d’abord !");
            return;
        }

        String result = CohereTextImprover.improveText(input);
        descriptionAmelioreeFX.setText(result);
    }

    @FXML
    private void ameliorerDescription() {
        String input = descriptionFX.getText();
        if (input == null || input.isEmpty()) {
            showAlert("Écris une description d’abord !");
            return;
        }

        ancienneDescription = input;
        String result = CohereTextImprover.improveText(input);
        descriptionFX.setText(result);
    }
    @FXML
    private void restaurerDescription() {
        if (!ancienneDescription.isEmpty()) {
            descriptionFX.setText(ancienneDescription);
        }
    }
}
