package controllers;

import Models.Commande;
import Models.CommandeProduit;
import Models.Livraison;
import Models.Produit;
import Models.StatutCommande;
import Services.CommandeService;
import Services.ProduitService;
import Services.PaymentService; // Importez PaymentService
import com.itextpdf.layout.element.Paragraph;
import com.stripe.exception.StripeException; // Import pour StripeException
import com.stripe.model.checkout.Session; // Import pour Stripe Checkout Session
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
import javafx.scene.web.WebView; // Import pour WebView

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;
import com.itextpdf.kernel.pdf.PdfWriter;
import java.text.SimpleDateFormat;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import java.awt.Desktop;

public class CommandeController {
    // Fields for affichercommand.fxml
    @FXML private TableView<Commande> commandeTable;
    @FXML private TableColumn<Commande, Integer> idColumn;
    @FXML private TableColumn<Commande, Date> dateColumn;
    @FXML private TableColumn<Commande, Double> totalColumn;
    @FXML private TableColumn<Commande, String> statutColumn;
    @FXML private TableColumn<Commande, Void> actionColumn;
    @FXML private Button addButton;
    @FXML private TableColumn<Commande, Void> actionFactureColumn;

    // Fields for ajoutercommand.fxml and modifiercommand.fxml
    @FXML private ComboBox<Produit> produitCombo;
    @FXML private TextField quantiteField;
    @FXML private TableView<CommandeProduit> produitsSelectionnesTable;
    @FXML private TableColumn<CommandeProduit, String> produitColumn;
    @FXML private TableColumn<CommandeProduit, String> nomProduitColumn;
    @FXML private TableColumn<CommandeProduit, Integer> quantiteColumn;
    @FXML private TableColumn<CommandeProduit, Double> prixUnitaireColumn;
    @FXML private TableColumn<CommandeProduit, Double> prixTotalColumn;
    @FXML private TableColumn<CommandeProduit, Void> actionCommandeColumn;
    @FXML private TableColumn<CommandeProduit, Void> actionProduitColumn;
    @FXML private TextField totalField;
    @FXML private Label totalLabel;
    @FXML private ComboBox<StatutCommande> statutCombo;
    @FXML private WebView paymentWebView; // Ajout de la WebView pour le paiement

    private ObservableList<CommandeProduit> produitsSelectionnes = FXCollections.observableArrayList();
    private ProduitService produitService = new ProduitService();
    private CommandeService commandeService = new CommandeService();
    private PaymentService paymentService = new PaymentService(); // Instance de PaymentService
    private Commande commandeToModify;
    private Commande commandeTemp; // Champ pour stocker la commande temporaire

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

