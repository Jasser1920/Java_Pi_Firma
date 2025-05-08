package controllers;

import Models.Reclammation;
import Models.Utilisateur;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherReclamationController {

    @FXML
    private VBox cardsContainer;

    @FXML
    private Button ajouterButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button refreshButton;

    @FXML
    private Button homeButton;

    private final ReclammationService reclammationService = new ReclammationService();
    private static final int MAX_PREVIEW_LENGTH = 50;
    private List<Reclammation> allReclamations;
    private Utilisateur currentUser;

    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
        loadReclamations();
    }

    @FXML
    public void initialize() {
        // Les réclamations seront chargées une fois que l'utilisateur sera défini
    }

    private void loadReclamations() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur connecté");
            return;
        }

        cardsContainer.getChildren().clear();
        try {
            allReclamations = reclammationService.rechercherParUtilisateur(currentUser.getId());
            if (allReclamations == null || allReclamations.isEmpty()) {
                Label noReclamationsLabel = new Label("Aucune réclamation disponible.");
                noReclamationsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                cardsContainer.getChildren().add(noReclamationsLabel);
            } else {
                for (Reclammation reclamation : allReclamations) {
                    VBox card = createReclamationCard(reclamation);
                    cardsContainer.getChildren().add(card);
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Chargement", "Impossible de charger les réclamations : " + e.getMessage());
            allReclamations = null; // Reset to null to avoid stale data
        }
    }

    private VBox createReclamationCard(Reclammation reclamation) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");

        Label titreLabel = new Label(reclamation.getTitre());
        titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        String description = reclamation.getDescription();
        if (description.length() > MAX_PREVIEW_LENGTH) {
            description = description.substring(0, MAX_PREVIEW_LENGTH) + "...";
        }
        Label descriptionLabel = new Label(description);

        Label dateLabel = new Label("Date: " + reclamation.getDateCreation());
        Label statutLabel = new Label("Statut: " + reclamation.getStatut());

        HBox buttonBox = new HBox(10);
        Button detailsButton = new Button("Détails");
        Button modifierButton = new Button("Modifier");
        Button supprimerButton = new Button("Supprimer");

        detailsButton.setOnAction(e -> openDetailsReclamation(reclamation));
        modifierButton.setOnAction(e -> handleModifier(reclamation));
        supprimerButton.setOnAction(e -> handleSupprimer(reclamation));

        buttonBox.getChildren().addAll(detailsButton, modifierButton, supprimerButton);
        card.getChildren().addAll(titreLabel, descriptionLabel, dateLabel, statutLabel, buttonBox);

        return card;
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReclamation.fxml"));
            Parent root = loader.load();
            AjouterReclamationController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            Stage stage = new Stage();
            stage.setTitle("Ajouter Réclamation");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();
            loadReclamations();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'Ajout", "Impossible d'ouvrir la fenêtre d'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void filterReclamations() {
        if (allReclamations == null || allReclamations.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Aucune réclamation à filtrer. Veuillez charger les réclamations d'abord.");
            return;
        }

        String searchText = searchField.getText().trim().toLowerCase(); // Trim to remove leading/trailing spaces
        cardsContainer.getChildren().clear();

        List<Reclammation> filteredReclamations = allReclamations.stream()
                .filter(r -> (r.getTitre() != null && r.getTitre().toLowerCase().contains(searchText)) ||
                        (r.getDescription() != null && r.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());

        if (filteredReclamations.isEmpty()) {
            Label noResultsLabel = new Label("Aucune réclamation trouvée.");
            noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            cardsContainer.getChildren().add(noResultsLabel);
        } else {
            for (Reclammation reclamation : filteredReclamations) {
                VBox card = createReclamationCard(reclamation);
                cardsContainer.getChildren().add(card);
            }
        }
    }

    @FXML
    private void refreshList(ActionEvent event) {
        searchField.clear();
        loadReclamations();
    }

    private void handleModifier(Reclammation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReclamation.fxml"));
            Parent root = loader.load();
            ModifierReclamationController controller = loader.getController();
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

    @FXML
    private void navigateHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner à l'accueil : " + e.getMessage());
        }
    }
}