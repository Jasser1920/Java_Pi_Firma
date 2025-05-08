package controllers;

import Models.Utilisateur;
import Services.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML private ChoiceBox<Models.Role> roleCB;
    @FXML private TextField profilePictureTF;
    @FXML private Button enregistrerBtn;
    @FXML private ImageView imagePreview;
    @FXML private Label nomError;
    @FXML private Label prenomError;
    @FXML private Label emailError;
    @FXML private Label motdepasseError;
    @FXML private Label telephoneError;
    @FXML private Label adresseError;
    @FXML private Label roleError;
    @FXML private Label profilePictureError;

    private Utilisateur utilisateur;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8}$"); // Validate only the 8 digits
    private static final String PHONE_PREFIX = "216";

    @FXML
    void initialize() {
        roleCB.setItems(FXCollections.observableArrayList(Models.Role.AGRICULTURE, Models.Role.CLIENT, Models.Role.ASSOCIATION));
        roleCB.setConverter(new javafx.util.StringConverter<Models.Role>() {
            @Override
            public String toString(Models.Role role) {
                return role != null ? role.getLabel() : "";
            }
            @Override
            public Models.Role fromString(String string) {
                return Models.Role.fromString(string);
            }
        });
        validateForm();
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        if (utilisateur != null) {
            nomTF.setText(utilisateur.getNom());
            prenomTF.setText(utilisateur.getPrenom());
            emailTF.setText(utilisateur.getEmail());
            motdepasseTF.setText(utilisateur.getMotdepasse());
            // Extract the 8 digits after the 216 prefix
            String telephone = utilisateur.getTelephone();
            if (telephone != null && telephone.startsWith(PHONE_PREFIX)) {
                telephoneTF.setText(telephone.substring(PHONE_PREFIX.length()));
            } else {
                telephoneTF.setText("");
            }
            adresseTF.setText(utilisateur.getAdresse());
            roleCB.setValue(utilisateur.getRole());
            profilePictureTF.setText(utilisateur.getProfilePicture());
            if (utilisateur.getProfilePicture() != null && !utilisateur.getProfilePicture().isEmpty()) {
                imagePreview.setImage(new Image(new File(utilisateur.getProfilePicture()).toURI().toString()));
                imagePreview.setVisible(true);
            }
            validateForm();
        }
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
            imagePreview.setImage(new Image(file.toURI().toString()));
            imagePreview.setVisible(true);
            validateForm();
        }
    }

    @FXML
    public void validateForm() {
        boolean nomValid = validateNom();
        boolean prenomValid = validatePrenom();
        boolean emailValid = validateEmail();
        boolean motdepasseValid = validateMotdepasse();
        boolean telephoneValid = validateTelephone();
        boolean adresseValid = validateAdresse();
        boolean roleValid = validateRole();
        boolean profilePictureValid = validateProfilePicture();

        boolean formValid = nomValid && prenomValid && emailValid && motdepasseValid
                && telephoneValid && adresseValid && roleValid && profilePictureValid;

        enregistrerBtn.setDisable(!formValid);
    }

    private boolean validateNom() {
        String nom = nomTF.getText().trim();
        if (nom.isEmpty()) {
            showError(nomTF, nomError, "Le nom est requis");
            return false;
        }
        clearError(nomTF, nomError);
        return true;
    }

    private boolean validatePrenom() {
        String prenom = prenomTF.getText().trim();
        if (prenom.isEmpty()) {
            showError(prenomTF, prenomError, "Le prénom est requis");
            return false;
        }
        clearError(prenomTF, prenomError);
        return true;
    }

    private boolean validateEmail() {
        String email = emailTF.getText().trim();
        if (email.isEmpty()) {
            showError(emailTF, emailError, "L'email est requis");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError(emailTF, emailError, "Format d'email invalide");
            return false;
        }
        clearError(emailTF, emailError);
        return true;
    }

    private boolean validateMotdepasse() {
        String motdepasse = motdepasseTF.getText().trim();
        if (motdepasse.isEmpty()) {
            showError(motdepasseTF, motdepasseError, "Le mot de passe est requis");
            return false;
        }
        if (motdepasse.length() < 6) {
            showError(motdepasseTF, motdepasseError, "Minimum 6 caractères");
            return false;
        }
        clearError(motdepasseTF, motdepasseError);
        return true;
    }

    private boolean validateTelephone() {
        String telephone = telephoneTF.getText().trim();
        if (telephone.isEmpty()) {
            showError(telephoneTF, telephoneError, "Le téléphone est requis");
            return false;
        }
        if (!PHONE_PATTERN.matcher(telephone).matches()) {
            showError(telephoneTF, telephoneError, "8 chiffres requis");
            return false;
        }
        clearError(telephoneTF, telephoneError);
        return true;
    }

    private boolean validateAdresse() {
        String adresse = adresseTF.getText().trim();
        if (adresse.isEmpty()) {
            showError(adresseTF, adresseError, "L'adresse est requise");
            return false;
        }
        clearError(adresseTF, adresseError);
        return true;
    }

    private boolean validateRole() {
        if (roleCB.getValue() == null) {
            showError(null, roleError, "Sélectionnez un rôle");
            return false;
        }
        clearError(null, roleError);
        return true;
    }

    private boolean validateProfilePicture() {
        String picture = profilePictureTF.getText();
        if (picture == null) picture = "";
        picture = picture.trim();
        if (picture.isEmpty()) {
            showError(profilePictureTF, profilePictureError, "Une photo est requise");
            return false;
        }
        clearError(profilePictureTF, profilePictureError);
        return true;
    }

    private void showError(TextField field, Label errorLabel, String message) {
        if (field != null) {
            field.setStyle("-fx-border-color: #F44336; -fx-border-width: 1.5px;");
        }
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void clearError(TextField field, Label errorLabel) {
        if (field != null) {
            field.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 1.5px;");
        }
        errorLabel.setVisible(false);
    }

    @FXML
    public void enregistrerModifications(ActionEvent actionEvent) {
        UtilisateurService us = new UtilisateurService();
        utilisateur.setNom(nomTF.getText().trim());
        utilisateur.setPrenom(prenomTF.getText().trim());
        utilisateur.setEmail(emailTF.getText().trim());
        utilisateur.setMotdepasse(motdepasseTF.getText().trim());
        utilisateur.setTelephone(PHONE_PREFIX + telephoneTF.getText().trim()); // Combine prefix with user input
        utilisateur.setAdresse(adresseTF.getText().trim());
        utilisateur.setRole(roleCB.getValue());
        utilisateur.setProfilePicture(profilePictureTF.getText().trim());
        try {
            us.modifier(utilisateur);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Succès");
            a.setContentText("Profil modifié avec succès.");
            a.showAndWait();
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileUtilisateur.fxml"));
            Parent root = loader.load();
            ProfileUtilisateurController controller = loader.getController();
            controller.setUtilisateur(utilisateur);
            nomTF.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}