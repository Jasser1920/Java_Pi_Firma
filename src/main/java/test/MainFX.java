package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {


            Parent root = FXMLLoader.load(getClass().getResource("/TerrainList.fxml"));
            Scene scene = new Scene(root, 1280, 860);

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