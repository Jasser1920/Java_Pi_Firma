package controllers;

import Models.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {
    @FXML private Button btnUser;
    @FXML private Button btnReclamation;
    @FXML private Button btnProduct;
    @FXML private VBox customOptionsBox;
    @FXML private StackPane mainContent;
    @FXML private Button btnLogout;
    private AuthController authController = AuthController.getInstance();

    @FXML
    public void initialize() {
        btnUser.setOnAction(e -> loadView("/user_table.fxml"));
        btnReclamation.setOnAction(e -> loadView("/reclamation_table.fxml"));
        btnProduct.setOnAction(e -> {/* TODO: load product view */});
        // You can add more options to customOptionsBox dynamically
    }

    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContent.getChildren().setAll(node);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Method to add new sidebar options dynamically
    public void addSidebarOption(String label, Runnable action) {
        Button btn = new Button(label);
        btn.setMinWidth(120);
        btn.setOnAction(e -> action.run());
        customOptionsBox.getChildren().add(btn);
    }
    @FXML
    public void logout() {
        authController.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Signin.fxml"));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign In");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}