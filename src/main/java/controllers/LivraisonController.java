package controllers;

import Models.Livraison;
import Models.Commande;
import Models.CommandeProduit;
import Models.StatutLivraison;
import Services.CommandeService;
import Services.LivraisonService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class LivraisonController {
    @FXML private TextField nomSocieteField;
    @FXML private TextField adresseField;
    @FXML private DatePicker dateLivraisonPicker;
    @FXML private ComboBox<StatutLivraison> statutCombo;
    @FXML private TableView<Livraison> livraisonsTable;
    @FXML private TableColumn<Livraison, Integer> idColumn;
    @FXML private TableColumn<Livraison, String> nomSocieteColumn;
    @FXML private TableColumn<Livraison, String> adresseColumn;
    @FXML private TableColumn<Livraison, Date> dateColumn;
    @FXML private TableColumn<Livraison, StatutLivraison> statutColumn;
    @FXML private TableColumn<Livraison, Void> actionColumn;
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnRetour;
    private LivraisonService livraisonService = new LivraisonService();
    private Livraison livraisonToModify;
    private Commande commandeTemp; // Commande temporaire passée depuis CommandeController
    private CommandeController sourceController; // Référence au CommandeController

    @FXML
    public void initialize() {
        // Initialiser les colonnes du TableView uniquement si elles existent (pour afficherlivraison.fxml)
        if (livraisonsTable != null && idColumn != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nomSocieteColumn.setCellValueFactory(new PropertyValueFactory<>("nomSociete"));
            adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresseLivraison"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateLivraison"));
            statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

            // Initialiser la colonne d'actions
            actionColumn.setCellFactory(col -> new TableCell<Livraison, Void>() {
                private final Button modifierButton = new Button("Modifier");
                private final Button supprimerButton = new Button("Supprimer");

                {
                    modifierButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    supprimerButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

                    modifierButton.setOnAction(event -> {
                        Livraison livraison = getTableView().getItems().get(getIndex());
                        setLivraisonToModify(livraison);
                        goToModifierLivraison();
                    });

                    supprimerButton.setOnAction(event -> {
                        Livraison livraison = getTableView().getItems().get(getIndex());
                        supprimerLivraison(livraison);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(new HBox(5, modifierButton, supprimerButton));
                    }
                }
            });

            // Charger les données dans le TableView
            loadData();
        }

        // Initialiser le ComboBox avec les statuts uniquement s'il existe (pour ajouterlivraison.fxml et modifierlivraison.fxml)
        if (statutCombo != null) {
            statutCombo.setItems(FXCollections.observableArrayList(StatutLivraison.values()));
            statutCombo.setValue(StatutLivraison.en_cours); // Valeur par défaut
        }

        // Pré-remplir les champs si on est en mode modification
        if (livraisonToModify != null) {
            preFillFields();
        }
    }

    private void loadData() {
        livraisonsTable.setItems(FXCollections.observableArrayList(livraisonService.rechercher()));
    }

    @FXML
    private void ajouterLivraison() {
        // Vérifier que les champs ne sont pas vides
        if (nomSocieteField.getText().trim().isEmpty() || adresseField.getText().trim().isEmpty() ||
                dateLivraisonPicker.getValue() == null || statutCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        // Vérifier que la date de livraison est supérieure à aujourd'hui
        LocalDate today = LocalDate.now();
        LocalDate dateLivraison = dateLivraisonPicker.getValue();
        if (dateLivraison.isBefore(today) || dateLivraison.isEqual(today)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de livraison doit être supérieure à la date d'aujourd'hui.");
            return;
        }

        Livraison livraison = new Livraison(
                0,
                nomSocieteField.getText().trim(),
                adresseField.getText().trim(),
                Date.from(dateLivraisonPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                statutCombo.getValue()
        );
        livraisonService.ajouter(livraison);
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Livraison ajoutée avec succès.");
        goToAfficherLivraison();
    }

    @FXML
    private void ajouterLivraisonDepuisCommande() {
        System.out.println("Début de ajouterLivraisonDepuisCommande");
        if (nomSocieteField.getText().trim().isEmpty() || adresseField.getText().trim().isEmpty() ||
                dateLivraisonPicker.getValue() == null || statutCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate dateLivraison = dateLivraisonPicker.getValue();
        if (dateLivraison.isBefore(today) || dateLivraison.isEqual(today)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de livraison doit être supérieure à la date d'aujourd'hui.");
            return;
        }

        Connection connection = null;
        try {
            connection = Utils.MyDatabase.getInstance().getConnection();
            connection.setAutoCommit(false);

            Livraison livraison = new Livraison(
                    0,
                    nomSocieteField.getText().trim(),
                    adresseField.getText().trim(),
                    Date.from(dateLivraisonPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    statutCombo.getValue()
            );
            System.out.println("Ajout de la livraison : " + livraison.getNomSociete());
            livraisonService.ajouter(livraison);
            System.out.println("Livraison ajoutée avec ID : " + livraison.getId());

            if (livraison.getId() == 0) {
                throw new RuntimeException("L'ID de la livraison n'a pas été généré correctement");
            }

            commandeTemp.setLivraison(livraison);
            CommandeService commandeService = new CommandeService();
            System.out.println("Ajout de la commande avec livraison ID : " + (livraison != null ? livraison.getId() : "null"));
            commandeService.ajouter(commandeTemp);
            System.out.println("Commande ajoutée avec succès");

            connection.commit();

            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Commande et livraison ajoutées avec succès.");
            System.out.println("Redirection vers affichercommand.fxml");
            goToAfficherCommande();
            System.out.println("Redirection réussie");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement de la livraison ou de la commande : " + e.getMessage());
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                    System.out.println("Rollback effectué");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'enregistrement : " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            commandeTemp = null;
        }
    }

    @FXML
    private void modifierLivraison() {
        // Vérifier que les champs ne sont pas vides
        if (livraisonToModify == null || nomSocieteField.getText().trim().isEmpty() || adresseField.getText().trim().isEmpty() ||
                dateLivraisonPicker.getValue() == null || statutCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Sélectionnez une livraison et remplissez tous les champs.");
            return;
        }

        // Vérifier que la date de livraison est supérieure à aujourd'hui
        LocalDate today = LocalDate.now();
        LocalDate dateLivraison = dateLivraisonPicker.getValue();
        if (dateLivraison.isBefore(today) || dateLivraison.isEqual(today)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de livraison doit être supérieure à la date d'aujourd'hui.");
            return;
        }

        livraisonToModify.setNomSociete(nomSocieteField.getText().trim());
        livraisonToModify.setAdresseLivraison(adresseField.getText().trim());
        livraisonToModify.setDateLivraison(Date.from(dateLivraisonPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        livraisonToModify.setStatut(statutCombo.getValue());
        livraisonService.modifier(livraisonToModify);
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Livraison modifiée avec succès.");
        goToAfficherLivraison();
    }

    private void supprimerLivraison(Livraison livraison) {
        if (livraison != null) {
            livraisonService.supprimer(livraison);
            loadData();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Livraison supprimée avec succès.");
        }
    }

    @FXML
    private void goToAjouterLivraison() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterlivraison.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("Ajouter une Livraison");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de ajouterlivraison.fxml : " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la redirection : " + e.getMessage());
        }
    }

    @FXML
    private void goToAjouterCommande() {
        try {
            System.out.println("LivraisonController: Tentative de redirection vers ajoutercommand.fxml depuis ajouterlivraison.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutercommand.fxml"));
            Parent root = loader.load();
            CommandeController controller = loader.getController();
            if (sourceController != null) {
                List<CommandeProduit> produitsSelectionnes = sourceController.getProduitsSelectionnesList();
                controller.setProduitsSelectionnesList(produitsSelectionnes);
            }
            Stage stage = (Stage) (nomSocieteField != null ? nomSocieteField.getScene().getWindow() : livraisonsTable.getScene().getWindow());
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("Ajouter une Commande");
            stage.show();
            System.out.println("LivraisonController: Redirection réussie vers ajoutercommand.fxml");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de ajoutercommand.fxml : " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la redirection : " + e.getMessage());
        }
    }
    @FXML
    private void goToModifierLivraison() {
        if (livraisonToModify != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierlivraison.fxml"));
                Parent root = loader.load();
                LivraisonController controller = loader.getController();
                controller.setLivraisonToModify(livraisonToModify);
                controller.preFillFields();
                Stage stage = (Stage) livraisonsTable.getScene().getWindow();
                stage.setScene(new Scene(root, 600, 500));
                stage.setTitle("Modifier une Livraison");
                stage.show();
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de modifierlivraison.fxml : " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la redirection : " + e.getMessage());
            }
        }
    }

    @FXML
    private void goToAfficherLivraison() {
        try {
            System.out.println("LivraisonController: Tentative de redirection vers afficherlivraison.fxml depuis ajouterlivraison.fxml");
            Parent root = FXMLLoader.load(getClass().getResource("/afficherlivraison.fxml"));
            Stage stage = (Stage) (btnModifier != null ? btnModifier.getScene().getWindow() : btnRetour.getScene().getWindow());
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Liste des Livraisons");
            stage.show();
            System.out.println("LivraisonController: Redirection réussie vers afficherlivraison.fxml");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de afficherlivraison.fxml : " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la redirection : " + e.getMessage());
        }
    }
    @FXML
    private void goToAfficherCommande() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/affichercommand.fxml"));
            Stage stage = (Stage) (btnRetour != null ? btnRetour.getScene().getWindow() : nomSocieteField.getScene().getWindow());
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Liste des Commandes");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de affichercommand.fxml : " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la redirection : " + e.getMessage());
        }
    }

    public void setLivraisonToModify(Livraison livraison) {
        this.livraisonToModify = livraison;
    }

    public void setCommandeTemp(Commande commande) {
        this.commandeTemp = commande;
    }

    public void setSourceController(CommandeController controller) {
        this.sourceController = controller;
    }

    private void preFillFields() {
        if (livraisonToModify != null) {
            nomSocieteField.setText(livraisonToModify.getNomSociete());
            adresseField.setText(livraisonToModify.getAdresseLivraison());
            // Convertir la date en LocalDate
            Date dateLivraison = livraisonToModify.getDateLivraison();
            if (dateLivraison != null) {
                LocalDate localDate;
                if (dateLivraison instanceof java.sql.Date) {
                    localDate = ((java.sql.Date) dateLivraison).toLocalDate();
                } else {
                    localDate = dateLivraison.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                }
                dateLivraisonPicker.setValue(localDate);
            }
            statutCombo.setValue(livraisonToModify.getStatut());
        }
    }

    private void clearFields() {
        nomSocieteField.clear();
        adresseField.clear();
        dateLivraisonPicker.setValue(null);
        statutCombo.getSelectionModel().clearSelection();
        livraisonToModify = null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
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