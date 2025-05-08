package controllers;

import Models.Terrain;
import Services.TerrainService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TerrainListController {
    private static final Logger LOGGER = Logger.getLogger(TerrainListController.class.getName());

    @FXML private TableView<Terrain> terrainTable;
    @FXML private TableColumn<Terrain, String> descriptionColumn;
    @FXML private TableColumn<Terrain, Double> superficieColumn;
    @FXML private TableColumn<Terrain, Double> latitudeColumn;
    @FXML private TableColumn<Terrain, Double> longitudeColumn;
    @FXML private TableColumn<Terrain, Double> prixLocationColumn;
    @FXML private TableColumn<Terrain, Boolean> disponibiliteColumn;
    @FXML private TableColumn<Terrain, Date> dateCreationColumn;
    @FXML private Button createTerrainButton;
    @FXML private Button homeFX;
    @FXML private TextField searchDescriptionField;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private Button filterButton;
    @FXML private Button clearFilterButton;
    @FXML private PieChart priceChart;

    private TerrainService terrainService = new TerrainService();
    private ObservableList<Terrain> terrainList;

    @FXML
    public void initialize() {
        System.out.println("✅ initialize() lancé dans TerrainListController !");
        setupTableColumns();
        loadTerrainData();
        setupSearchAndFilter();
        setupPriceChart();
    }

    private void setupTableColumns() {
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        superficieColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSuperficie()).asObject());
        latitudeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLatitude()));
        longitudeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLongitude()));
        prixLocationColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixLocation()).asObject());
        disponibiliteColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDisponibilite()));
        dateCreationColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateCreation()));
    }

    private void loadTerrainData() {
        try {
            List<Terrain> terrains = terrainService.rechercher();
            terrainList = FXCollections.observableArrayList(terrains);
            terrainTable.setItems(terrainList);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les terrains : " + e.getMessage());
        }
    }

    private void setupSearchAndFilter() {
        searchDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                terrainTable.setItems(terrainList);
            } else {
                ObservableList<Terrain> filteredList = FXCollections.observableArrayList();
                for (Terrain terrain : terrainList) {
                    if (terrain.getDescription().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(terrain);
                    }
                }
                terrainTable.setItems(filteredList);
            }
        });
    }

    @FXML
    private void handleFilter() {
        String minPriceText = minPriceField.getText().trim();
        String maxPriceText = maxPriceField.getText().trim();

        double minPrice = Double.MIN_VALUE;
        double maxPrice = Double.MAX_VALUE;

        try {
            if (!minPriceText.isEmpty()) {
                minPrice = Double.parseDouble(minPriceText);
            }
            if (!maxPriceText.isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceText);
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour les prix.");
            return;
        }

        if (minPrice > maxPrice) {
            showAlert("Erreur", "Le prix minimum ne peut pas être supérieur au prix maximum.");
            return;
        }

        ObservableList<Terrain> filteredList = FXCollections.observableArrayList();
        for (Terrain terrain : terrainList) {
            double price = terrain.getPrixLocation();
            if (price >= minPrice && price <= maxPrice) {
                filteredList.add(terrain);
            }
        }
        terrainTable.setItems(filteredList);
    }

    @FXML
    private void handleClearFilter() {
        searchDescriptionField.clear();
        minPriceField.clear();
        maxPriceField.clear();
        terrainTable.setItems(terrainList);
    }

    private void setupPriceChart() {
        priceChart.getData().clear();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        if (terrainList.isEmpty()) {
            priceChart.setData(pieChartData);
            return;
        }

        List<Double> prices = new ArrayList<>();
        for (Terrain terrain : terrainList) {
            prices.add(terrain.getPrixLocation());
        }
        double minPrice = Collections.min(prices);
        double maxPrice = Collections.max(prices);

        if (minPrice == maxPrice) {
            pieChartData.add(new PieChart.Data(String.format("%.0f €", minPrice), terrainList.size()));
            priceChart.setData(pieChartData);
            return;
        }

        int numRanges = 5;
        double rangeSize = (maxPrice - minPrice) / numRanges;
        double[] priceRanges = new double[numRanges + 1];
        String[] rangeLabels = new String[numRanges];
        int[] counts = new int[numRanges];

        for (int i = 0; i <= numRanges; i++) {
            priceRanges[i] = minPrice + (i * rangeSize);
        }

        for (int i = 0; i < numRanges; i++) {
            rangeLabels[i] = String.format("%.0f-%.0f €", priceRanges[i], priceRanges[i + 1]);
        }

        for (Terrain terrain : terrainList) {
            double price = terrain.getPrixLocation();
            for (int i = 0; i < numRanges; i++) {
                if (price >= priceRanges[i] && price < priceRanges[i + 1]) {
                    counts[i]++;
                    break;
                }
            }
        }

        for (int i = 0; i < numRanges; i++) {
            if (counts[i] > 0) {
                pieChartData.add(new PieChart.Data(rangeLabels[i], counts[i]));
            }
        }

        priceChart.setData(pieChartData);
    }

    @FXML
    private void handleCreateTerrain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddTerrainMap.fxml"));
            Parent root = loader.load();
            AddTerrainMapController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Terrain avec Carte");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(terrainTable.getScene().getWindow());
            stage.showAndWait();
            loadTerrainData();
            setupPriceChart();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture de la fenêtre d'ajout de terrain", e);
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void handleEditTerrainFromSidebar() {
        Terrain selectedTerrain = terrainTable.getSelectionModel().getSelectedItem();
        if (selectedTerrain == null) {
            showAlert("Erreur", "Veuillez sélectionner un terrain à modifier.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditTerrain.fxml"));
            Parent root = loader.load();
            EditTerrainController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.setTerrain(selectedTerrain);
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier une Terre");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(terrainTable.getScene().getWindow());
            stage.showAndWait();
            loadTerrainData();
            setupPriceChart();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture de la fenêtre de modification de terrain", e);
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification : " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteTerrainFromSidebar() {
        Terrain selectedTerrain = terrainTable.getSelectionModel().getSelectedItem();
        if (selectedTerrain == null) {
            showAlert("Erreur", "Veuillez sélectionner un terrain à supprimer.");
            return;
        }
        handleDeleteTerrain(selectedTerrain);
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
                    setupPriceChart();
                    showAlert("Succès", "Terrain supprimé avec succès !");
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de supprimer le terrain : " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleRentTerrainFromSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RentTerrain.fxml"));
            Parent root = loader.load();
            RentTerrainController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setScene(new Scene(root));
            stage.setTitle("Louer un Terrain");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(terrainTable.getScene().getWindow());
            stage.showAndWait();
            loadTerrainData();
            setupPriceChart();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture de la fenêtre de location", e);
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de location : " + e.getMessage());
        }
    }

    @FXML
    private void handleShowLocationList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationList.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Locations");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(terrainTable.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture de la liste des locations", e);
            showAlert("Erreur", "Impossible d'ouvrir la liste des locations : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void ouvrirHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeFX.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture de la fenêtre Home", e);
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre Home : " + e.getMessage());
        }
    }
}