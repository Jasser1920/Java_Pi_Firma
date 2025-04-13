package Controllers;

import Models.Categorie;
import Services.CategorieService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherCategorieController {

    @FXML private TableView<Categorie> categorieTable;
    @FXML private TableColumn<Categorie, Integer> idColumn;
    @FXML private TableColumn<Categorie, String> nomColumn;
    @FXML private Button btnAjouter;
    @FXML private Button btnVoir;
    @FXML private Button btnRetourProduit;
    @FXML private Button homeFX;
    @FXML private Label compteurLabel;
    @FXML
    private Label labelNomTopCategorie;


    private final CategorieService service = new CategorieService();



    @FXML
    public void initialize() {
        // Initialisation des colonnes
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nomColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomCategorie()));

        // Rafraîchir la table
        rafraichirTable();

        // Lier les boutons
        btnAjouter.setOnAction(event -> ajouterCategorie());
        btnVoir.setOnAction(event -> voirCategorie());
        btnRetourProduit.setOnAction(event -> retournerProduits());

        // Affichage de la catégorie la plus utilisée
        try {
            Categorie topCategorie = service.getCategorieLaPlusUtilisee();
            if (topCategorie != null) {
                labelNomTopCategorie.setText(topCategorie.getNomCategorie());
            } else {
                labelNomTopCategorie.setText("Aucune");
            }
        } catch (Exception e) {
            e.printStackTrace();
            labelNomTopCategorie.setText("Erreur");
        }

    }

    public void rafraichirTable() {
        try {
            List<Categorie> categories = service.rechercher();
            categorieTable.setItems(FXCollections.observableArrayList(categories));
            compteurLabel.setText(String.valueOf(categories.size())); // Mise à jour du compteur
        } catch (SQLException e) {
            showAlert("Erreur lors du chargement des catégories : " + e.getMessage());
        }
    }



    public void ajouterCategorie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCategorie.fxml"));
            Parent root = loader.load();

            AjouterCategorieController controller = loader.getController();
            controller.setAfficherCategorieController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une catégorie");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur d'ouverture du formulaire d'ajout.");
        }
    }

    public void voirCategorie() {
        Categorie selected = categorieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Veuillez sélectionner une catégorie à consulter.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailCategorie.fxml"));
            Parent root = loader.load();

            DetailCategorieController controller = loader.getController();
            controller.setCategorie(selected);
            controller.setAfficherCategorieController(this);
            controller.initComboBox();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de la catégorie");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur d'ouverture des détails de la catégorie.");
        }
    }

    public void retournerProduits() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRetourProduit.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Produits");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du retour à la liste des produits.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alerte");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void ouvrirHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeFX.getScene().getWindow(); // remplace la scène actuelle
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture de la page d'accueil.");
        }
    }

}
