package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

public class HomeController {

    @FXML private Hyperlink produitLink;
    @FXML private Hyperlink categorieLink;
    @FXML private Hyperlink evenementLink;
    @FXML private Hyperlink donLink;
    @FXML private Hyperlink reclamationLink;
    @FXML private Hyperlink reponseLink;
    @FXML private Hyperlink commandeLink;
    @FXML private Hyperlink terrainLink;
    @FXML private Hyperlink livraisonLink;
    @FXML private Hyperlink locationLink;
    @FXML private Hyperlink userLink;


    @FXML
    public void initialize() {
        produitLink.setOnAction(event -> ouvrirPage("/AfficherProduit.fxml", "Produits"));
        categorieLink.setOnAction(event -> ouvrirPage("/AfficherCategorie.fxml", "Catégories"));
        evenementLink.setOnAction(event -> ouvrirPage("/AfficherEvenement.fxml", "Événements"));
        donLink.setOnAction(event -> ouvrirPage("/AfficherDon.fxml", "Dons"));
        userLink.setOnAction(event -> ouvrirPage("/AfficherUtilisateurs.fxml", "Utilisateurs"));
        reclamationLink.setOnAction(event -> ouvrirPage("/AfficherReclamation.fxml", "Réclamations"));
        reponseLink.setOnAction(event -> ouvrirPage("/AfficherReponseReclamation.fxml", "Réponses"));
        commandeLink.setOnAction(event -> ouvrirPage("/affichercommand.fxml", "Commandes"));
         terrainLink.setOnAction(event -> ouvrirPage("/TerrainList.fxml", "Terrains"));
         livraisonLink.setOnAction(event -> ouvrirPage("/afficherlivraison.fxml", "Livraisons"));
        locationLink.setOnAction(event -> ouvrirPage("/LocationList.fxml", "Terrains"));
        //
        //<?import javafx.scene.shape.Rectangle?>
        //
        //
        //<Rectangle arcHeight="5.0" arcWidth="5.0" fill="#85c20a" height="50.0" layoutX="16.0" layoutY="14.0" stroke="#85c20a" strokeType="INSIDE" style="-fx-arc-height: 300; -fx-arc-width: 50;" width="964.0" xmlns="http://javafx.com/javafx/23.0.1" xmlns:fx="http://javafx.com/fxml/1" />erLocation.fxml", "Locations"));
    }

    private void ouvrirPage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) produitLink.getScene().getWindow(); // ou categorieLink, peu importe ici
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
