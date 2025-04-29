package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFx extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load signin.fxml instead of home.fxml
        Parent root = FXMLLoader.load(getClass().getResource("/Signin.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Sign In");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}