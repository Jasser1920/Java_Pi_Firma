package controllers;

import Models.Role;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AjouterUtilisateurController {
    @FXML
    private TextField nomTF;
    @FXML
    private TextField prenomTF;
    @FXML
    private TextField emailTF;
    @FXML
    private TextField motdepasseTF;
    @FXML
    private TextField telephoneTF;
    @FXML
    private TextField adresseTF;
    @FXML
    private ChoiceBox<Models.Role> roleCB;
    @FXML
    private TextField profilePictureTF;
    @FXML
    private Button ajouterBtn;
    @FXML
    private ImageView imagePreview;

    @FXML
    private Label nomError;
    @FXML
    private Label prenomError;
    @FXML
    private Label emailError;
    @FXML
    private Label motdepasseError;
    @FXML
    private Label telephoneError;
    @FXML
    private Label adresseError;
    @FXML
    private Label roleError;
    @FXML
    private Label profilePictureError;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8}$"); // Only validate the 8 digits
    private static final String PHONE_PREFIX = "216";

    @FXML
    void initialize() {
        // Filter out the "Admin" role
        roleCB.setItems(FXCollections.observableArrayList(
                java.util.Arrays.stream(Role.values())
                        .filter(role -> !role.getLabel().equalsIgnoreCase("Admin"))
                        .collect(Collectors.toList())
        ));
        roleCB.setConverter(new javafx.util.StringConverter<Models.Role>() {
            @Override
            public String toString(Models.Role role) {
                return role != null ? role.getLabel() : "";
            }
            @Override
            public Models.Role fromString(String string) {
                return Role.fromString(string);
            }
        });

        // Bind ImageView size to a fraction of the parent VBox width
        imagePreview.fitWidthProperty().bind(((VBox) imagePreview.getParent()).widthProperty().multiply(0.4));
        imagePreview.fitHeightProperty().bind(imagePreview.fitWidthProperty());

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
            imagePreview.setImage(new Image(file.toURI().toString()));
            imagePreview.setVisible(true);
            validateForm();
        }
    }

    @FXML
    public boolean validateForm() {
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

        ajouterBtn.setDisable(!formValid);
        return formValid;
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
        String picture = profilePictureTF.getText().trim();
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
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }

    private void clearError(TextField field, Label errorLabel) {
        if (field != null) {
            field.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 1.5px;");
        }
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    @FXML
    public void ajouterUtilisateur(ActionEvent actionEvent) {
        if (!validateForm()) return;

        UtilisateurService us = new UtilisateurService();
        AuthController authController = AuthController.getInstance();
        String nom = nomTF.getText().trim();
        String prenom = prenomTF.getText().trim();
        String email = emailTF.getText().trim();
        String motdepasse = motdepasseTF.getText().trim();
        String telephone = PHONE_PREFIX + telephoneTF.getText().trim(); // Combine prefix with user input
        String adresse = adresseTF.getText().trim();
        Models.Role role = roleCB.getValue();
        String profilePicture = profilePictureTF.getText().trim();

        Utilisateur u = new Utilisateur(0, nom, prenom, email, motdepasse, telephone, adresse, role, profilePicture, false, false, null);
        try {
            if (authController.signup(u)) {
                nomTF.clear();
                prenomTF.clear();
                emailTF.clear();
                motdepasseTF.clear();
                telephoneTF.clear(); // Clear only the editable part
                adresseTF.clear();
                roleCB.setValue(null);
                profilePictureTF.clear();
                imagePreview.setImage(null);
                imagePreview.setVisible(false);

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Succès");
                a.setContentText("Utilisateur ajouté avec succès. Veuillez vérifier votre email.");
                a.showAndWait();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/codeConfirmation.fxml"));
                Parent root = loader.load();
                CodeConfirmationController controller = loader.getController();
                controller.setUserEmail(email);
                nomTF.getScene().setRoot(root);
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Erreur");
                a.setContentText("Erreur lors de l'ajout de l'utilisateur.");
                a.show();
            }
        } catch (IOException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Erreur");
            a.setContentText(e.getMessage());
            a.show();
        }
    }

    @FXML
    public void goToSignIn(ActionEvent actionEvent) {
        SceneChanger.changeScene(actionEvent, "/signin.fxml");
    }
}