                // Configuration pour la colonne actionFactureColumn (Télécharger Facture)
                if (actionFactureColumn != null) {
                    actionFactureColumn.setCellFactory(col -> new TableCell<Commande, Void>() {
                        private final Button telechargerButton = new Button("Télécharger Facture");

                        {
                            telechargerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

                            telechargerButton.setOnAction(event -> {
                                Commande commande = getTableView().getItems().get(getIndex());
                                String cheminFacture = "facture_" + commande.getId() + ".pdf";
                                File fichier = new File(cheminFacture);
                                if (fichier.exists()) {
                                    try {
                                        Desktop.getDesktop().open(fichier);
                                    } catch (Exception e) {
                                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la facture : " + e.getMessage());
                                    }
                                } else {
                                    showAlert(Alert.AlertType.WARNING, "Aucune facture", "Aucune facture disponible pour cette commande.");
                                }
                            });
                        }

                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            setGraphic(empty ? null : telechargerButton);
                        }
                    });
                }

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
                if (statutCombo != null) {
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
            this.commandeTemp = new Commande(
                    0,
                    null, // livraison sera ajoutée plus tard
                    new Date(),
                    calculerTotal(),
                    StatutCommande.en_attente,
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
        } catch (Exception e) {
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
                goToAfficherCommande(); // Appel sans paramètre
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
    private void goToAfficherCommande(Stage stage) {
        try {
            // Charger la nouvelle scène (affichercommand.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichercommand.fxml"));
            Parent root = loader.load();

            // Définir la nouvelle scène et afficher
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher les commandes");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de l'interface d'affichage des commandes : " + e.getMessage());
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

    private void goToModifierCommande(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifiercommand.fxml"));
            Parent root = loader.load();
            CommandeController controller = loader.getController();
            controller.setCommandeToModify(commande);
            controller.preFillFields();
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
            produitsSelectionnes.clear();
            List<CommandeProduit> produits = commandeToModify.getProduits();
            if (produits != null) {
                produitsSelectionnes.addAll(produits);
            } else {
                System.err.println("Aucun produit trouvé pour la commande ID : " + commandeToModify.getId());
            }
            produitsSelectionnesTable.refresh();

            if (statutCombo != null) {
                statutCombo.setValue(commandeToModify.getStatut());
            }

            updateTotal();
        } else {
            System.err.println("Erreur : commandeToModify est null dans preFillFields");
        }
    }

    // Méthode pour initier le paiement après l'enregistrement de la commande et de la livraison
    public void initierPaiement(Stage stage) {
        if (commandeTemp == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune commande enregistrée pour le paiement.");
            return;
        }

        try {
            double montant = commandeTemp.getTotal();
            if (montant <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le montant de la commande doit être supérieur à 0.");
                return;
            }

            Session checkoutSession = paymentService.createCheckoutSession(
                    montant,
                    "eur",
                    "Paiement pour la commande ID " + commandeTemp.getId()
            );

            WebView webViewToUse = (currentPaymentWebView != null) ? currentPaymentWebView : paymentWebView;
            if (webViewToUse == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune WebView disponible pour le paiement.");
                return;
            }

            webViewToUse.setVisible(true);
            webViewToUse.getEngine().load(checkoutSession.getUrl());

            webViewToUse.getEngine().locationProperty().addListener((obs, oldLocation, newLocation) -> {
                if (newLocation != null) {
                    if (newLocation.contains("success")) {
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Paiement effectué avec succès !");
                        try {
                            genererFacturePDF(commandeTemp);
                        } catch (Exception e) {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération de la facture : " + e.getMessage());
                        }
                        goToAfficherCommande(stage); // Passer le Stage directement
                    } else if (newLocation.contains("cancel")) {
                        showAlert(Alert.AlertType.WARNING, "Annulé", "Le paiement a été annulé.");
                        webViewToUse.setVisible(false);
                        webViewToUse.getEngine().load(null);
                    }
                }
            });

        } catch (StripeException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de paiement", "Erreur lors de l'initiation du paiement : " + e.getMessage());
        }
    }    public void setCommandeTemp(Commande commande) {
        this.commandeTemp = commande;
    }

    // Ajoutez ceci à CommandeController.java
    private void genererFacturePDF(Commande commande) throws Exception {
        // Définir le chemin du fichier PDF
        String cheminFacture = "facture_" + commande.getId() + ".pdf";
        File fichier = new File(cheminFacture);

        // Créer un document PDF
        PdfWriter writer = new PdfWriter(cheminFacture);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Ajouter un titre
        document.add(new Paragraph("Facture " )
                .setFontSize(20)
                .setBold());

        // Ajouter la date de la commande
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        document.add(new Paragraph("Date de la commande : " + dateFormat.format(commande.getDateCommande())));

        // Ajouter les détails de la livraison (si disponible)
        if (commande.getLivraison() != null) {
            document.add(new Paragraph("Livraison : " + commande.getLivraison().getNomSociete()));
            document.add(new Paragraph("Adresse : " + commande.getLivraison().getAdresseLivraison()));
            document.add(new Paragraph("Date de livraison : " + dateFormat.format(commande.getLivraison().getDateLivraison())));
        }

        // Ajouter un tableau pour les produits
        document.add(new Paragraph("\nDétails de la commande :\n"));
        Table table = new Table(new float[]{3, 1, 1, 1}); // 4 colonnes : Nom, Quantité, Prix unitaire, Total
        table.addHeaderCell(new Cell().add(new Paragraph("Produit")));
        table.addHeaderCell(new Cell().add(new Paragraph("Quantité")));
        table.addHeaderCell(new Cell().add(new Paragraph("Prix unitaire")));
        table.addHeaderCell(new Cell().add(new Paragraph("Total")));

        for (CommandeProduit cp : commande.getProduits()) {
            table.addCell(new Cell().add(new Paragraph(cp.getProduit().getNom())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(cp.getQuantite()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f €", cp.getProduit().getPrix()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f €", cp.getProduit().getPrix() * cp.getQuantite()))));
        }

        document.add(table);

        // Ajouter le total général
        document.add(new Paragraph("\nTotal : " + String.format("%.2f €", commande.getTotal()))
                .setBold());

        // Ajouter un message de remerciement
        document.add(new Paragraph("\nMerci pour votre commande !"));

        // Fermer le document
        document.close();

        // Informer l'utilisateur
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture générée : " + fichier.getAbsolutePath());
    }
    private WebView currentPaymentWebView;

    public void setPaymentWebView(WebView webView) {
        this.currentPaymentWebView = webView;
    }

    @FXML
    private void goToAfficherCommande() {
        try {
            // Utiliser un élément existant pour récupérer le Stage
            Stage stage;
            if (produitsSelectionnesTable != null) {
                stage = (Stage) produitsSelectionnesTable.getScene().getWindow();
            } else if (statutCombo != null) {
                stage = (Stage) statutCombo.getScene().getWindow();
            } else if (commandeTable != null) {
                stage = (Stage) commandeTable.getScene().getWindow();
            } else if (addButton != null) {
                stage = (Stage) addButton.getScene().getWindow();
            } else {
                throw new IllegalStateException("Impossible de récupérer la fenêtre principale.");
            }

            // Appeler la méthode privée avec le Stage récupéré
            goToAfficherCommande(stage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de l'interface d'affichage des commandes : " + e.getMessage());
        }
    }
}