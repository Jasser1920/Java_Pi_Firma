package Controllers;

import Models.Evenemment;
import Services.EvenemmentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    public void ajouterEvenement() {
        String titre = titreField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDate selectedDate = datePicker.getValue();
        String lieu = lieuxField.getText().trim();

        if (titre.isEmpty() || description.isEmpty() || selectedDate == null || lieu.isEmpty()) {
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
        Evenemment evenement = new Evenemment(0, null, titre, description, date, lieu, "");

        try {
            EvenemmentService service = new EvenemmentService();
            service.ajouter(evenement);
            showSuccess("Événement ajouté avec succès !");
            if (afficherEvenementController != null) {
                afficherEvenementController.rafraichirTable(); // 🔁 met à jour la table
            }
            fermer();

            // Remplace par ton vrai template ID
            String placidTemplateId = Utils.PlacidConfig.TEMPLATE_ID;


            // Exemple de construction de l’URL (Placid en mode GET sans clé API)
            String placidUrl = "https://api.placid.app/u/" + placidTemplateId +
                    "?title[text]=" + URLEncoder.encode(titre, "UTF-8") +
                    "&date[text]=" + URLEncoder.encode(String.valueOf(date), "UTF-8") +
                    "&lieu[text]=" + URLEncoder.encode(lieu, "UTF-8");

            // Charger l’affiche dans la nouvelle fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPoster.fxml"));
            Parent root = loader.load();

            AfficherPosterController controller = loader.getController();
            controller.setPosterUrl(placidUrl);

            Stage stage = new Stage();
            stage.setTitle("Affiche générée");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Erreur lors de la génération de l'affiche.");
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

    private void genererAfficheAvecPlacid(Evenemment evenement) {
        try {
            String url = "https://api.placid.app/u/TON_TEMPLATE_ID" +
                    "?title[text]=" + encode(evenement.getTitre()) +
                    "&description[text]=" + encode(evenement.getDesecription()) +
                    "&lieu[text]=" + encode(evenement.getLieux());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPoster.fxml"));
            Parent root = loader.load();

            AfficherPosterController controller = loader.getController();
            controller.setPosterUrl(url); // on passe l’URL de l’affiche

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Affiche générée");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String encode(String texte) {
        return URLEncoder.encode(texte, StandardCharsets.UTF_8);
    }


}
