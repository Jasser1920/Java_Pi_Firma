package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import Models.Utilisateur;
import Services.UtilisateurService;

import java.sql.SQLException;

public class SigninController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthController authController = AuthController.getInstance();
    private UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    private void handleSignIn(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            System.out.println("SigninController: Empty email or password");
            return;
        }
        if (authController.login(email, password)) {
            System.out.println("SigninController: Login successful for " + email);
            try {
                // Fetch the user to check their role
                Utilisateur user = utilisateurService.findByEmail(email);
                if (user != null && "admin".equalsIgnoreCase(user.getRole())) {
                    // Redirect to admin dashboard if the role is "admin"
                    SceneChanger.changeScene(event, "/admin_dashboard.fxml");
                } else {
                    // Redirect to regular home for non-admin users
                    SceneChanger.changeScene(event, "/home.fxml");
                }
            } catch (SQLException e) {
                errorLabel.setText("Error fetching user details.");
                System.err.println("SigninController: Failed to fetch user role for " + email + ": " + e.getMessage());
            }
        } else {
            errorLabel.setText("Invalid credentials or email not verified.");
            System.out.println("SigninController: Login failed for " + email);
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