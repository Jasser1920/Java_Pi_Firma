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
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HomeController {

    @FXML private Hyperlink produitLink;
    @FXML private Hyperlink reclamationLink;
    @FXML private Hyperlink reponseLink;
    @FXML private Hyperlink terrainLink;
    @FXML private Hyperlink commandLink;
    @FXML private Hyperlink donLink;
    @FXML private Hyperlink  evenementLink;
    @FXML private Label userNameLabel;
    @FXML private MenuButton userMenu;
    @FXML private MenuItem profileItem;
    @FXML private MenuItem reclamationItem;
    @FXML private MenuItem logoutItem;
    @FXML private Label welcomeLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button profileButton;
    @FXML private Button reclamationsButton;
    @FXML private Button logoutButton;
    @FXML private VBox adminPanel;
    @FXML private ImageView profilePicture;

    private AuthController authController = AuthController.getInstance();
    private Utilisateur currentUser;

    @FXML
    public void initialize() {
        // Test WhatsApp configuration on startup
        try {

            // Test d'envoi de message WhatsApp

        } catch (Exception e) {
            System.err.println("Erreur de configuration WhatsApp: " + e.getMessage());
            e.printStackTrace();
        }

        produitLink.setOnAction(event -> ouvrirPage("/AfficherProduit.fxml", "Produits"));
        reclamationLink.setOnAction(event -> ouvrirPage("/AfficherReclamation.fxml", "Réclamations"));
        reponseLink.setOnAction(event -> ouvrirPage("/AfficherReponseReclamation.fxml", "Réponses"));
        terrainLink.setOnAction(event -> ouvrirPage("/TerrainList.fxml", "terrainLink"));
        commandLink.setOnAction(event -> ouvrirPage("/affichercommand.fxml", "terrainLink"));
        donLink.setOnAction(event -> ouvrirPage("/AfficherDon.fxml", "donLink"));
        evenementLink.setOnAction(event -> ouvrirPage("/AfficherEvenement.fxml", "evenementLink"));
        Utilisateur currentUser = authController.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            System.out.println("HomeController: User initialized - " + currentUser.getEmail());
            setCurrentUser(currentUser);
        } else {
            userNameLabel.setText("Guest");
            System.out.println("HomeController: No user found");
        }
    }

    @FXML
    public void showProfile() {
        try {
            Utilisateur currentUser = authController.getCurrentUser();
            if (currentUser == null) {
                System.out.println("showProfile: No user logged in");
                return;
            }
            System.out.println("Navigating to profile for user: " + currentUser.getEmail());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileUtilisateur.fxml"));
            Parent root = loader.load();
            ProfileUtilisateurController controller = loader.getController();
            controller.setUtilisateur(currentUser);
            Stage stage = (Stage) userMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profile");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading profile page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showReclamation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();
            AfficherReclamationController controller = loader.getController();
            controller.setCurrentUser(authController.getCurrentUser());
            Stage stage = (Stage) userMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mes Réclamations");
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

            // Si on ouvre AfficherEvenement.fxml, transmettre l’utilisateur connecté
            if (fxmlPath.equals("/AfficherEvenement.fxml")) {
                AfficherEvenementController controller = loader.getController();
                controller.setUtilisateurConnecte(currentUser);
            }

            Stage stage = (Stage) userMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
        updateUI();
    }

    private void updateUI() {
        if (currentUser != null) {
            // Update welcome message
            welcomeLabel.setText("Bienvenue, " + currentUser.getPrenom() + " " + currentUser.getNom());

            // Update role display
            userRoleLabel.setText("Rôle : " + currentUser.getRole());

            // Show/hide admin panel based on role
            adminPanel.setVisible(currentUser.getRole().equals("ADMIN"));

            // Update profile picture if available
            if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) {
                // Load and display profile picture
                // You'll need to implement this based on how you store profile pictures
            }
        }
    }

    @FXML
    private void handleProfile() {
        // Implement profile view/edit functionality
    }

    @FXML
    private void handleReclamations() {
        // Implement reclamations view functionality
    }

    @FXML
    private void handleLogout() {
        // Implement logout functionality
        currentUser = null;
        // Navigate to login screen
    }
}