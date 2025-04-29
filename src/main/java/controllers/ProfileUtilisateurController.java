package controllers;

import Models.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileUtilisateurController {
    @FXML private Label nomLabel;
    @FXML private Label prenomLabel;
    @FXML private Label emailLabel;
    @FXML private Label telephoneLabel;
    @FXML private Label adresseLabel;
    @FXML private Label roleLabel;
    @FXML private Label profilePictureLabel;

    private Utilisateur utilisateur;

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        if (utilisateur != null) {
            nomLabel.setText("Nom: " + utilisateur.getNom());
            prenomLabel.setText("Prénom: " + utilisateur.getPrenom());
            emailLabel.setText("Email: " + utilisateur.getEmail());
            telephoneLabel.setText("Téléphone: " + utilisateur.getTelephone());
            adresseLabel.setText("Adresse: " + utilisateur.getAdresse());
            roleLabel.setText("Rôle: " + utilisateur.getRole());
            profilePictureLabel.setText("Photo de profil: " + utilisateur.getProfilePicture());
        }
    }

    @FXML
    public void modifyProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierutilisateur.fxml"));
            Parent root = loader.load();
            ModifierUtilisateurController controller = loader.getController();
            controller.setUtilisateur(utilisateur);
            Stage stage = (Stage) nomLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modify Profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/home.fxml"));
            Stage stage = (Stage) nomLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}