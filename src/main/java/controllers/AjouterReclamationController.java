package controllers;

import Models.Reclammation;
import Models.Statut;
import Models.Utilisateur;
import Services.PerspectiveService;
import Services.ReclammationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;

public class AjouterReclamationController {
    @FXML private TextField titreFx;
    @FXML private TextArea descriptionFx;
    @FXML private Button ajouterBtn;
    @FXML private Label titreError;
    @FXML private Label descriptionError;
    @FXML private Button homeButton;

    private final ReclammationService reclammationService = new ReclammationService();
    private PerspectiveService perspectiveService;
    private Utilisateur currentUser;

    public AjouterReclamationController() {
        try {
            this.perspectiveService = new PerspectiveService();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'initialiser le service de vérification de contenu");
        }
    }

    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
    }

    @FXML
    public void validateForm() {
        boolean titreValid = !titreFx.getText().trim().isEmpty();
        boolean descriptionValid = !descriptionFx.getText().trim().isEmpty();

        if (!titreValid) {
            titreError.setText("Le titre est requis");
            titreError.setVisible(true);
        } else {
            titreError.setVisible(false);
        }

        if (!descriptionValid) {
            descriptionError.setText("La description est requise");
            descriptionError.setVisible(true);
        } else {
            descriptionError.setVisible(false);
        }

        ajouterBtn.setDisable(!(titreValid && descriptionValid));
    }

    @FXML
    public void AjouterReclamation(ActionEvent event) {
        if (currentUser == null) {
            // Try to get from AuthController (global session)
            currentUser = AuthController.getInstance().getCurrentUser();
        }
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur connecté");
            return;
        }

        if (perspectiveService == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le service de vérification de contenu n'est pas disponible");
            return;
        }

        String titre = titreFx.getText().trim();
        String description = descriptionFx.getText().trim();

        // Vérifier le contenu avec Perspective API
        if (!perspectiveService.isContentAppropriate(titre) || !perspectiveService.isContentAppropriate(description)) {
            showAlert(Alert.AlertType.WARNING, "Contenu inapproprié",
                    "Veuillez reformuler votre réclamation, le contenu est inapproprié");
            return;
        }

        try {
            Reclammation reclamation = new Reclammation(
                    0, // L'ID sera généré par la base de données
                    currentUser,
                    titre,
                    description,
                    new Date(),
                    Statut.EN_ATTENTE
            );

            reclammationService.ajouter(reclamation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation ajoutée avec succès !");

            // Retourner à la page des réclamations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();
            AfficherReclamationController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            Stage stage = (Stage) ajouterBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mes Réclamations");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la réclamation : " + e.getMessage());
        }
    }

    @FXML
    public void AnnulerReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();
            AfficherReclamationController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            Stage stage = (Stage) ajouterBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mes Réclamations");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à la liste des réclamations : " + e.getMessage());
        }
    }

    @FXML
    public void goToHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à l'accueil : " + e.getMessage());
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