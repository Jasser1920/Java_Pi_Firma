package controllers;

import Models.Reclammation;
import Models.ReponseReclamation;
import Services.ReclammationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class AfficherReclamationController {

    @FXML
    private VBox cardsContainer;

    @FXML
    private Button ajouterButton;

    private final ReclammationService reclammationService = new ReclammationService();
    private static final int MAX_PREVIEW_LENGTH = 50;

    @FXML
    public void initialize() {
        loadReclamations();
    }

    private void loadReclamations() {
        cardsContainer.getChildren().clear();
        try {
            List<Reclammation> reclamations = reclammationService.rechercher();
            for (Reclammation reclamation : reclamations) {
                VBox card = createReclamationCard(reclamation);
                cardsContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Chargement", "Impossible de charger les réclamations : " + e.getMessage());
        }
    }

    private VBox createReclamationCard(Reclammation reclamation) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e9ecef; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        Label titreLabel = new Label(reclamation.getTitre());
        titreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #343a40; -fx-font-family: 'Segoe UI';");

        String descriptionPreview = reclamation.getDescription().length() > MAX_PREVIEW_LENGTH
                ? reclamation.getDescription().substring(0, MAX_PREVIEW_LENGTH) + "..."
                : reclamation.getDescription();
        Label descriptionLabel = new Label(descriptionPreview);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d; -fx-font-family: 'Segoe UI';");

        Label dateLabel = new Label("2025-03-03"); // Placeholder pour la date
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-font-family: 'Segoe UI';");

        HBox actionsBox = new HBox(10);
        Label reponseIndicator = new Label(reclamation.getReponse() != null ? "Réponse reçue" : "Aucune réponse");
        reponseIndicator.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (reclamation.getReponse() != null ? "#28a745" : "#6c757d") + "; -fx-font-family: 'Segoe UI';");

        Button modifierButton = new Button("Modifier");
        modifierButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-font-family: 'Segoe UI';");
        modifierButton.setOnAction(event -> handleModifier(reclamation));

        Button supprimerButton = new Button("Supprimer");
        supprimerButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-font-family: 'Segoe UI';");
        supprimerButton.setOnAction(event -> handleSupprimer(reclamation));

        actionsBox.getChildren().addAll(dateLabel, reponseIndicator, modifierButton, supprimerButton);
        card.getChildren().addAll(titreLabel, descriptionLabel, actionsBox);
        card.setOnMouseClicked(event -> openDetailsReclamation(reclamation));
        return card;
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReclamation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Réclamation");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();
            loadReclamations();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'Ajout", "Impossible d'ouvrir la fenêtre d'ajout : " + e.getMessage());
        }
    }

    private void handleModifier(Reclammation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReclamation.fxml"));
            Parent root = loader.load();
            AjouterReclamationController controller = loader.getController();
            controller.setReclamation(reclamation);
            Stage stage = new Stage();
            stage.setTitle("Modifier Réclamation");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();
            loadReclamations();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Modification", "Impossible d'ouvrir la fenêtre de modification : " + e.getMessage());
        }
    }

    private void handleSupprimer(Reclammation reclamation) {
        try {
            reclammationService.supprimer(reclamation.getId());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation supprimée avec succès !");
            loadReclamations();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Suppression", "Impossible de supprimer la réclamation : " + e.getMessage());
        }
    }

    private void openDetailsReclamation(Reclammation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsReclamation.fxml"));
            Parent root = loader.load();
            DetailsReclamationController controller = loader.getController();
            controller.setReclamation(reclamation);
            Stage stage = new Stage();
            stage.setTitle("Détails de la Réclamation");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Détails", "Impossible d'ouvrir les détails : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}