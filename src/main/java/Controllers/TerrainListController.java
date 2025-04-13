package Controllers;

import Models.Terrain;
import Services.TerrainService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TerrainListController {
    private static final Logger LOGGER = Logger.getLogger(TerrainListController.class.getName());

    @FXML private TableView<Terrain> terrainTable;
    @FXML private TableColumn<Terrain, Integer> idColumn;
    @FXML private TableColumn<Terrain, String> descriptionColumn;
    @FXML private TableColumn<Terrain, Double> superficieColumn;
    @FXML private TableColumn<Terrain, Double> latitudeColumn;
    @FXML private TableColumn<Terrain, Double> longitudeColumn;
    @FXML private TableColumn<Terrain, Double> prixLocationColumn;
    @FXML private TableColumn<Terrain, Boolean> disponibiliteColumn;
    @FXML private TableColumn<Terrain, Date> dateCreationColumn;
    @FXML private TableColumn<Terrain, Void> actionColumn;
    @FXML private Button createTerrainButton;

    private TerrainService terrainService = new TerrainService();

    @FXML
    public void initialize() {
        System.out.println("✅ initialize() lancé dans TerrainListController !");
        setupTableColumns();
        loadTerrainData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        superficieColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSuperficie()).asObject());
        latitudeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLatitude()));
        longitudeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLongitude()));
        prixLocationColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixLocation()).asObject());
        disponibiliteColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDisponibilite()));
        dateCreationColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateCreation()));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox hBox = new HBox(editButton, deleteButton);

            {
                hBox.setSpacing(5);
                editButton.setOnAction(event -> {
                    Terrain terrain = getTableView().getItems().get(getIndex());
                    handleEditTerrain(terrain);
                });
                deleteButton.setOnAction(event -> {
                    Terrain terrain = getTableView().getItems().get(getIndex());
                    handleDeleteTerrain(terrain);
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

    @FXML
    private void handleCreateTerrain() {
        try {
            URL resource = getClass().getResource("/AjouterTerrain.fxml");
            if (resource == null) {
                throw new IOException("Fichier FXML 'AjouterTerrain.fxml' non trouvé dans les ressources.");
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            AjouterTerrainController controller = loader.getController();
            controller.setStage(stage);
            stage.initOwner(terrainTable.getScene().getWindow());
            stage.showAndWait();
            loadTerrainData();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture de la fenêtre d'ajout de terrain", e);
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout : " + e.getMessage());
        }
    }

    private void handleEditTerrain(Terrain terrain) {
        try {
            URL resource = getClass().getResource("/EditTerrain.fxml");
            if (resource == null) {
                throw new IOException("Fichier FXML 'EditTerrain.fxml' non trouvé dans les ressources.");
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            EditTerrainController controller = loader.getController();
            controller.setTerrain(terrain);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(terrainTable.getScene().getWindow());
            stage.showAndWait();
            loadTerrainData(); // Refresh the list after the edit window closes
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture de la fenêtre de modification de terrain", e);
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification : " + e.getMessage());
        }
    }

    private void handleDeleteTerrain(Terrain terrain) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le terrain");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce terrain ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    terrainService.supprimer(terrain);
                    loadTerrainData();
                    showAlert("Succès", "Terrain supprimé avec succès !");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de supprimer le terrain : " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}