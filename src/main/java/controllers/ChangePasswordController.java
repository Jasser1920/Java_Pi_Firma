package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ChangePasswordController {
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label infoLabel;

    private AuthController authController = AuthController.getInstance();
    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    private void changePassword(ActionEvent event) {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            infoLabel.setText("Please fill in all fields.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            infoLabel.setText("Passwords do not match.");
            return;
        }
        if (newPassword.length() < 6) {
            infoLabel.setText("Password must be at least 6 characters.");
            return;
        }
        if (authController.updatePassword(userEmail, newPassword)) {
            infoLabel.setText("Password updated successfully.");
            SceneChanger.changeScene(event, "/signin.fxml");
        } else {
            infoLabel.setText("Error updating password.");
        }
    }
}