package Controllers;

import Models.Categorie;
import Models.Produit;
import Services.CategorieService;
import Services.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class DetaillerProduitController {

    private Produit produit;
    private AfficherProduitController afficherProduitController;

    @FXML private TextField nomField;
    @FXML private TextField prixField;
    @FXML private TextArea descriptionField;
    @FXML private TextArea imageField;
    @FXML private TextField quantiteField;
    @FXML private DatePicker dateExpirationPicker;
    @FXML private ComboBox<Categorie> categorieCombo;
    @FXML
    private Button retourFX;

    public void setProduit(Produit produit) {
        this.produit = produit;

        nomField.setText(produit.getNom());
        prixField.setText(String.valueOf(produit.getPrix()));
        descriptionField.setText(produit.getDescription());
        imageField.setText(produit.getImage());
        quantiteField.setText(String.valueOf(produit.getQuantite()));
        dateExpirationPicker.setValue(convertToLocalDate(produit.getDateExpiration()));
        categorieCombo.setValue(produit.getCategorie());
    }

    public void setAfficherProduitController(AfficherProduitController controller) {
        this.afficherProduitController = controller;
    }

    @FXML
    public void initialize() throws SQLException {
        CategorieService cs = new CategorieService();
        ObservableList<Categorie> categories = FXCollections.observableArrayList(cs.rechercher());
        categorieCombo.setItems(categories);

        categorieCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "" : item.getNomCategorie());
            }
        });

        categorieCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "" : item.getNomCategorie());
            }
        });
    }

    @FXML
    public void modifierProduit(ActionEvent event) {
        try {
            String nom = nomField.getText().trim();
            String prixStr = prixField.getText().trim();
            String quantiteStr = quantiteField.getText().trim();
            String image = imageField.getText().trim();
            String description = descriptionField.getText().trim();
            LocalDate dateExp = dateExpirationPicker.getValue();
            Categorie selectedCat = categorieCombo.getValue();

            if (nom.isEmpty() || prixStr.isEmpty() || quantiteStr.isEmpty() || image.isEmpty()
                    || description.isEmpty() || dateExp == null || selectedCat == null) {
                showAlert("Tous les champs sont obligatoires.");
                return;
            }

            double prix = Double.parseDouble(prixStr);
            int quantite = Integer.parseInt(quantiteStr);

            produit.setNom(nom);
            produit.setPrix(prix);
            produit.setQuantite(quantite);
            produit.setImage(image);
            produit.setDescription(description);
            produit.setDateExpiration(Date.valueOf(dateExp));
            produit.setCategorie(selectedCat);

            new ProduitService().modifier(produit);

            showSuccess("Produit modifié avec succès !");
            if (afficherProduitController != null) {
                afficherProduitController.rafraichirTable();
            }
            closeStage();

        } catch (Exception e) {
            showAlert("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerProduit(ActionEvent event) throws SQLException {
        if (produit != null) {
            new ProduitService().supprimer(produit);
            if (afficherProduitController != null) {
                afficherProduitController.rafraichirTable();
            }
            closeStage();
        }
    }

    @FXML
    public void retourListe(ActionEvent event) {
        closeStage();
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

    private void closeStage() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private LocalDate convertToLocalDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    @FXML
    private void retour() {
        closeStage();
    }

}
