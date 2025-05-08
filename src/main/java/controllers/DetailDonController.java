package controllers;

import Models.Don;
import Models.Evenemment;
import Services.DonService;
import Services.EvenemmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class DetailDonController {

    @FXML private TextField donateurField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Evenemment> evenementComboBox;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnRetour;
    @FXML private Button retourFX;


    private Don don;
    private controllers.AfficherDonController afficherDonController;
    private final DonService donService = new DonService();

    public void setDon(Don don) {
        this.don = don;
        remplirChamps();
    }

    public void setAfficherDonController(controllers.AfficherDonController controller) {
        this.afficherDonController = controller;
    }

    @FXML
    public void initialize() {
        List<Evenemment> evenements = new EvenemmentService().rechercher();
        evenementComboBox.setItems(FXCollections.observableArrayList(evenements));
    }

    private void remplirChamps() {
        if (don != null) {
            donateurField.setText(don.getDonateur());
            descriptionArea.setText(don.getDescription());

            if (don.getDate() != null) {
                //  Solution alternative au toInstant() qui pose problème avec java.sql.Date
                LocalDate localDate = new java.sql.Date(don.getDate().getTime()).toLocalDate();
                datePicker.setValue(localDate);
            }

            if (don.getEvenement() != null) {
                for (Evenemment ev : evenementComboBox.getItems()) {
                    if (ev.getId() == don.getEvenement().getId()) {
                        evenementComboBox.getSelectionModel().select(ev);
                        break;
                    }
                }
            }
        }
    }

    @FXML
    public void modifierDon() {
        String donateur = donateurField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDate localDate = datePicker.getValue();
        Evenemment selectedEvenement = evenementComboBox.getValue();

        if (donateur.isEmpty() || description.isEmpty() || localDate == null || selectedEvenement == null) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        Date date = java.sql.Date.valueOf(localDate);

        don.setDonateur(donateur);
        don.setDescription(description);
        don.setDate(date);
        don.setEvenement(selectedEvenement);

        try {
            donService.modifier(don);
            showSuccess("Don modifié avec succès !");
            if (afficherDonController != null) {
                afficherDonController.rafraichirTable();
            }
            fermer();
        } catch (Exception e) {
            showAlert("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerDon() {
        try {
            donService.supprimer(don);
            showSuccess("Don supprimé avec succès !");
            if (afficherDonController != null) {
                afficherDonController.rafraichirTable();
            }
            fermer();
        } catch (Exception e) {
            showAlert("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @FXML
    public void retourner() {
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
    @FXML
    private void retour() {
        fermer();
    }
}
