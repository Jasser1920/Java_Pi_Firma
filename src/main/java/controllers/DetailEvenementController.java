package controllers;

import Models.Evenemment;
import Services.EvenemmentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DetailEvenementController {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker datePicker;
    @FXML private TextField lieuxField;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnRetour;
    @FXML
    private Button retourFX;


    private Evenemment evenement;
    private AfficherEvenementController afficherEvenementController;

    public void setEvenement(Evenemment evenement) {
        this.evenement = evenement;
        titreField.setText(evenement.getTitre());
        descriptionArea.setText(evenement.getDesecription());
        lieuxField.setText(evenement.getLieux());

        if (evenement.getDate() != null) {
            datePicker.setValue(convertToLocalDate(evenement.getDate()));
        }

    }

    public void setAfficherEvenementController(AfficherEvenementController controller) {
        this.afficherEvenementController = controller;
    }

    @FXML
    public void initialize() {
        descriptionArea.setWrapText(true);
    }

    @FXML
    public void modifierEvenement() {
        String titre = titreField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDate selectedDate = datePicker.getValue();
        String lieux = lieuxField.getText().trim();

        if (titre.isEmpty() || description.isEmpty() || selectedDate == null || lieux.isEmpty()) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        if (description.length() < 50 || description.length() > 500) {
            showAlert("La description doit contenir entre 50 et 500 caractères.");
            return;
        }

        if (selectedDate.isBefore(LocalDate.now())) {
            showAlert("La date ne peut pas être dans le passé.");
            return;
        }

        evenement.setTitre(titre);
        evenement.setDesecription(description);
        evenement.setDate(Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        evenement.setLieux(lieux);

        try {
            EvenemmentService service = new EvenemmentService();
            service.modifier(evenement);
            showSuccess("Événement modifié avec succès !");
            if (afficherEvenementController != null) {
                afficherEvenementController.rafraichirTable();
            }
            fermer();
        } catch (Exception e) {
            showAlert("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerEvenement() {
        try {
            EvenemmentService service = new EvenemmentService();
            service.supprimer(evenement);
            showSuccess("Événement supprimé !");
            if (afficherEvenementController != null) {
                afficherEvenementController.rafraichirTable();
            }
            fermer();
        } catch (Exception e) {
            showAlert("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @FXML
    public void retour() {
        fermer();
    }

    private void fermer() {
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
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
    private LocalDate convertToLocalDate(java.util.Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }


}
