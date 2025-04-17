package controllers;

import Models.Commande;
import Models.CommandeProduit;
import Models.Livraison;
import Models.Produit;
import Models.StatutCommande;
import Services.CommandeService;
import Services.ProduitService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandeController {
    // Fields for affichercommand.fxml
    @FXML
    private TableView<Commande> commandeTable;
    @FXML
    private TableColumn<Commande, Integer> idColumn;
    @FXML
    private TableColumn<Commande, Date> dateColumn;
    @FXML
    private TableColumn<Commande, Double> totalColumn;
    @FXML
    private TableColumn<Commande, String> statutColumn;
    @FXML
    private TableColumn<Commande, Void> actionColumn; // For affichercommand.fxml
    @FXML
    private Button addButton;

    // Fields for ajoutercommand.fxml and modifiercommand.fxml
    @FXML
    private ComboBox<Produit> produitCombo;
    @FXML
    private TextField quantiteField;
    @FXML
    private TableView<CommandeProduit> produitsSelectionnesTable;
    @FXML
    private TableColumn<CommandeProduit, String> produitColumn; // For ajoutercommand.fxml
    @FXML
    private TableColumn<CommandeProduit, String> nomProduitColumn; // For modifiercommand.fxml
    @FXML
    private TableColumn<CommandeProduit, Integer> quantiteColumn;
    @FXML
    private TableColumn<CommandeProduit, Double> prixUnitaireColumn;
    @FXML
    private TableColumn<CommandeProduit, Double> prixTotalColumn;
    @FXML
    private TableColumn<CommandeProduit, Void> actionCommandeColumn; // For ajoutercommand.fxml
    @FXML
    private TableColumn<CommandeProduit, Void> actionProduitColumn; // For modifiercommand.fxml
    @FXML
    private TextField totalField; // For ajoutercommand.fxml
    @FXML
    private Label totalLabel; // For modifiercommand.fxml
    @FXML
    private ComboBox<StatutCommande> statutCombo;

    private ObservableList<CommandeProduit> produitsSelectionnes = FXCollections.observableArrayList();
    private ProduitService produitService = new ProduitService();
    private CommandeService commandeService = new CommandeService();
    private Commande commandeToModify; // For modifiercommand.fxml

//    @FXML
//    public void initialize() {
//        try {
//            // Initialize for affichercommand.fxml
//            if (commandeTable != null) {
//                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//                dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
//                totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
//                statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
//
//                // Action column with "Supprimer" and "Modifier" buttons for Commande
//                actionColumn.setCellFactory(param -> new TableCell<>() {
//                    private final Button supprimerButton = new Button("Supprimer");
//                    private final Button modifierButton = new Button("Modifier");
//                    private final HBox buttonsBox = new HBox(5, supprimerButton, modifierButton);
//
//                    {
//                        supprimerButton.setOnAction(event -> {
//                            Commande commande = getTableView().getItems().get(getIndex());
//                            commandeService.supprimer(commande);
//                            loadCommandes();
//                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Commande supprimée avec succès.");
//                        });
//
//                        modifierButton.setOnAction(event -> {
//                            Commande commande = getTableView().getItems().get(getIndex());
//                            setCommandeToModify(commande);
//                            goToModifierCommande(commande);                        });
//
//
//
//
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(empty ? null : buttonsBox);
//                    }
//                });
//
//                loadCommandes();
//            }
//
//            // Initialize for ajoutercommand.fxml and modifiercommand.fxml
//            if (produitsSelectionnesTable != null) {
//                // Use produitColumn for ajoutercommand.fxml, nomProduitColumn for modifiercommand.fxml
//                TableColumn<CommandeProduit, String> productNameColumn = (produitColumn != null) ? produitColumn : nomProduitColumn;
//                productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduit().getNom()));
//                quantiteColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());
//                prixUnitaireColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProduit().getPrix()).asObject());
//                prixTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProduit().getPrix() * cellData.getValue().getQuantite()).asObject());
//
//                // Use actionCommandeColumn for ajoutercommand.fxml, actionProduitColumn for modifiercommand.fxml
//                TableColumn<CommandeProduit, Void> actionCol = (actionCommandeColumn != null) ? actionCommandeColumn : actionProduitColumn;
//                actionCol.setCellFactory(param -> new TableCell<>() {
//                    private final Button supprimerButton = new Button("Supprimer");
//
//                    {
//                        supprimerButton.setOnAction(event -> {
//                            CommandeProduit commandeProduit = getTableView().getItems().get(getIndex());
//                            produitsSelectionnes.remove(commandeProduit);
//                            updateTotal();
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(empty ? null : supprimerButton);
//                    }
//                });
//
//                // Configure the ComboBox to display product names
//                produitCombo.setItems(FXCollections.observableArrayList(produitService.rechercher()));
//                produitCombo.setConverter(new StringConverter<Produit>() {
//                    @Override
//                    public String toString(Produit produit) {
//                        return produit != null ? produit.getNom() : "";
//                    }
//
//                    @Override
//                    public Produit fromString(String string) {
//                        return produitCombo.getItems().stream()
//                                .filter(produit -> produit.getNom().equals(string))
//                                .findFirst()
//                                .orElse(null);
//                    }
//                });
//
//                produitsSelectionnesTable.setItems(produitsSelectionnes);
//
//                if (statutCombo != null) {
//                    statutCombo.setItems(FXCollections.observableArrayList(StatutCommande.values()));
//                    statutCombo.setValue(StatutCommande.en_attente);
//                }
//
//                // If in modifier mode, prefill the fields
//                if (commandeToModify != null) {
//                    produitsSelectionnes.addAll(commandeToModify.getProduits());
//                    updateTotal();
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Erreur lors de l'initialisation de CommandeController : " + e.getMessage());
//            e.printStackTrace();
//            showAlert("Erreur", "Une erreur est survenue lors de l'initialisation : " + e.getMessage());
//        }
//    }

    @FXML
    public void initialize() {
        try {
            // Initialize for affichercommand.fxml
            if (commandeTable != null) {
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
                totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
                statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

                // Action column with "Supprimer" and "Modifier" buttons for Commande
                actionColumn.setCellFactory(param -> new TableCell<>() {
                    private final Button supprimerButton = new Button("Supprimer");
                    private final Button modifierButton = new Button("Modifier");
                    private final HBox buttonsBox = new HBox(5, supprimerButton, modifierButton);

                    {
                        supprimerButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                        modifierButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                        supprimerButton.setOnAction(event -> {
                            Commande commande = getTableView().getItems().get(getIndex());
                            commandeService.supprimer(commande);
                            loadCommandes();
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Commande supprimée avec succès.");
                        });

                        modifierButton.setOnAction(event -> {
                            Commande commande = getTableView().getItems().get(getIndex());
                            setCommandeToModify(commande);
                            goToModifierCommande(commande);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : buttonsBox);
                    }
                });

                loadCommandes();
            }

            // Initialize for ajoutercommand.fxml and modifiercommand.fxml
            if (produitsSelectionnesTable != null) {
                // Use produitColumn for ajoutercommand.fxml, nomProduitColumn for modifiercommand.fxml
                TableColumn<CommandeProduit, String> productNameColumn = (produitColumn != null) ? produitColumn : nomProduitColumn;
                productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduit().getNom()));
                quantiteColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());
                prixUnitaireColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProduit().getPrix()).asObject());
                prixTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProduit().getPrix() * cellData.getValue().getQuantite()).asObject());

                // Use actionCommandeColumn for ajoutercommand.fxml, actionProduitColumn for modifiercommand.fxml
                TableColumn<CommandeProduit, Void> actionCol = (actionCommandeColumn != null) ? actionCommandeColumn : actionProduitColumn;
                actionCol.setCellFactory(param -> new TableCell<>() {
                    private final Button supprimerButton = new Button("Supprimer");

                    {
                        supprimerButton.setOnAction(event -> {
                            CommandeProduit commandeProduit = getTableView().getItems().get(getIndex());
                            produitsSelectionnes.remove(commandeProduit);
                            updateTotal();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : supprimerButton);
                    }
                });

                // Configure the ComboBox to display product names
                produitCombo.setItems(FXCollections.observableArrayList(produitService.rechercher()));
                produitCombo.setConverter(new StringConverter<Produit>() {
                    @Override
                    public String toString(Produit produit) {
                        return produit != null ? produit.getNom() : "";
                    }

                    @Override
                    public Produit fromString(String string) {
                        return produitCombo.getItems().stream()
                                .filter(produit -> produit.getNom().equals(string))
                                .findFirst()
                                .orElse(null);
                    }
                });

                produitsSelectionnesTable.setItems(produitsSelectionnes);

                // Configuration de statutCombo uniquement pour modifiercommand.fxml
                if (statutCombo != null) { // statutCombo existe uniquement dans modifiercommand.fxml
                    statutCombo.setItems(FXCollections.observableArrayList(StatutCommande.values()));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de CommandeController : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'initialisation : " + e.getMessage());
        }
    }
    private void loadCommandes() {
        commandeTable.setItems(FXCollections.observableArrayList(commandeService.rechercher()));
    }

    private void updateTotal() {
        double total = calculerTotal();
        if (totalField != null) {
            totalField.setText(String.format("%.2f", total));
        }
        if (totalLabel != null) {
            totalLabel.setText(String.format("%.2f", total));
        }
    }

    private double calculerTotal() {
        return produitsSelectionnes.stream()
                .mapToDouble(cp -> cp.getProduit().getPrix() * cp.getQuantite())
                .sum();
    }

    @FXML
    private void ajouterProduit() {
        Produit produit = produitCombo.getValue();
        String quantiteStr = quantiteField.getText();

        if (produit == null || quantiteStr.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un produit et saisir une quantité.");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(quantiteStr);
            if (quantite <= 0) {
                showAlert("Erreur", "La quantité doit être supérieure à 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La quantité doit être un nombre valide.");
            return;
        }

        CommandeProduit existingProduit = produitsSelectionnes.stream()
                .filter(cp -> cp.getProduit().getId() == produit.getId())
                .findFirst()
                .orElse(null);

        if (existingProduit != null) {
            existingProduit.setQuantite(existingProduit.getQuantite() + quantite);
            produitsSelectionnesTable.refresh();
        } else {
            CommandeProduit commandeProduit = new CommandeProduit(produit, quantite);
            produitsSelectionnes.add(commandeProduit);
        }

        updateTotal();
        produitCombo.setValue(null);
        quantiteField.clear();
    }

    @FXML
    private void ajouterCommande() {
        try {
            System.out.println("Début de ajouterCommande");
            if (produitsSelectionnes.isEmpty()) {
                showAlert("Erreur", "Veuillez ajouter au moins un produit à la commande.");
                System.out.println("produitsSelectionnes est vide");
                return;
            }

            System.out.println("Création de commandeTemp");
            Commande commandeTemp = new Commande(
                    0,
                    null, // livraison sera ajoutée plus tard
                    new Date(),
                    calculerTotal(),
                    StatutCommande.en_attente, // Utiliser directement la valeur par défaut
                    new ArrayList<>(produitsSelectionnes)
            );

            System.out.println("Chargement de ajouterlivraison.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterlivraison.fxml"));
            Parent root = loader.load();
            LivraisonController livraisonController = loader.getController();
            livraisonController.setCommandeTemp(commandeTemp);
            livraisonController.setSourceController(this);

            System.out.println("Affichage de l'interface ajouterlivraison");
            Stage stage = (Stage) produitCombo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une livraison");
            stage.show();
            System.out.println("Redirection réussie vers ajouterlivraison.fxml");
        } catch (Exception e) { // Capturer toutes les exceptions
            System.err.println("Erreur dans ajouterCommande : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout de la commande : " + e.getMessage());
        }
    }
    @FXML
    private void updateCommande() {
        try {
            if (produitsSelectionnes.isEmpty()) {
                showAlert("Erreur", "Veuillez ajouter au moins un produit à la commande.");
                return;
            }

            if (commandeToModify != null) {
                commandeToModify.setDateCommande(new Date());
                commandeToModify.setTotal(calculerTotal());
                commandeToModify.setProduits(new ArrayList<>(produitsSelectionnes));

                commandeService.modifier(commandeToModify);
                showAlert("Succès", "Commande modifiée avec succès.");
                goToAfficherCommande();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la modification de la commande : " + e.getMessage());
        }
    }

    @FXML
    private void goToAjouterCommande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutercommand.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) (addButton != null ? addButton.getScene().getWindow() : produitCombo.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une Commande");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de l'interface d'ajout de commande : " + e.getMessage());
        }
    }

    @FXML
    private void goToAfficherCommande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichercommand.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) (produitCombo != null ? produitCombo.getScene().getWindow() : addButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher les commandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de l'interface d'affichage des commandes : " + e.getMessage());
        }
    }

    @FXML
    private void goToAfficherLivraison() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherlivraison.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gérer les Livraisons");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de l'interface de gestion des livraisons : " + e.getMessage());
        }
    }

