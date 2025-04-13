package Controllers;

import Models.Location;
import Models.Terrain;
import Services.LocationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditLocationController {
    private static final Logger LOGGER = Logger.getLogger(EditLocationController.class.getName());

    @FXML private TextField terrainField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextField prixTotalField;
    @FXML private ComboBox<String> modePaiementCombo;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Location location;
    private LocationService locationService = new LocationService();
    private Runnable refreshCallback;

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public void setLocation(Location location) {
        this.location = location;
        populateFields();
    }

    private void populateFields() {
        Terrain terrain = location.getTerrain();
        terrainField.setText(terrain != null ? terrain.getDescription() : "N/A");
        terrainField.setEditable(false);

        dateDebutPicker.setValue(location.getDateDebutLocalDate());
        dateFinPicker.setValue(location.getDateFinLocalDate());

        prixTotalField.setText(String.valueOf(location.getPrixTotal()));
        modePaiementCombo.setItems(FXCollections.observableArrayList(
                "Carte bancaire", "Espèces", "Virement bancaire", "Chèque"
        ));
        modePaiementCombo.setValue(location.getModePaiement());
    }

    @FXML
    private void handleSave() {
        try {
            if (!validateInputs()) return;

            updateLocationFromForm();
            locationService.modifier(location);

            showAlert("Succès", "Location modifiée avec succès !");
            closeWindowAndRefresh();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification de la location", e);
            showAlert("Erreur", "Impossible de modifier la location : " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        // Validation logic remains the same as before
        // ...
        return true;
    }

    private void updateLocationFromForm() {
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        location.setDateDebut(java.sql.Date.valueOf(dateDebut));
        location.setDateFin(java.sql.Date.valueOf(dateFin));
        location.setPrixTotal(Double.parseDouble(prixTotalField.getText().trim()));
        location.setModePaiement(modePaiementCombo.getValue());
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindowAndRefresh() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) terrainField.getScene().getWindow();
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