package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AfficherPosterController implements Initializable {

    @FXML
    private ImageView afficheImage;

    @FXML
    private Button retourFX;

    private String posterUrl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Interface Affiche chargée");
    }

    public void setPosterUrl(String url) {
        this.posterUrl = url;
        try {
            Image image = new Image(url, true); // Chargement asynchrone
            afficheImage.setImage(image);
            System.out.println("Affiche chargée depuis : " + url);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement de l'affiche");
        }
    }

    @FXML
    public void retour() {
        Stage stage = (Stage) retourFX.getScene().getWindow();
        stage.close();
    }
}
