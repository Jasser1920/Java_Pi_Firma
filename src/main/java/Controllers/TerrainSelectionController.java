package Controllers;

import Models.Terrain;
import Services.TerrainService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TerrainSelectionController {

    @FXML private TableView<Terrain> terrainTable;
    @FXML private TableColumn<Terrain, Integer> idColumn;
    @FXML private TableColumn<Terrain, String> descriptionColumn;
    @FXML private TableColumn<Terrain, Double> superficieColumn;
    @FXML private TableColumn<Terrain, Double> prixLocationColumn;
    @FXML private TableColumn<Terrain, Boolean> disponibiliteColumn;
    @FXML private TableColumn<Terrain, Void> actionColumn;
    @FXML private Button cancelButton;

    private boolean returnToLocationList = false;
    private TerrainService terrainService = new TerrainService();

    public void setReturnToLocationList(boolean value) {
        this.returnToLocationList = value;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadTerrainData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        superficieColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSuperficie()).asObject());
        prixLocationColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixLocation()).asObject());
        disponibiliteColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDisponibilite()));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button selectButton = new Button("Sélectionner");
            private final HBox hBox = new HBox(selectButton);

            {
                selectButton.setOnAction(event -> {
                    Terrain terrain = getTableView().getItems().get(getIndex());
                    handleSelectTerrain(terrain);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hBox);
            }
        });
    }

    private void loadTerrainData() {
        try {
            List<Terrain> terrains = terrainService.rechercher();
            terrainTable.setItems(FXCollections.observableArrayList(terrains));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les terrains : " + e.getMessage());
        }
    }

    private void handleSelectTerrain(Terrain terrain) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterLocation.fxml"));
            Parent root = loader.load();

            AjouterLocationController controller = loader.getController();
            controller.setTerrain(terrain);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Location");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(terrainTable.getScene().getWindow());
            stage.showAndWait();

            ((Stage) terrainTable.getScene().getWindow()).close();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout de location");
        }
    }

    @FXML
    private void handleCancel() {
        if (returnToLocationList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationList.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Liste des Locations");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de retourner à la liste des locations");
            }
        }
        ((Stage) terrainTable.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}