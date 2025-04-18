package controllers;

import Models.Reclammation;
import Models.Statut;
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

public class ModifierReclamationController {

    @FXML
    private TextField titreFx;

    @FXML
    private TextArea descriptionFx;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button homeButton;

    @FXML
    private Label titreError;

    @FXML
    private Label descriptionError;

    private final ReclammationService reclammationService = new ReclammationService();
    private Reclammation reclamationToEdit;
    private static final int MIN_TITRE_LENGTH = 3;
    private static final int MIN_DESCRIPTION_LENGTH = 10;

    @FXML
    public void initialize() {
        if (reclamationToEdit != null) {
            titreFx.setText(reclamationToEdit.getTitre());
            descriptionFx.setText(reclamationToEdit.getDescription());
            validateForm(); // Initial validation
        }
    }

    @FXML
    public void ModifierReclamation(ActionEvent actionEvent) {
        String titre = titreFx.getText().trim();
        String description = descriptionFx.getText().trim();

        if (!validateForm()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez corriger les erreurs dans le formulaire.");
            return;
        }

        try {
            if (reclamationToEdit != null) {
                reclamationToEdit.setTitre(titre);
                reclamationToEdit.setDescription(description);
                reclammationService.modifier(reclamationToEdit);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation modifiée avec succès !");
                Stage stage = (Stage) titreFx.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune réclamation à modifier.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier la réclamation : " + e.getMessage());
        }
    }

    @FXML
    public void AnnulerReclamation(ActionEvent actionEvent) {
        Stage stage = (Stage) titreFx.getScene().getWindow();
        stage.close();
    }

    @FXML
    public boolean validateForm() {
        String titre = titreFx.getText().trim();
        String description = descriptionFx.getText().trim();
        boolean isValid = true;

        // Validate titre
        if (titre.isEmpty()) {
            titreError.setText("Le titre est requis.");
            isValid = false;
        } else if (titre.length() < MIN_TITRE_LENGTH) {
            titreError.setText("Le titre doit contenir au moins " + MIN_TITRE_LENGTH + " caractères.");
            isValid = false;
        } else {
            titreError.setText("");
        }

        // Validate description
        if (description.isEmpty()) {
            descriptionError.setText("La description est requise.");
            isValid = false;
        } else if (description.length() < MIN_DESCRIPTION_LENGTH) {
            descriptionError.setText("La description doit contenir au moins " + MIN_DESCRIPTION_LENGTH + " caractères.");
            isValid = false;
        } else {
            descriptionError.setText("");
        }

        // Enable/disable Modifier button
        modifierBtn.setDisable(!isValid);
        return isValid;
    }

    @FXML
    public void navigateHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de Bord");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner au tableau de bord : " + e.getMessage());
        }
    }

    public void setReclamation(Reclammation reclamation) {
        this.reclamationToEdit = reclamation;
        if (reclamation != null) {
            titreFx.setText(reclamation.getTitre());
            descriptionFx.setText(reclamation.getDescription());
            validateForm();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public TextField getTitreFx() {
        return titreFx;
    }

    public TextArea getDescriptionFx() {
        return descriptionFx;
    }
}