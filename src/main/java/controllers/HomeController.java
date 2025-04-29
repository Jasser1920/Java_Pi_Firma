package controllers;

import Models.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML private Hyperlink produitLink;
    @FXML private Hyperlink reclamationLink;
    @FXML private Hyperlink reponseLink;
    @FXML private Label userNameLabel;
    @FXML private MenuButton userMenu;
    @FXML private MenuItem profileItem;
    @FXML private MenuItem reclamationItem;
    @FXML private MenuItem logoutItem;

    private AuthController authController = AuthController.getInstance();

    @FXML
    public void initialize() {
        // Set hyperlink actions
        produitLink.setOnAction(event -> ouvrirPage("/AfficherProduit.fxml", "Produits"));
        reclamationLink.setOnAction(event -> ouvrirPage("/AfficherReclamation.fxml", "Réclamations"));
        reponseLink.setOnAction(event -> ouvrirPage("/AfficherReponseReclamation.fxml", "Réponses"));

        // Display logged-in user's name
        Utilisateur currentUser = authController.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNom() + " " + currentUser.getPrenom());
        } else {
            userNameLabel.setText("Guest");
        }
    }

    @FXML
    public void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileUtilisateur.fxml"));
            Parent root = loader.load();
            ProfileUtilisateurController controller = loader.getController();
            controller.setUtilisateur(authController.getCurrentUser());
            Stage stage = (Stage) userMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showReclamation() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterReclamation.fxml"));
            Stage stage = (Stage) userMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Réclamation");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logout() {
        authController.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/signin.fxml"));
            Stage stage = (Stage) userMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign In");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirPage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) produitLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}