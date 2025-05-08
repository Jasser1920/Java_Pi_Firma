package controllers;

import Models.Terrain;
import Services.TerrainService;
import javafx.application.Platform;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

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
    @FXML private WebView mapWebView;
    @FXML private Label weatherLabel;

    private boolean returnToLocationList = false;
    private TerrainService terrainService = new TerrainService();
    private WebEngine webEngine;

    public void setReturnToLocationList(boolean value) {
        this.returnToLocationList = value;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadTerrainData();
        initializeMap();
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

    private void initializeMap() {
        webEngine = mapWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        // Set WebView size to match the layout
        mapWebView.setPrefSize(740, 200);

        // Load map.html from resources
        String mapUrl = getClass().getResource("/map.html") != null ? getClass().getResource("/map.html").toExternalForm() : null;
        if (mapUrl == null) {
            webEngine.loadContent("<html><body><h1>Error: map.html not found</h1></body></html>");
            showAlert("Erreur", "Fichier map.html introuvable. Vérifiez le dossier src/main/resources/.");
        } else {
            webEngine.load(mapUrl);
        }

        // Set up JavaScript bridge when the map loads successfully
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("java", new JavaBridge());
                // Set initial marker at Tunis
                webEngine.executeScript("setMarker(36.8065, 10.1815);");
            } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                showAlert("Erreur", "Échec du chargement de la carte: " + webEngine.getLoadWorker().getException());
            }
        });

        // Initialize weather label
        weatherLabel.setText("Météo: Sélectionnez un terrain pour voir la météo.");
    }

    // JavaScript bridge to receive weather updates
    public class JavaBridge {
        public void onWeatherUpdate(JSObject weatherInfo) {
            Platform.runLater(() -> {
                String city = (String) weatherInfo.getMember("city");
                String temperature = String.valueOf(weatherInfo.getMember("temperature"));
                String description = (String) weatherInfo.getMember("description");
                weatherLabel.setText(String.format("Météo à %s: %s°C, %s", city, temperature, description));
            });
        }

        public void onWeatherError(String errorMessage) {
            Platform.runLater(() -> {
                weatherLabel.setText("Météo: " + errorMessage);
            });
        }
    }

    private void handleSelectTerrain(Terrain terrain) {
        try {
            // Check if terrain has valid coordinates
            if (terrain.getLatitude() != null && terrain.getLongitude() != null) {
                // Update map marker and fetch weather
                webEngine.executeScript(String.format("setMarker(%f, %f);", terrain.getLatitude(), terrain.getLongitude()));
            } else {
                weatherLabel.setText("Météo: Coordonnées non disponibles pour ce terrain.");
            }

            // Open AjouterLocation.fxml
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