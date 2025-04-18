package controllers;

import Models.Location;
import Models.Terrain;
import Services.LocationService;
import Services.TerrainService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AjouterLocationController implements Initializable {

    @FXML private TextField terrainField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextField prixTotalField;
    @FXML private ComboBox<String> modePaiementComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private LocationService locationService = new LocationService();
    private TerrainService terrainService = new TerrainService();
    private Terrain selectedTerrain;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modePaiementComboBox.setItems(FXCollections.observableArrayList(
                "Carte bancaire", "Espèces", "Virement bancaire", "Chèque"
        ));
        modePaiementComboBox.setPromptText("Sélectionnez un mode");

        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> calculatePrice());
        dateFinPicker.valueProperty().addListener((obs, oldVal, newVal) -> calculatePrice());
    }

    public void setTerrain(Terrain terrain) {
        this.selectedTerrain = terrain;
        this.terrainField.setText(terrain.getDescription());
    }

    private void calculatePrice() {
        if (selectedTerrain != null && dateDebutPicker.getValue() != null && dateFinPicker.getValue() != null) {
            long daysBetween = ChronoUnit.DAYS.between(dateDebutPicker.getValue(), dateFinPicker.getValue());
            double prixTotal = daysBetween * selectedTerrain.getPrixLocation();
            prixTotalField.setText(String.valueOf(prixTotal));
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (selectedTerrain == null) {
                showAlert("Erreur", "Aucun terrain sélectionné.");
                return;
            }
            if (!selectedTerrain.isDisponibilite()) {
                showAlert("Erreur", "Ce terrain n'est pas disponible pour la location.");
                return;
            }

            LocalDate dateDebutLocal = dateDebutPicker.getValue();
            if (dateDebutLocal == null) {
                showAlert("Erreur", "Veuillez sélectionner une date de début.");
                return;
            }
            Date dateDebut = Date.from(dateDebutLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

            LocalDate dateFinLocal = dateFinPicker.getValue();
            if (dateFinLocal == null) {
                showAlert("Erreur", "Veuillez sélectionner une date de fin.");
                return;
            }
            Date dateFin = Date.from(dateFinLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

            if (dateFin.before(dateDebut)) {
                showAlert("Erreur", "La date de fin doit être postérieure à la date de début.");
                return;
            }

            String modePaiement = modePaiementComboBox.getValue();
            if (modePaiement == null) {
                showAlert("Erreur", "Veuillez sélectionner un mode de paiement.");
                return;
            }

            Location location = new Location(
                    0,
                    selectedTerrain,
                    dateDebut,
                    dateFin,
                    Double.parseDouble(prixTotalField.getText()),
                    modePaiement
            );

            locationService.ajouter(location);
            selectedTerrain.setDisponibilite(false);
            terrainService.modifier(selectedTerrain);

            showAlert("Succès", "Location créée avec succès !");
            goToLocationList();
        } catch (SQLException e) {
            Logger.getLogger(AjouterLocationController.class.getName()).log(Level.SEVERE, null, e);
            showAlert("Erreur", "Erreur lors de la création de la location : " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix total invalide.");
        }
    }

    @FXML
    private void handleCancel() {
        goToLocationList();
    }

    private void goToLocationList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationList.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Locations");
            stage.show();

            ((Stage) saveButton.getScene().getWindow()).close();
        } catch (IOException e) {
            Logger.getLogger(AjouterLocationController.class.getName()).log(Level.SEVERE, null, e);
            showAlert("Erreur", "Erreur lors du retour à la liste des locations");
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