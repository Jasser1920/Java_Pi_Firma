package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneChanger {
    public static void changeScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(SceneChanger.class.getResource(fxmlFile));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void changeScene(ActionEvent event, String fxmlFile, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneChanger.class.getResource(fxmlFile));
            Parent root = loader.load();
            if (fxmlFile.equals("/ChangePassword.fxml")) {
                ChangePasswordController controller = loader.getController();
                controller.setUserEmail(email);
            }
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}