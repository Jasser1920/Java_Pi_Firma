package controllers;

import Models.Don;
import Models.Evenemment;
import Services.DonService;
import Services.EvenemmentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherDonController {

    @FXML private TableView<Don> donTable;
    @FXML private TableColumn<Don, Integer> idColumn;
    @FXML private TableColumn<Don, String> donateurColumn;
    @FXML private TableColumn<Don, String> descriptionColumn;
    @FXML private TableColumn<Don, String> dateColumn;
    @FXML private TableColumn<Don, String> evenementColumn;
    @FXML private Button btnAjouter;
    @FXML private Button btnVoir;
    @FXML private Button btnRetour;
    @FXML private Button btnFiltrer;
    @FXML private Button btnReset;
    @FXML private ComboBox<Evenemment> evenementFilterCombo;
    @FXML private DatePicker dateMinPicker;
    @FXML private DatePicker dateMaxPicker;
    @FXML private Button homeFX;
    @FXML private Label nbDonLabel;



    private final DonService donService = new DonService();
    private List<Don> tousLesDons;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        donateurColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDonateur()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        dateColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return new SimpleStringProperty(sdf.format(cellData.getValue().getDate()));
        });

        evenementColumn.setCellValueFactory(cellData -> {
            Evenemment ev = cellData.getValue().getEvenement();
            return new SimpleStringProperty(ev != null ? ev.getTitre() : "Aucun");
        });

        evenementFilterCombo.setItems(FXCollections.observableArrayList(new EvenemmentService().rechercher()));

        btnAjouter.setOnAction(event -> ouvrirAjout());
        btnVoir.setOnAction(event -> voirDon());
        btnRetour.setOnAction(event -> retournerEvenement());
        btnFiltrer.setOnAction(event -> appliquerFiltres());
        btnReset.setOnAction(event -> reinitialiserFiltres());

        rafraichirTable();
    }

    public void rafraichirTable() {
        tousLesDons = donService.rechercher();
        donTable.setItems(FXCollections.observableArrayList(tousLesDons));
        nbDonLabel.setText("Nombre de dons : " + tousLesDons.size());
    }

    private void appliquerFiltres() {
        Evenemment selectedEv = evenementFilterCombo.getValue();
        LocalDate minDate = dateMinPicker.getValue();
        LocalDate maxDate = dateMaxPicker.getValue();

        List<Don> filtres = tousLesDons.stream().filter(don -> {
            boolean matchEv = selectedEv == null || (don.getEvenement() != null && don.getEvenement().getId() == selectedEv.getId());
            boolean matchMinDate = minDate == null || !don.getDate().before(java.sql.Date.valueOf(minDate));
            boolean matchMaxDate = maxDate == null || !don.getDate().after(java.sql.Date.valueOf(maxDate));
            return matchEv && matchMinDate && matchMaxDate;
        }).collect(Collectors.toList());

        donTable.setItems(FXCollections.observableArrayList(filtres));
        nbDonLabel.setText("Nombre de dons : " + filtres.size());
    }


    private void reinitialiserFiltres() {
        evenementFilterCombo.getSelectionModel().clearSelection();
        dateMinPicker.setValue(null);
        dateMaxPicker.setValue(null);
        donTable.setItems(FXCollections.observableArrayList(tousLesDons));
    }

    @FXML
    private void ouvrirAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterDon.fxml"));
            Parent root = loader.load();

            AjouterDonController controller = loader.getController();
            controller.setAfficherDonController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Don");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur d'ouverture du formulaire d'ajout.");
        }
    }

    private void voirDon() {
        Don selected = donTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Veuillez sélectionner un don à consulter.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailDon.fxml"));
            Parent root = loader.load();

            DetailDonController controller = loader.getController();
            controller.setDon(selected);
            controller.setAfficherDonController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails du Don");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture des détails du don.");
        }
    }

    private void retournerEvenement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvenement.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du retour aux événements.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alerte");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private java.time.LocalDate convertToLocalDateViaSqlDate(java.sql.Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

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
            showAlert("Erreur lors de l'ouverture de la page d'accueil.");
        }
    }


}
