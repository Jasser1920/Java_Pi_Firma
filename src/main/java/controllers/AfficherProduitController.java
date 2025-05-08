package controllers;

import Models.Categorie;
import Models.Produit;
import Services.CategorieService;
import Services.ProduitService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherProduitController {

    @FXML private TableView<Produit> tabFX;
    @FXML private TableColumn<Produit, Integer> colidFX;
    @FXML private TableColumn<Produit, String> colnomFX;
    @FXML private TableColumn<Produit, Double> colprixFX;
    @FXML private TableColumn<Produit, String> coldescriptionFX;
    @FXML private TableColumn<Produit, String> colimageFX;
    @FXML private TableColumn<Produit, Integer> colquantiteFX;
    @FXML private TableColumn<Produit, Date> coldateFX;
    @FXML private TableColumn<Produit, String> colcategorieFX;

    @FXML private TextField nomField;
    @FXML private TextField quantiteMinField;
    @FXML private TextField quantiteMaxField;
    @FXML private TextField prixMinField;
    @FXML private TextField prixMaxField;
    @FXML private ComboBox<Categorie> categorieComboBox;
    @FXML private Button btnFiltrer;
    @FXML private Button btnReset;
    @FXML private Button btnVoirFX;
    @FXML private Button btnajouterFX;
    @FXML private Button btnCategorieFX;
    @FXML private Button homeFX;

    @FXML private Button btnStatistiquesFX;
    @FXML
    private TableColumn<Produit, Void> colAlerteFX;



    private final ProduitService produitService = new ProduitService();
    private final CategorieService categorieService = new CategorieService();
    private ObservableList<Produit> produits;

    @FXML
    public void initialize() {
        colidFX.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colnomFX.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colprixFX.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrix()));
        coldescriptionFX.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        colimageFX.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImage()));
        colquantiteFX.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantite()));
        coldateFX.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateExpiration()));
        colcategorieFX.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategorie().getNomCategorie()));

        // Colonne des alertes avec ImageView
        colAlerteFX.setCellFactory(col -> new TableCell<>() {
            private final Tooltip tooltip = new Tooltip();
            private final ImageView iconView = new ImageView();

            {
                iconView.setFitWidth(20);
                iconView.setFitHeight(20);
                setGraphic(iconView);
                setTooltip(tooltip);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    iconView.setImage(null);
                    tooltip.setText("");
                } else {
                    Produit p = getTableView().getItems().get(getIndex());
                    long joursRestants = java.time.temporal.ChronoUnit.DAYS.between(
                            java.time.LocalDate.now(),
                            p.getDateExpiration().toLocalDate()
                    );

                    boolean expBientot = joursRestants <= 3;
                    boolean quantiteBasse = p.getQuantite() < 5;
                    boolean surstock = p.getQuantite() > 20;

                    if (expBientot && surstock) {
                        InputStream imgStream = getClass().getResourceAsStream("/Images/alert-red.png");
                        if (imgStream == null) {
                            System.err.println("Image not found: /Images/alert-red.png");
                            iconView.setImage(null);
                        } else {
                            iconView.setImage(new Image(imgStream));
                        }
                        tooltip.setText("Produit à écouler rapidement : expiration proche + grande quantité.");
                    } else if (quantiteBasse) {
                        InputStream imgStream = getClass().getResourceAsStream("/Images/alert-orange.png");
                        if (imgStream == null) {
                            System.err.println("Image not found: /Images/alert-orange.png");
                            iconView.setImage(null);
                        } else {
                            iconView.setImage(new Image(imgStream));
                        }
                        tooltip.setText("Stock critique : quantité faible.");
                    } else {
                        iconView.setImage(null);
                        tooltip.setText("");
                    }
                }
            }
        });

        try {
            categorieComboBox.setItems(FXCollections.observableArrayList(categorieService.rechercher()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnFiltrer.setOnAction(e -> appliquerFiltre());
        btnReset.setOnAction(e -> resetFiltre());
        btnVoirFX.setOnAction(e -> voirProduit());
        btnajouterFX.setOnAction(e -> ajouterProduit());
        btnCategorieFX.setOnAction(e -> ouvrirGestionCategorie());

        rafraichirTable();
    }



    public void rafraichirTable() {
        try {
            produits = FXCollections.observableArrayList(produitService.rechercher());
            tabFX.setItems(produits);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void appliquerFiltre() {
        String nom = nomField.getText().toLowerCase();
        int qMin = parseOrDefault(quantiteMinField.getText(), 0);
        int qMax = parseOrDefault(quantiteMaxField.getText(), Integer.MAX_VALUE);
        double pMin = parseDoubleOrDefault(prixMinField.getText(), 0.0);
        double pMax = parseDoubleOrDefault(prixMaxField.getText(), Double.MAX_VALUE);
        Categorie cat = categorieComboBox.getValue();

        List<Produit> filtres = produits.stream()
                .filter(p -> p.getNom().toLowerCase().contains(nom))
                .filter(p -> p.getQuantite() >= qMin && p.getQuantite() <= qMax)
                .filter(p -> p.getPrix() >= pMin && p.getPrix() <= pMax)
                .filter(p -> cat == null || (p.getCategorie() != null && p.getCategorie().getId() == cat.getId()))
                .collect(Collectors.toList());

        tabFX.setItems(FXCollections.observableArrayList(filtres));
    }

    private void resetFiltre() {
        nomField.clear();
        quantiteMinField.clear();
        quantiteMaxField.clear();
        prixMinField.clear();
        prixMaxField.clear();
        categorieComboBox.getSelectionModel().clearSelection();
        tabFX.setItems(produits);
    }

    private int parseOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double parseDoubleOrDefault(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void voirProduit() {
        Produit selected = tabFX.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Veuillez sélectionner un produit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetaillerProduit.fxml"));
            Parent root = loader.load();
            controllers.DetaillerProduitController controller = loader.getController();
            controller.setProduit(selected);
            controller.setAfficherProduitController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détail Produit");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture des détails du produit.");
        }
    }

    private void ajouterProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduit.fxml"));
            Parent root = loader.load();
            controllers.AjouterProduitController controller = loader.getController();
            controller.setAfficherProduitController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un produit");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture du formulaire d'ajout.");
        }
    }

    private void ouvrirGestionCategorie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCategorie.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnCategorieFX.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Catégories");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture de la gestion des catégories.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alerte");
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
    public void ouvrirStatistiques(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatProduit.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Statistiques des produits");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}