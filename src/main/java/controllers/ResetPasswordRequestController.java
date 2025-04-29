package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ResetPasswordRequestController {
    @FXML
    private TextField emailField;
    @FXML
    private TextField codeField;
    @FXML
    private Label infoLabel;

    private AuthController authController = AuthController.getInstance();
    private String userEmail;

    @FXML
    private void sendCode(ActionEvent event) {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            infoLabel.setText("Please enter your email.");
            return;
        }
        if (authController.sendResetCode(email)) {
            infoLabel.setText("Reset code sent to your email.");
            userEmail = email; // Store the email for verification
        } else {
            infoLabel.setText("Email not found or error sending code.");
        }
    }

    @FXML
    private void verifyCode(ActionEvent event) {
        String code = codeField.getText().trim();
        if (code.isEmpty()) {
            infoLabel.setText("Please enter the confirmation code.");
            return;
        }
        if (userEmail == null) {
            infoLabel.setText("Please send the code first.");
            return;
        }
        if (authController.verifyResetCode(code, userEmail)) {
            SceneChanger.changeScene(event, "/ChangePassword.fxml", userEmail);
        } else {
            infoLabel.setText("Invalid or expired code.");
        }
    }
}