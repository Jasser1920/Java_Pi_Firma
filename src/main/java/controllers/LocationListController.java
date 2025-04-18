package controllers;

import Models.Location;
import Models.Terrain;
import Services.LocationService;
import Services.TerrainService;
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

public class LocationListController {

    @FXML private TableView<Location> locationTable;
    @FXML private TableColumn<Location, Integer> idColumn;
    @FXML private TableColumn<Location, String> terrainColumn;
    @FXML private TableColumn<Location, Date> dateDebutColumn;
    @FXML private TableColumn<Location, Date> dateFinColumn;
    @FXML private TableColumn<Location, Double> prixTotalColumn;
    @FXML private TableColumn<Location, String> modePaiementColumn;
    @FXML private TableColumn<Location, Void> actionColumn;
    @FXML private Button createLocationButton;

    private LocationService locationService = new LocationService();
    private TerrainService terrainService = new TerrainService();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadLocationData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        terrainColumn.setCellValueFactory(cellData -> {
            Terrain terrain = cellData.getValue().getTerrain();
            return new SimpleStringProperty(terrain != null ? terrain.getDescription() : "N/A");
        });
        dateDebutColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateDebut()));
        dateFinColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateFin()));
        prixTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixTotal()).asObject());
        modePaiementColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModePaiement()));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox hBox = new HBox(editButton, deleteButton);

            {
                hBox.setSpacing(5);
                editButton.setOnAction(event -> {
                    Location location = getTableView().getItems().get(getIndex());
                    handleEditLocation(location);
                });
                deleteButton.setOnAction(event -> {
                    Location location = getTableView().getItems().get(getIndex());
                    handleDeleteLocation(location);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hBox);
            }
        });
    }

    public void loadLocationData() {
        try {
            List<Location> locations = locationService.rechercher();
            locationTable.setItems(FXCollections.observableArrayList(locations));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les locations : " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateLocation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TerrainSelection.fxml"));
            Parent root = loader.load();

            TerrainSelectionController controller = loader.getController();
            controller.setReturnToLocationList(true);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sélectionner un Terrain");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(createLocationButton.getScene().getWindow());
            stage.showAndWait();

            loadLocationData();
        } catch (IOException e) {
            Logger.getLogger(LocationListController.class.getName()).log(Level.SEVERE, null, e);
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de sélection de terrain");
        }
    }

    private void handleEditLocation(Location location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditLocation.fxml"));
            Parent root = loader.load();

            EditLocationController controller = loader.getController();
            controller.setLocation(location);
            controller.setRefreshCallback(this::loadLocationData);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Location");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(locationTable.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            Logger.getLogger(LocationListController.class.getName()).log(Level.SEVERE, null, e);
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification");
        }
    }

    private void handleDeleteLocation(Location location) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la location");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette location ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    locationService.supprimer(location);
                    Terrain terrain = location.getTerrain();
                    if (terrain != null) {
                        terrain.setDisponibilite(true);
                        terrainService.modifier(terrain);
                    }
                    loadLocationData();
                    showAlert("Succès", "Location supprimée avec succès !");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de supprimer la location : " + e.getMessage());
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
    @FXML private Button homeFX;
    @FXML
    private void ouvrirHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeFX.getScene().getWindow(); // remplace la scène actuelle
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}