//    private void goToModifierCommande(Commande commande) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifiercommand.fxml"));
//            Parent root = loader.load();
//            CommandeController controller = loader.getController();
//            controller.setCommandeToModify(commande);
//            Stage stage = (Stage) commandeTable.getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Modifier une Commande");
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            showAlert("Erreur", "Erreur lors du chargement de l'interface de modification de commande : " + e.getMessage());
//        }
//    }

    private void goToModifierCommande(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifiercommand.fxml"));
            Parent root = loader.load();
            CommandeController controller = loader.getController();
            controller.setCommandeToModify(commande);
            controller.preFillFields(); // Appeler la méthode pour pré-remplir les champs
            Stage stage = (Stage) commandeTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier une Commande");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de l'interface de modification de commande : " + e.getMessage());
        }
    }
    // Méthodes pour gérer les produits sélectionnés
    public List<CommandeProduit> getProduitsSelectionnesList() {
        return new ArrayList<>(produitsSelectionnes);
    }

    public void setProduitsSelectionnesList(List<CommandeProduit> produits) {
        produitsSelectionnes.clear();
        if (produits != null) {
            produitsSelectionnes.addAll(produits);
        }
        produitsSelectionnesTable.refresh();
        updateTotal();
    }

    public void setCommandeToModify(Commande commande) {
        this.commandeToModify = commande;
    }

    private void showAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void preFillFields() {
        if (commandeToModify != null) {
            // Pré-remplir la liste des produits
            produitsSelectionnes.clear();
            List<CommandeProduit> produits = commandeToModify.getProduits();
            if (produits != null) {
                produitsSelectionnes.addAll(produits);
            } else {
                System.err.println("Aucun produit trouvé pour la commande ID : " + commandeToModify.getId());
            }
            produitsSelectionnesTable.refresh();

            // Pré-remplir le statut
            if (statutCombo != null) {
                statutCombo.setValue(commandeToModify.getStatut());
            }

            // Mettre à jour le total
            updateTotal();
        } else {
            System.err.println("Erreur : commandeToModify est null dans preFillFields");
        }
    }
}