package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Charger AfficherReclamation.fxml depuis src/main/resources/
        FXMLLoader fxmlLoader = new FXMLLoader(MainFx.class.getResource("/Home.fxml"));
        if (fxmlLoader.getLocation() == null) {
            throw new IOException("Cannot find /AjouterReponseReclamation.fxml");
        }
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Firma - Mes Réclamations");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}