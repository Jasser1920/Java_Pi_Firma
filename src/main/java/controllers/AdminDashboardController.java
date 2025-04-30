package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class AdminDashboardController {
    @FXML private Button btnUser;
    @FXML private Button btnReclamation;
    @FXML private Button btnProduct;
    @FXML private VBox customOptionsBox;
    @FXML private StackPane mainContent;

    @FXML
    public void initialize() {
        btnUser.setOnAction(e -> loadView("/user_table.fxml"));
        btnReclamation.setOnAction(e -> {/* TODO: load reclamation view */});
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
}
