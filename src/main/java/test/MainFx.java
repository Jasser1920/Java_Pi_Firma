package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFx extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            // Debug: Check if the stylesheet resource exists
            java.net.URL stylesheetUrl = getClass().getResource("/css/styles.css");
            if (stylesheetUrl == null) {
                System.err.println("Stylesheet /css/styles.css not found in classpath!");
            } else {
                System.out.println("Stylesheet found at: " + stylesheetUrl);
            }

            Parent root = FXMLLoader.load(getClass().getResource("/AjouterUtilisateur.fxml"));
            Scene scene = new Scene(root, 1280, 860);
            // Load the stylesheet programmatically
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setWidth(1280);
            stage.setHeight(860);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}