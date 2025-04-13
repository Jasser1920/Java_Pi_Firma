package Controllers;

import Models.Terrain;
import Services.TerrainService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AjouterTerrainController {
    private static final Logger LOGGER = Logger.getLogger(AjouterTerrainController.class.getName());

    @FXML private TextField descriptionField;
    @FXML private TextField superficieField;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private TextField prixLocationField;
    @FXML private CheckBox disponibiliteCheckBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private TerrainService terrainService = new TerrainService();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Ajouter Terrain");
    }

    @FXML
    private void handleSave() {
        try {
            String description = descriptionField.getText().trim();
            if (description.isEmpty()) {
                showAlert("Erreur", "La description ne peut pas être vide.");
                return;
            }

            double superficie;
            try {
                superficie = Double.parseDouble(superficieField.getText().trim());
                if (superficie <= 0) {
                    showAlert("Erreur", "La superficie doit être positive.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "La superficie doit être un nombre valide.");
                return;
            }

            Double latitude = latitudeField.getText().isEmpty() ? null : Double.parseDouble(latitudeField.getText().trim());
            Double longitude = longitudeField.getText().isEmpty() ? null : Double.parseDouble(longitudeField.getText().trim());

            double prixLocation;
            try {
                prixLocation = Double.parseDouble(prixLocationField.getText().trim());
                if (prixLocation <= 0) {
                    showAlert("Erreur", "Le prix de location doit être positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Le prix de location doit être un nombre valide.");
                return;
            }

            Terrain terrain = new Terrain(
                    0, null, description, superficie, latitude, longitude, prixLocation,
                    disponibiliteCheckBox.isSelected(), new Date()
            );

            terrainService.ajouter(terrain);
            showAlert("Succès", "Terrain ajouté avec succès !");
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du terrain", e);
            showAlert("Erreur", "Impossible d'ajouter le terrain : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}