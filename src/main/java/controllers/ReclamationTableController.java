package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.application.Platform;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import Models.Reclammation;
import Models.Statut;
import Models.ReponseReclamation;
import Models.Utilisateur;
import Services.ReclammationService;
import Services.ReponseReclamationService;

public class ReclamationTableController {
    @FXML private TableView<Reclammation> reclamationTable;
    @FXML private TableColumn<Reclammation, Integer> idColumn;
    @FXML private TableColumn<Reclammation, Utilisateur> userIdColumn;
    @FXML private TableColumn<Reclammation, String> titleColumn;
    @FXML private TableColumn<Reclammation, String> descriptionColumn;
    @FXML private TableColumn<Reclammation, Statut> statusColumn;
    @FXML private TableColumn<Reclammation, Date> dateColumn;
    @FXML private TableColumn<Reclammation, Void> actionColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker dateFilter;
    @FXML private TextField emailFilter;
    @FXML private ComboBox<String> sortOrder;

    private final ObservableList<Reclammation> reclamations = FXCollections.observableArrayList();
    private FilteredList<Reclammation> filteredData;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final ReclammationService reclammationService = new ReclammationService();
    private final ReponseReclamationService reponseReclamationService = new ReponseReclamationService();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();
        setupTableStyling();
        loadReclamations();
        setupSearch();
    }

    private void setupTableColumns() {
        // Configuration des colonnes
        userIdColumn.setText("Email Utilisateur");
        idColumn.setText("N°");
        titleColumn.setText("Titre");
        descriptionColumn.setText("Description");
        statusColumn.setText("Statut");
        dateColumn.setText("Date");
        actionColumn.setText("Action");

        // Configuration des valeurs
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("utilisateur"));

        // Réorganiser l'ordre des colonnes
        reclamationTable.getColumns().clear();
        reclamationTable.getColumns().addAll(
                userIdColumn,
                idColumn,
                titleColumn,
                descriptionColumn,
                statusColumn,
                dateColumn,
                actionColumn
        );

        // Formater la colonne utilisateur
        userIdColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Utilisateur item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getEmail() != null ? item.getEmail() : "N/A");
                }
            }
        });

        // Formater la colonne description
        descriptionColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.length() > 100 ? item.substring(0, 97) + "..." : item);
                }
            }
        });

        // Formater la colonne statut
        statusColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Statut item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setBackground(null);
                } else {
                    setText(item.getLabel());
                    Color color = switch (item) {
                        case EN_ATTENTE, EN_COURS -> Color.LIGHTBLUE;
                        case RESOLUE -> Color.LIGHTGREEN;
                        case REJETEE -> Color.LIGHTCORAL;
                    };
                    setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, null)));
                    setStyle("-fx-text-fill: black;");
                }
            }
        });

        // Formater la colonne date
        dateColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    LocalDate localDate = item.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    setText(localDate.format(dateFormatter));
                }
            }
        });

        // Configurer la colonne action
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button actionButton = new Button("Répondre");
            private final HBox buttonsBox = new HBox(5, actionButton);

            {
                buttonsBox.setAlignment(Pos.CENTER);
                actionButton.setOnAction(event -> {
                    Reclammation reclammation = getTableView().getItems().get(getIndex());
                    if (reclammation.getStatut() == Statut.EN_ATTENTE && reclammation.getReponse() == null) {
                        showResponseDialog(reclammation);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reclammation reclammation = getTableView().getItems().get(getIndex());
                    boolean canRespond = reclammation.getStatut() == Statut.EN_ATTENTE && reclammation.getReponse() == null;
                    actionButton.setDisable(!canRespond);
                    if (canRespond) {
                        actionButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    } else {
                        actionButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
                    }
                    setGraphic(buttonsBox);
                }
            }
        });
    }

    private void setupFilters() {
        // Initialiser les filtres
        statusFilter.setItems(FXCollections.observableArrayList(
                "Tous",
                Statut.EN_ATTENTE.getLabel(),
                Statut.EN_COURS.getLabel(),
                Statut.RESOLUE.getLabel(),
                Statut.REJETEE.getLabel()
        ));
        statusFilter.getSelectionModel().selectFirst();

        // Initialiser le ComboBox de tri
        sortOrder.setItems(FXCollections.observableArrayList("Plus récent", "Plus ancien"));

        // Configurer la recherche et le filtrage
        filteredData = new FilteredList<>(reclamations, p -> true);

        // Configurer le tri
        SortedList<Reclammation> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(reclamationTable.comparatorProperty());
        reclamationTable.setItems(sortedData);

        // Configurer le tri par défaut
        reclamationTable.getSortOrder().add(dateColumn);
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
    }

    private void setupTableStyling() {
        // Style pour la TableView
        reclamationTable.setStyle("-fx-selection-bar: #e0e0e0; -fx-selection-bar-non-focused: #f0f0f0;");

        // Alterner les couleurs des lignes
        reclamationTable.setRowFactory(param -> new TableRow<>() {
            @Override
            protected void updateItem(Reclammation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setStyle("");
                } else {
                    setStyle(getIndex() % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: #f5f5f5;");
                }
            }
        });
    }

    private void loadReclamations() {
        try {
            reclamations.setAll(reclammationService.rechercher());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réclamations : " + e.getMessage());
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(reclammation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String searchText = newValue.toLowerCase();
                return reclammation.getTitre().toLowerCase().contains(searchText) ||
                        reclammation.getDescription().toLowerCase().contains(searchText) ||
                        (reclammation.getUtilisateur() != null &&
                                reclammation.getUtilisateur().getEmail().toLowerCase().contains(searchText));
            });
        });
    }

    @FXML
    private void handleFilter() {
        String status = statusFilter.getValue();
        LocalDate date = dateFilter.getValue();
        String email = emailFilter.getText().toLowerCase();
        String sort = sortOrder.getValue();

        filteredData.setPredicate(reclammation -> {
            Statut requiredStatus = null;
            if (!status.equals("Tous")) {
                requiredStatus = switch (status) {
                    case "En attente" -> Statut.EN_ATTENTE;
                    case "En cours" -> Statut.EN_COURS;
                    case "Résolue" -> Statut.RESOLUE;
                    case "Rejetée" -> Statut.REJETEE;
                    default -> null;
                };
            }

            boolean statusMatch = status.equals("Tous") || reclammation.getStatut() == requiredStatus;
            boolean dateMatch = date == null || reclammation.getDateCreation().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate().equals(date);
            boolean emailMatch = email.isEmpty() ||
                    (reclammation.getUtilisateur() != null &&
                            reclammation.getUtilisateur().getEmail().toLowerCase().contains(email));
            return statusMatch && dateMatch && emailMatch;
        });

        // Appliquer le tri
        if (sort != null) {
            if (sort.equals("Plus récent")) {
                reclamationTable.getSortOrder().clear();
                reclamationTable.getSortOrder().add(dateColumn);
                dateColumn.setSortType(TableColumn.SortType.DESCENDING);
            } else {
                reclamationTable.getSortOrder().clear();
                reclamationTable.getSortOrder().add(dateColumn);
                dateColumn.setSortType(TableColumn.SortType.ASCENDING);
            }
        }
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        statusFilter.getSelectionModel().selectFirst();
        dateFilter.setValue(null);
        emailFilter.clear();
        sortOrder.getSelectionModel().clearSelection();
        filteredData.setPredicate(p -> true);
    }

    private void refreshTableView() {
        Platform.runLater(() -> {
            loadReclamations();
            handleFilter();
            reclamationTable.refresh();
        });
    }

    private void showResponseDialog(Reclammation reclammation) {
        // Vérifier si la réclamation a déjà une réponse
        if (reclammation.getReponse() != null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Cette réclamation a déjà une réponse.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Répondre à la réclamation");
        dialog.setHeaderText("Répondre à la réclamation #" + reclammation.getId());

        VBox content = new VBox(10);
        TextArea responseArea = new TextArea();
        responseArea.setPromptText("Entrez votre réponse...");
        responseArea.setPrefRowCount(5);

        ComboBox<String> statusComboBox = new ComboBox<>(FXCollections.observableArrayList(
                Statut.RESOLUE.getLabel(),
                Statut.REJETEE.getLabel()
        ));
        statusComboBox.setPromptText("Choisir le nouveau statut");

        content.getChildren().addAll(
                new Label("Réponse :"),
                responseArea,
                new Label("Nouveau statut :"),
                statusComboBox
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String response = responseArea.getText();
            String newStatus = statusComboBox.getValue();

            if (response.isEmpty() || newStatus == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs");
                return;
            }

            try {
                // Créer la réponse
                ReponseReclamation reponse = new ReponseReclamation(0, reclammation, response, new Date());

                // Mettre à jour le statut
                Statut nouveauStatut = switch (newStatus) {
                    case "Résolue" -> Statut.RESOLUE;
                    case "Rejetée" -> Statut.REJETEE;
                    default -> throw new IllegalArgumentException("Statut invalide");
                };
                reclammation.setStatut(nouveauStatut);

                // Sauvegarder la réponse dans la base de données
                reponseReclamationService.ajouter(reponse);

                // Mettre à jour la réclamation avec la réponse
                reclammation.setReponse(reponse);
                reclammationService.modifier(reclammation);

                // WhatsApp notification logic (system prompt and debug)
                System.out.println("[SYSTEM PROMPT] Attempting WhatsApp notification for reclamation.");
                if (reclammation.getUtilisateur() != null && reclammation.getUtilisateur().getTelephone() != null && !reclammation.getUtilisateur().getTelephone().isEmpty()) {
                    String userPhone = reclammation.getUtilisateur().getTelephone();
                    String statusMessage = nouveauStatut == Statut.RESOLUE ? "résolue" : "rejetée";
                    System.out.println("[SYSTEM PROMPT] WhatsApp message will be sent.");
                    System.out.println("[SYSTEM PROMPT] Recipient number: " + userPhone);
                    System.out.println("[SYSTEM PROMPT] Message content (status): " + statusMessage);
                    System.out.println("[SYSTEM PROMPT] Message content (admin response): " + response);
                    Utils.WhatsAppBusinessService.sendReclamationStatus(userPhone, statusMessage, response);
                } else {
                    System.out.println("[SYSTEM PROMPT] No phone number found for user of this reclamation.");
                }

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponse enregistrée avec succès");

                // Actualiser la vue
                refreshTableView();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer la réponse : " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}