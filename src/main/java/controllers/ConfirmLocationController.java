package controllers;

import Models.Location;
import Models.Terrain;
import Services.LocationService;
import Services.TerrainService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfirmLocationController {
    private static final Logger LOGGER = Logger.getLogger(ConfirmLocationController.class.getName());

    @FXML private Button rentButton;

    private Terrain terrain;
    private Stage stage;
    private LocationService locationService = new LocationService();
    private TerrainService terrainService = new TerrainService();

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
        LOGGER.info("Terrain défini : " + (terrain != null ? terrain.getDescription() : "null"));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        LOGGER.info("Stage défini pour ConfirmLocationController");
    }

    @FXML
    private void handleRent() {
        LOGGER.info("Bouton Louer cliqué");
        try {
            if (terrain == null) {
                showAlert("Erreur", "Aucun terrain sélectionné.");
                return;
            }

            // Créer une nouvelle location avec des valeurs par défaut
            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusDays(7); // Location pour 1 semaine
            Date dateDebut = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date dateFin = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            double prixTotal = 7 * terrain.getPrixLocation(); // Prix pour 7 jours
            String modePaiement = "Carte bancaire"; // Mode de paiement par défaut

            Location location = new Location(
                    0, // ID généré automatiquement
                    terrain,
                    dateDebut,
                    dateFin,
                    prixTotal,
                    modePaiement
            );

            // Ajouter la location
            LOGGER.info("Ajout de la location : " + location.getTerrain().getDescription());
            locationService.ajouter(location);

            // Marquer le terrain comme indisponible
            LOGGER.info("Mise à jour de la disponibilité du terrain : " + terrain.getDescription());
            terrain.setDisponibilite(false);
            terrainService.modifier(terrain);

            showAlert("Succès", "Location créée avec succès !");

            // Rediriger vers la liste des locations
            goToLocationList();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la location", e);
            showAlert("Erreur", "Impossible de créer la location : " + e.getMessage());
        }
    }

    private void goToLocationList() {
        try {
            LOGGER.info("Redirection vers la liste des locations");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationList.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Liste des Locations");
            newStage.show();

            // Fermer la fenêtre actuelle et la fenêtre de la liste des terrains
            stage.close();
            Stage parentStage = (Stage) stage.getOwner();
            parentStage.close();
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
}