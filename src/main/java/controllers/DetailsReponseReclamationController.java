package controllers;

import Models.ReponseReclamation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class DetailsReponseReclamationController {

    @FXML
    private Label reclamationIdLabel;

    @FXML
    private Label dateReponseLabel;

    @FXML
    private TextArea messageArea;

    @FXML
    private Button fermerButton;

    public void setReponse(ReponseReclamation reponse) {
        reclamationIdLabel.setText("Réclamation ID: " + (reponse.getReclamation() != null ? reponse.getReclamation().getId() : "N/A"));
        dateReponseLabel.setText("Date: " + reponse.getDateReponse().toString());
        messageArea.setText(reponse.getMessage());
    }

    @FXML
    private void handleFermer(ActionEvent event) {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        stage.close();
    }
}