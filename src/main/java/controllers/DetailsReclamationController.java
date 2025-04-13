package controllers;

import Models.Reclammation;
import Models.ReponseReclamation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class DetailsReclamationController {

    @FXML
    private Label titreLabel;

    @FXML
    private Label statutLabel;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextArea reponseArea;

    @FXML
    private Button fermerButton;

    public void setReclamation(Reclammation reclamation) {
        titreLabel.setText(reclamation.getTitre());
        statutLabel.setText("Statut : " + reclamation.getStatut().name());
        descriptionArea.setText(reclamation.getDescription());
        ReponseReclamation reponse = reclamation.getReponse();
        reponseArea.setText(reponse != null ? reponse.getMessage() : "Aucune réponse");
    }

    @FXML
    private void handleFermer(ActionEvent event) {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        stage.close();
    }
}