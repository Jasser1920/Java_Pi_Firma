package controllers;

import Models.ReponseReclamation;
import Services.ReponseReclamationService;
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

public class ModifierReponseReclamationController {

    @FXML
    private TextField reclamationIdFx;

    @FXML
    private TextArea messageFx;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button homeButton;

    @FXML
    private Label reclamationIdError;

    @FXML
    private Label messageError;

    private final ReponseReclamationService reponseService = new ReponseReclamationService();
    private ReponseReclamation reponseToEdit;

    @FXML
    public void initialize() {
        if (reponseToEdit != null) {
            reclamationIdFx.setText(String.valueOf(reponseToEdit.getReclamation() != null ? reponseToEdit.getReclamation().getId() : ""));
            messageFx.setText(reponseToEdit.getMessage());
        }
    }

    @FXML
    public void ModifierReponse(ActionEvent actionEvent) {
        String reclamationIdText = reclamationIdFx.getText().trim();
        String message = messageFx.getText().trim();

        try {
            int reclamationId = Integer.parseInt(reclamationIdText);
            if (reponseToEdit != null) {
                reponseToEdit.setId(reclamationId);
                reponseToEdit.setMessage(message);
                reponseService.modifier(reponseToEdit);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponse modifiée avec succès !");
                Stage stage = (Stage) reclamationIdFx.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune réponse à modifier.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID de réclamation doit être un nombre valide.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier la réponse : " + e.getMessage());
        }
    }

    @FXML
    public void AnnulerReponse(ActionEvent actionEvent) {
        Stage stage = (Stage) reclamationIdFx.getScene().getWindow();
        stage.close();
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

    public void setReponse(ReponseReclamation reponse) {
        this.reponseToEdit = reponse;
        if (reponse != null) {
            reclamationIdFx.setText(String.valueOf(reponse.getReclamation() != null ? reponse.getReclamation().getId() : ""));
            messageFx.setText(reponse.getMessage());
        }
    }

    public TextField getReclamationIdFx() {
        return reclamationIdFx;
    }

    public TextArea getMessageFx() {
        return messageFx;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}