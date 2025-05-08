package controllers;

import Models.Terrain;
import Services.TerrainService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RentTerrainController {

    @FXML private ChoiceBox<Terrain> terrainChoiceBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button rentButton;
    @FXML private Button cancelButton;

    private TerrainService terrainService = new TerrainService();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        loadTerrains();
    }

    private void loadTerrains() {
        try {
            List<Terrain> terrains = terrainService.rechercher();
            terrainChoiceBox.getItems().addAll(terrains);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les terrains : " + e.getMessage());
        }
    }

    @FXML
    private void handleRent() {
        Terrain selectedTerrain = terrainChoiceBox.getValue();
        if (selectedTerrain == null) {
            showAlert("Erreur", "Veuillez sélectionner un terrain.");
            return;
        }
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner les dates de début et de fin.");
            return;
        }
        if (!selectedTerrain.isDisponibilite()) {
            showAlert("Erreur", "Ce terrain n'est pas disponible pour la location.");
            return;
        }

        // Placeholder for rental logic (e.g., save to database)
        showAlert("Succès", "Terrain loué avec succès de " + startDatePicker.getValue() + " à " + endDatePicker.getValue() + " !");
        stage.close();
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}