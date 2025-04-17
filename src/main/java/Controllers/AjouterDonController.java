package Controllers;

import Models.Don;
import Models.Evenemment;
import Services.DonService;
import Services.EvenemmentService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AjouterDonController {

    @FXML private TextField donateurField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Evenemment> evenementComboBox;
    @FXML private Button btnAjouter;
    @FXML
    private Button btnAnnuler;
    @FXML private Button retourFX;


    private AfficherDonController afficherDonController;

    public void setAfficherDonController(AfficherDonController controller) {
        this.afficherDonController = controller;
    }

    @FXML
    public void initialize() {
        descriptionArea.setWrapText(true);
        chargerEvenements();
    }

    private void chargerEvenements() {
        EvenemmentService evenementService = new EvenemmentService();
        List<Evenemment> evenements = evenementService.rechercher();
        evenementComboBox.setItems(FXCollections.observableArrayList(evenements));
    }

    @FXML
    public void ajouterDon() {
        String donateur = donateurField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDate dateValue = datePicker.getValue();
        Evenemment evenement = evenementComboBox.getValue();

        if (donateur.isEmpty() || description.isEmpty() || dateValue == null || evenement == null) {
            showAlert("Tous les champs sont obligatoires.");
            return;
        }

        if (description.length() < 10 || description.length() > 500) {
            showAlert("La description doit contenir entre 10 et 500 caractères.");
            return;
        }

        LocalDate today = LocalDate.now();
        if (dateValue.isBefore(today)) {
            showAlert("La date du don ne peut pas être dans le passé.");
            return;
        }

        Date date = Date.from(dateValue.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Le don est créé avec l'utilisateur (null pour l'instant, tu pourras l'ajouter plus tard si besoin)
        Don don = new Don(0, evenement, null, donateur, description, date);

        try {
            DonService donService = new DonService();
            donService.ajouter(don);
            showSuccess("Don ajouté avec succès !");
            if (afficherDonController != null) {
                afficherDonController.rafraichirTable();
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
