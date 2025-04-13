package controllers;

import Models.Utilisateur;
import Services.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ModifierUtilisateurController {
    @FXML private TextField nomTF;
    @FXML private TextField prenomTF;
    @FXML private TextField emailTF;
    @FXML private TextField motdepasseTF;
    @FXML private TextField telephoneTF;
    @FXML private TextField adresseTF;
    @FXML private ChoiceBox<String> roleCB;
    @FXML private TextField profilePictureTF;
    @FXML private Button enregistrerBtn;

    private Utilisateur utilisateur;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8}$");

    @FXML
    void initialize() {
        roleCB.setItems(FXCollections.observableArrayList("Agriculture", "Client", "Association"));
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        nomTF.setText(utilisateur.getNom());
        prenomTF.setText(utilisateur.getPrenom());
        emailTF.setText(utilisateur.getEmail());
        motdepasseTF.setText(utilisateur.getMotdepasse());
        telephoneTF.setText(utilisateur.getTelephone());
        adresseTF.setText(utilisateur.getAdresse());
        roleCB.setValue(utilisateur.getRole());
        profilePictureTF.setText(utilisateur.getProfilePicture());
        validateForm();
    }

    @FXML
    public void uploadPicture(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(nomTF.getScene().getWindow());
        if (file != null) {
            profilePictureTF.setText(file.getAbsolutePath());
            validateForm();
        }
    }

    @FXML
    public void validateForm() {
        boolean isValid = !nomTF.getText().trim().isEmpty() &&
                !prenomTF.getText().trim().isEmpty() &&
                EMAIL_PATTERN.matcher(emailTF.getText().trim()).matches() &&
                !motdepasseTF.getText().trim().isEmpty() &&
                PHONE_PATTERN.matcher(telephoneTF.getText().trim()).matches() &&
                !adresseTF.getText().trim().isEmpty() &&
                roleCB.getValue() != null &&
                !profilePictureTF.getText().trim().isEmpty();
        enregistrerBtn.setDisable(!isValid);
    }

    @FXML
    public void enregistrerModifications(ActionEvent actionEvent) {
        UtilisateurService us = new UtilisateurService();
        utilisateur.setNom(nomTF.getText());
        utilisateur.setPrenom(prenomTF.getText());
        utilisateur.setEmail(emailTF.getText());
        utilisateur.setMotdepasse(motdepasseTF.getText());
        utilisateur.setTelephone(telephoneTF.getText());
        utilisateur.setAdresse(adresseTF.getText());
        utilisateur.setRole(roleCB.getValue());
        utilisateur.setProfilePicture(profilePictureTF.getText());
        try {
            us.modifier(utilisateur);
            retourAfficher(actionEvent);
        } catch (SQLException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Erreur");
            a.setContentText(e.getMessage());
            a.show();
        }
    }

    @FXML
    public void retourAfficher(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUtilisateurs.fxml"));
        try {
            Parent root = loader.load();
            nomTF.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}