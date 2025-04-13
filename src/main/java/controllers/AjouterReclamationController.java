package controllers;

import Models.Reclammation;
import Models.Statut;
import Services.ReclammationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Date;

public class AjouterReclamationController {

    @FXML
    private TextField titreFx;

    @FXML
    private TextArea descriptionFx;

    private final ReclammationService reclammationService = new ReclammationService();
    private Reclammation reclamationToEdit;
    private static final int MIN_TITRE_LENGTH = 3;
    private static final int MIN_DESCRIPTION_LENGTH = 10;

    @FXML
    @SuppressWarnings("unused")
    public void AjouterReclamation(ActionEvent actionEvent) {
        String titre = titreFx.getText().trim();
        String description = descriptionFx.getText().trim();

        if (titre.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (titre.length() < MIN_TITRE_LENGTH) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le titre doit contenir au moins " + MIN_TITRE_LENGTH + " caractères.");
            return;
        }

        if (description.length() < MIN_DESCRIPTION_LENGTH) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La description doit contenir au moins " + MIN_DESCRIPTION_LENGTH + " caractères.");
            return;
        }

        try {
            if (reclamationToEdit != null) {
                reclamationToEdit.setTitre(titre);
                reclamationToEdit.setDescription(description);
                reclammationService.modifier(reclamationToEdit);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation modifiée avec succès !");
            } else {
                Reclammation reclammation = new Reclammation(
                        0, null, titre, description, new Date(), Statut.enattente
                );
                reclammationService.ajouter(reclammation);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation ajoutée avec succès !");
            }
            Stage stage = (Stage) titreFx.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de sauvegarder la réclamation : " + e.getMessage());
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void AnnulerReclamation(ActionEvent actionEvent) {
        Stage stage = (Stage) titreFx.getScene().getWindow();
        stage.close();
    }

    public void setReclamation(Reclammation reclamation) {
        this.reclamationToEdit = reclamation;
        titreFx.setText(reclamation.getTitre());
        descriptionFx.setText(reclamation.getDescription());
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