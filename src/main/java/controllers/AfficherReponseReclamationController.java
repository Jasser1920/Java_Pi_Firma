package controllers;

import Models.ReponseReclamation;
import Services.ReponseReclamationService;
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

public class AfficherReponseReclamationController {

    @FXML
    private VBox cardsContainer;

    @FXML
    private Button ajouterButton;

    private final ReponseReclamationService reponseService = new ReponseReclamationService();
    private static final int MAX_PREVIEW_LENGTH = 50;

    @FXML
    public void initialize() {
        loadReponses();
    }

    private void loadReponses() {
        cardsContainer.getChildren().clear();
        try {
            List<ReponseReclamation> reponses = reponseService.rechercher();
            for (ReponseReclamation reponse : reponses) {
                VBox card = createReponseCard(reponse);
                cardsContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Chargement", "Impossible de charger les réponses : " + e.getMessage());
        }
    }

    private VBox createReponseCard(ReponseReclamation reponse) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label idLabel = new Label("Réclamation ID: " + (reponse.getReclamation() != null ? reponse.getReclamation().getId() : "N/A"));
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        String messagePreview = reponse.getMessage().length() > MAX_PREVIEW_LENGTH
                ? reponse.getMessage().substring(0, MAX_PREVIEW_LENGTH) + "..."
                : reponse.getMessage();
        Label messageLabel = new Label(messagePreview);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        HBox actionsBox = new HBox(10);
        Button modifierButton = new Button("Modifier");
        modifierButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        modifierButton.setOnAction(event -> handleModifier(reponse));

        Button supprimerButton = new Button("Supprimer");
        supprimerButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        supprimerButton.setOnAction(event -> handleSupprimer(reponse));

        actionsBox.getChildren().addAll(modifierButton, supprimerButton);
        card.getChildren().addAll(idLabel, messageLabel, actionsBox);
        card.setOnMouseClicked(event -> openDetailsReponse(reponse));
        return card;
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReponseReclamation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Réponse");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();
            loadReponses();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'Ajout", "Impossible d'ouvrir la fenêtre d'ajout : " + e.getMessage());
        }
    }

    private void handleModifier(ReponseReclamation reponse) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReponseReclamation.fxml"));
            Parent root = loader.load();
            AjouterReponseReclamationController controller = loader.getController();
            controller.setReponse(reponse);
            Stage stage = new Stage();
            stage.setTitle("Modifier Réponse");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();
            loadReponses();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Modification", "Impossible d'ouvrir la fenêtre de modification : " + e.getMessage());
        }
    }

    private void handleSupprimer(ReponseReclamation reponse) {
        try {
            reponseService.supprimer(reponse.getId());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponse supprimée avec succès !");
            loadReponses();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Suppression", "Impossible de supprimer la réponse : " + e.getMessage());
        }
    }

    private void openDetailsReponse(ReponseReclamation reponse) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsReponseReclamation.fxml"));
            Parent root = loader.load();
            DetailsReponseReclamationController controller = loader.getController();
            controller.setReponse(reponse);
            Stage stage = new Stage();
            stage.setTitle("Détails de la Réponse");
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