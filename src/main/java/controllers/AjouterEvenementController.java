package controllers;

import Models.Evenemment;
import Services.EvenemmentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AjouterEvenementController {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker datePicker;
    @FXML private TextField lieuxField;
    @FXML private Button btnAjouter;
    @FXML private Button btnAnnuler;
    @FXML private Button retourFX;


    private AfficherEvenementController afficherEvenementController;

    public void setAfficherEvenementController(AfficherEvenementController controller) {
        this.afficherEvenementController = controller;
    }

    @FXML
    public void initialize() {
        descriptionArea.setWrapText(true);
    }

    @FXML
    public void ajouterEvenement() {
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

        LocalDate today = LocalDate.now();
        if (selectedDate.isBefore(today)) {
            showAlert("La date de l'événement ne peut pas être dans le passé.");
            return;
        }

        Date date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Evenemment evenement = new Evenemment(0, null, titre, description, date, lieux, "");

        try {
            EvenemmentService service = new EvenemmentService();
            service.ajouter(evenement);
            showSuccess("Événement ajouté avec succès !");
            if (afficherEvenementController != null) {
                afficherEvenementController.rafraichirTable();
            }
            fermer();
        } catch (Exception e) {
            showAlert("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    public void annuler() {
        fermer();
    }

    @FXML
    private void fermer() {
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
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
    @FXML
    private void retour() {
        fermer();
    }
}
