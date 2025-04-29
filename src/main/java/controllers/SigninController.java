package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SigninController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private AuthController authController = AuthController.getInstance();

    @FXML
    private void handleSignIn(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }
        if (authController.login(email, password)) {
            SceneChanger.changeScene(event, "/home.fxml");
        } else {
            errorLabel.setText("Invalid credentials or email not verified.");
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        SceneChanger.changeScene(event, "/AjouterUtilisateur.fxml");
    }

    @FXML
    private void handleResetPassword(ActionEvent event) {
        SceneChanger.changeScene(event, "/ResetPasswordRequest.fxml");
    }
}