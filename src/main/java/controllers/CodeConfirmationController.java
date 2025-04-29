package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CodeConfirmationController {
    @FXML
    private TextField codeField;
    @FXML
    private Label infoLabel;

    private AuthController authController = AuthController.getInstance(); // Use singleton instance
    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        String code = codeField.getText().trim();
        if (code.isEmpty()) {
            infoLabel.setText("Please enter the verification code.");
            return;
        }
        if (authController.confirmCode(code, userEmail)) {
            SceneChanger.changeScene(event, "/home.fxml");
        } else {
            infoLabel.setText("Invalid or expired code.");
        }
    }
}