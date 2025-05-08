package controllers;

import Models.Evenemment;
import Models.Participation;
import Models.Utilisateur;
import Services.EvenemmentService;
import Services.ParticipationService;
import Utils.QRCodeAPI;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherEvenementController {


    @FXML
    private Button btnParticiper;
    @FXML
    private Button btnCalendrier;
    @FXML
    private TableView<Evenemment> evenementTable;
    @FXML
    private TableColumn<Evenemment, String> titreColumn;
    @FXML
    private TableColumn<Evenemment, String> descriptionColumn;
    @FXML
    private TableColumn<Evenemment, String> dateColumn;
    @FXML
    private TableColumn<Evenemment, String> lieuxColumn;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnVoir;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnDons;
    @FXML
    private Button btnFiltrer;
    @FXML
    private Button btnReset;
    @FXML
    private TextField titreField;
    @FXML
    private TextField lieuxField;
    @FXML
    private DatePicker dateMinPicker;
    @FXML
    private DatePicker dateMaxPicker;
    @FXML
    private Button homeFX;
    @FXML
    private TableColumn<Evenemment, Void> chronoColumn;


    private final EvenemmentService service = new EvenemmentService();
    private List<Evenemment> tousLesEvenements;
    private Utilisateur utilisateurConnecte;


    @FXML
    public void initialize() {
        titreColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitre()));
        descriptionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDesecription()));
        dateColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return new javafx.beans.property.SimpleStringProperty(sdf.format(cellData.getValue().getDate()));
        });
        lieuxColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLieux()));
        chronoColumn.setCellFactory(col -> new TableCell<>() {
            private final ImageView chronoIcon = new ImageView(getClass().getResource("/icons/chrono.png").toExternalForm());
            private final Tooltip tooltip = new Tooltip();

            {
                chronoIcon.setFitWidth(24);
                chronoIcon.setFitHeight(24);
                setGraphic(chronoIcon);
                setTooltip(tooltip);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                try {
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                        setTooltip(null);
                    } else {
                        Evenemment ev = getTableRow().getItem();
                        LocalDate eventDate = ((java.sql.Date) ev.getDate()).toLocalDate();
                        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), eventDate);

                        tooltip.setText(daysLeft >= 0 ? daysLeft + " jour(s) restant(s)" : "Événement passé");


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setText("");
                    setTooltip(null);
                }
            }

        });
        btnAjouter.setOnAction(event -> ouvrirFormulaireAjout());
        btnVoir.setOnAction(event -> voirEvenement());
        btnSupprimer.setOnAction(event -> supprimerEvenement());
        btnDons.setOnAction(event -> ouvrirPageDons());
        btnFiltrer.setOnAction(event -> appliquerFiltres());
        btnReset.setOnAction(event -> reinitialiserFiltres());

        rafraichirTable();
    }

    public void rafraichirTable() {
        tousLesEvenements = service.rechercher();
        evenementTable.setItems(FXCollections.observableArrayList(tousLesEvenements));
    }

    private void appliquerFiltres() {
        String titre = titreField.getText().toLowerCase();
        String lieux = lieuxField.getText().toLowerCase();
        LocalDate minDate = dateMinPicker.getValue();
        LocalDate maxDate = dateMaxPicker.getValue();

        List<Evenemment> filtres = tousLesEvenements.stream().filter(ev -> {
            boolean matchTitre = titre.isEmpty() || ev.getTitre().toLowerCase().contains(titre);
            boolean matchLieu = lieux.isEmpty() || ev.getLieux().toLowerCase().contains(lieux);
            boolean matchMinDate = minDate == null || !ev.getDate().before(java.sql.Date.valueOf(minDate));
            boolean matchMaxDate = maxDate == null || !ev.getDate().after(java.sql.Date.valueOf(maxDate));
            return matchTitre && matchLieu && matchMinDate && matchMaxDate;
        }).collect(Collectors.toList());

        evenementTable.setItems(FXCollections.observableArrayList(filtres));
    }

    private void reinitialiserFiltres() {
        titreField.clear();
        lieuxField.clear();
        dateMinPicker.setValue(null);
        dateMaxPicker.setValue(null);
        evenementTable.setItems(FXCollections.observableArrayList(tousLesEvenements));
    }

    private void ouvrirFormulaireAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvenement.fxml"));
            Parent root = loader.load();
            AjouterEvenementController controller = loader.getController();
            controller.setAfficherEvenementController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un événement");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors de l'ouverture du formulaire d'ajout.");
        }
    }

    private void voirEvenement() {
        Evenemment selected = evenementTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Veuillez sélectionner un événement à consulter.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailEvenement.fxml"));
            Parent root = loader.load();
            DetailEvenementController controller = loader.getController();
            controller.setEvenement(selected);
            controller.setAfficherEvenementController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détail de l'événement");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors de l'ouverture des détails de l'événement.");
        }
    }

    private void supprimerEvenement() {
        Evenemment selected = evenementTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Veuillez sélectionner un événement à supprimer.");
            return;
        }
        try {
            service.supprimer(selected);
            showSuccess("Événement supprimé avec succès.");
            rafraichirTable();
        } catch (Exception e) {
            showAlert("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private void ouvrirPageDons() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDon.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnDons.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Dons");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors de l'ouverture de la page des dons.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alerte");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    @FXML
    private void ouvrirCalendrier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CalendrierEvenement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Calendrier des événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }


    @FXML
    private void participerEvenement() {
        Evenemment selected = evenementTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun événement sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un événement pour participer.");
            alert.showAndWait();
            return;
        }

        try {
            String contenuQR = "📅 Événement : " + selected.getTitre() + "\n" +
                    "📝 Description : " + selected.getDesecription() + "\n" +
                    "📍 Lieu : " + selected.getLieux() + "\n" +
                    "📆 Date : " + selected.getDate();

            // Générer l’image QR (on ajoutera la méthode plus tard si pas encore faite)
            Image qrImage = QRCodeAPI.generateQRCode(contenuQR, 250, 250);

            ParticipationService participationService = new ParticipationService();
            Participation participation = new Participation(
                    0, // id auto-incrémenté
                    utilisateurConnecte,
                    selected,
                    new java.util.Date()
            );
            participationService.ajouter(participation);

            // Charger la page AfficherQR.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherQR.fxml"));
            Parent root = loader.load();

            // Passer les données au contrôleur QR
            AfficherQRController qrController = loader.getController();
            String titre = selected.getTitre();
            String emailParticipant = utilisateurConnecte.getEmail();
            String prenom = utilisateurConnecte.getPrenom();
            qrController.setQRCodeData(qrImage, contenuQR, titre, emailParticipant, prenom);



            // Ouvrir une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("QR de participation");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de générer le QR");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }








}
