package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AjouterReclamationController {
    @FXML private TextField titreFx;
    @FXML private TextArea descriptionFx;
    @FXML private Button ajouterBtn;
    @FXML private Label titreError;
    @FXML private Label descriptionError;
    @FXML private Button homeButton;

    @FXML
    public void validateForm() {
        boolean titreValid = !titreFx.getText().trim().isEmpty();
        boolean descriptionValid = !descriptionFx.getText().trim().isEmpty();

        if (!titreValid) {
            titreError.setText("Le titre est requis");
            titreError.setVisible(true);
        } else {
            titreError.setVisible(false);
        }

        if (!descriptionValid) {
            descriptionError.setText("La description est requise");
            descriptionError.setVisible(true);
        } else {
            descriptionError.setVisible(false);
        }

        ajouterBtn.setDisable(!(titreValid && descriptionValid));
    }

    @FXML
    public void AjouterReclamation(ActionEvent event) {
        // Implement reclamation addition logic here
        // For now, just navigate back to home
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/home.fxml"));
            ajouterBtn.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void AnnulerReclamation(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/home.fxml"));
            ajouterBtn.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/home.fxml"));
            homeButton.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}