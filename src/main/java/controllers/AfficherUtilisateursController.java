package controllers;

import Models.Utilisateur;
import Services.UtilisateurService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherUtilisateursController {
    @FXML private TableView<Utilisateur> tableView;
    @FXML private TableColumn<Utilisateur, String> idColumn;
    @FXML private TableColumn<Utilisateur, String> nomColumn;
    @FXML private TableColumn<Utilisateur, String> prenomColumn;
    @FXML private TableColumn<Utilisateur, String> emailColumn;
    @FXML private TableColumn<Utilisateur, String> telephoneColumn;
    @FXML private TableColumn<Utilisateur, String> roleColumn;
    @FXML private TableColumn<Utilisateur, Void> actionColumn;
    @FXML private TextField searchField;

    private ObservableList<Utilisateur> utilisateurList;
    private UtilisateurService us = new UtilisateurService();

    @FXML
    void initialize() {
        try {
            List<Utilisateur> utilisateurs = us.rechercher();
            utilisateurList = FXCollections.observableArrayList(utilisateurs);
            tableView.setItems(utilisateurList);

            idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
            nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
            prenomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
            emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
            telephoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelephone()));
            roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

            actionColumn.setCellFactory(param -> new TableCell<>() {
                private final Button modifierBtn = new Button("Modifier");
                private final Button supprimerBtn = new Button("Supprimer");
                private final HBox hbox = new HBox(5, modifierBtn, supprimerBtn);

                {
                    modifierBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 3;");
                    supprimerBtn.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 3;");
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Utilisateur utilisateur = getTableView().getItems().get(getIndex());
                        modifierBtn.setOnAction(e -> modifierUtilisateur(utilisateur));
                        supprimerBtn.setOnAction(e -> supprimerUtilisateur(utilisateur));
                        setGraphic(hbox);
                    }
                }
            });
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void filterUtilisateurs() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            tableView.setItems(utilisateurList);
        } else {
            ObservableList<Utilisateur> filteredList = utilisateurList.stream()
                    .filter(u -> u.getNom().toLowerCase().contains(searchText) ||
                            u.getPrenom().toLowerCase().contains(searchText) ||
                            u.getEmail().toLowerCase().contains(searchText))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            tableView.setItems(filteredList);
        }
    }

    private void supprimerUtilisateur(Utilisateur utilisateur) {
        try {
            us.supprimer(utilisateur);
            utilisateurList.remove(utilisateur);
            tableView.refresh();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void modifierUtilisateur(Utilisateur utilisateur) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierUtilisateur.fxml"));
        try {
            Parent root = loader.load();
            ModifierUtilisateurController controller = loader.getController();
            controller.setUtilisateur(utilisateur);
            tableView.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void naviguerAjouterUtilisateur(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterUtilisateur.fxml"));
        try {
            Parent root = loader.load();
            tableView.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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