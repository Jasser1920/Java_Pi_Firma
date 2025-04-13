package Controllers;

import Models.Terrain;
import Services.TerrainService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditTerrainController {

    @FXML private TextField descriptionField;
    @FXML private TextField superficieField;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private TextField prixLocationField;
    @FXML private CheckBox disponibiliteCheckBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Terrain terrain;
    private TerrainService terrainService = new TerrainService();
    private Stage stage;

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
        populateFields();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Modifier Terrain");
    }

    private void populateFields() {
        descriptionField.setText(terrain.getDescription());
        superficieField.setText(String.valueOf(terrain.getSuperficie()));
        latitudeField.setText(terrain.getLatitude() != null ? String.valueOf(terrain.getLatitude()) : "");
        longitudeField.setText(terrain.getLongitude() != null ? String.valueOf(terrain.getLongitude()) : "");
        prixLocationField.setText(String.valueOf(terrain.getPrixLocation()));
        disponibiliteCheckBox.setSelected(terrain.isDisponibilite());
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

            terrain.setDescription(description);
            terrain.setSuperficie(superficie);
            terrain.setLatitude(latitude);
            terrain.setLongitude(longitude);
            terrain.setPrixLocation(prixLocation);
            terrain.setDisponibilite(disponibiliteCheckBox.isSelected());

            terrainService.modifier(terrain);
            showAlertAndClose("Succès", "Terrain modifié avec succès !");
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides.");
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de modifier le terrain : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        returnToList();
    }

    private void returnToList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TerrainList.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Liste des Terrains");
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(stage);
            newStage.show();
            stage.close();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à la liste : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertAndClose(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        if (stage != null) {
            stage.close();
        }
    }
}