package controllers;

import Models.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ProfileUtilisateurController {
    @FXML private Label nomLabel;
    @FXML private Label prenomLabel;
    @FXML private Label emailLabel;
    @FXML private Label telephoneLabel;
    @FXML private Label adresseLabel;
    @FXML private Label roleLabel;
    @FXML private Label profilePictureLabel;
    @FXML private ImageView profileImageView;

    private Utilisateur utilisateur;

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        if (utilisateur != null) {
            nomLabel.setText("Nom: " + (utilisateur.getNom() != null ? utilisateur.getNom() : "N/A"));
            prenomLabel.setText("Prénom: " + (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "N/A"));
            emailLabel.setText("Email: " + (utilisateur.getEmail() != null ? utilisateur.getEmail() : "N/A"));
            telephoneLabel.setText("Téléphone: " + (utilisateur.getTelephone() != null ? utilisateur.getTelephone() : "N/A"));
            adresseLabel.setText("Adresse: " + (utilisateur.getAdresse() != null ? utilisateur.getAdresse() : "N/A"));
            roleLabel.setText("Rôle: " + (utilisateur.getRole() != null ? utilisateur.getRole() : "N/A"));
            profilePictureLabel.setText("Photo de profil: " + (utilisateur.getProfilePicture() != null ? utilisateur.getProfilePicture() : "N/A"));

            // Load profile picture
            String profilePicturePath = utilisateur.getProfilePicture();
            if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
                File file = new File(profilePicturePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    profileImageView.setImage(image);
                } else {
                    profileImageView.setImage(null);
                }
            } else {
                profileImageView.setImage(null);
            }
        } else {
            nomLabel.setText("Nom: Error - User not found");
            prenomLabel.setText("Prénom: Error - User not found");
            emailLabel.setText("Email: Error - User not found");
            telephoneLabel.setText("Téléphone: Error - User not found");
            adresseLabel.setText("Adresse: Error - User not found");
            roleLabel.setText("Rôle: Error - User not found");
            profilePictureLabel.setText("Photo de profil: Error - User not found");
            profileImageView.setImage(null);
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