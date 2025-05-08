package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.File;
import java.io.IOException;
import Models.Utilisateur;
import Services.UtilisateurService;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class UserTableController {
    @FXML private TableView<Utilisateur> userTable;
    @FXML private TableColumn<Utilisateur, String> colNom;
    @FXML private TableColumn<Utilisateur, String> colPrenom;
    @FXML private TableColumn<Utilisateur, String> colEmail;
    @FXML private TableColumn<Utilisateur, String> colTelephone;
    @FXML private TableColumn<Utilisateur, String> colAdresse;
    @FXML private TableColumn<Utilisateur, String> colRole;
    @FXML private TableColumn<Utilisateur, String> colProfilePicture;
    @FXML private TableColumn<Utilisateur, Void> colActions;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private Button clearSearchButton;
    @FXML private Button refreshButton;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private ObservableList<Utilisateur> masterData = FXCollections.observableArrayList();
    private FilteredList<Utilisateur> filteredData;

    @FXML
    public void initialize() {
        System.out.println("UserTableController: Initializing...");
        // Initialize table columns
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colRole.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole() != null ? cellData.getValue().getRole().getLabel() : "");
        });
        colProfilePicture.setCellValueFactory(new PropertyValueFactory<>("profilePicture"));
        colProfilePicture.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                // Set a fixed size for the ImageView
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true); // Maintain aspect ratio
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    try {
                        // Convert the local file path to a file: URL
                        File file = new File(item);
                        String fileUrl = file.toURI().toString(); // Converts to file:/path format
                        Image image = new Image(fileUrl, 50, 50, true, true); // Load image with specified size
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        System.err.println("Failed to load image: " + item + " - Error: " + e.getMessage());
                        setGraphic(new Label("Image not found"));
                    }
                }
            }
        });
        addActionButtonsToTable();

        // Initialize search combo with role filtering option
        searchTypeCombo.setItems(FXCollections.observableArrayList("All", "Nom", "Prénom", "Email", "Téléphone", "Adresse", "Role"));
        searchTypeCombo.getSelectionModel().select("All");

        // Load data and setup filtering
        loadUsers();
        setupSearchFilter();

        System.out.println("UserTableController: Initialization complete.");
    }

    private void loadUsers() {
        try {
            System.out.println("UserTableController: Loading users...");
            masterData.setAll(utilisateurService.getAllUsers().stream()
                    .filter(user -> user.getNom() != null && user.getPrenom() != null &&
                            user.getEmail() != null && user.getTelephone() != null &&
                            user.getAdresse() != null && user.getRole() != null)
                    .collect(Collectors.toList()));
            System.out.println("UserTableController: Loaded " + masterData.size() + " users.");
            if (filteredData == null) {
                filteredData = new FilteredList<>(masterData, p -> true);
                userTable.setItems(filteredData);
            }
        } catch (SQLException e) {
            System.err.println("Failed to load users: " + e.getMessage());
            userTable.setItems(FXCollections.observableArrayList());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load users: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                String searchTypeValue = searchTypeCombo.getValue();
                String searchType = searchTypeValue != null ? searchTypeValue : "All";

                switch (searchType) {
                    case "Nom":
                        return user.getNom() != null && user.getNom().toLowerCase().contains(lowerCaseFilter);
                    case "Prénom":
                        return user.getPrenom() != null && user.getPrenom().toLowerCase().contains(lowerCaseFilter);
                    case "Email":
                        return user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseFilter);
                    case "Téléphone":
                        return user.getTelephone() != null && user.getTelephone().toLowerCase().contains(lowerCaseFilter);
                    case "Adresse":
                        return user.getAdresse() != null && user.getAdresse().toLowerCase().contains(lowerCaseFilter);
                    case "Role":
                        return user.getRole() != null && user.getRole().getLabel() != null &&
                                user.getRole().getLabel().toLowerCase().contains(lowerCaseFilter);
                    default: // All
                        return (user.getNom() != null && user.getNom().toLowerCase().contains(lowerCaseFilter)) ||
                                (user.getPrenom() != null && user.getPrenom().toLowerCase().contains(lowerCaseFilter)) ||
                                (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseFilter)) ||
                                (user.getTelephone() != null && user.getTelephone().toLowerCase().contains(lowerCaseFilter)) ||
                                (user.getAdresse() != null && user.getAdresse().toLowerCase().contains(lowerCaseFilter)) ||
                                (user.getRole() != null && user.getRole().getLabel() != null &&
                                        user.getRole().getLabel().toLowerCase().contains(lowerCaseFilter));
                }
            });
        });
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        searchTypeCombo.getSelectionModel().select("All");
    }

    @FXML
    private void refreshTable() {
        loadUsers();
        clearSearch();
    }

    private void addActionButtonsToTable() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Delete");
            private final Button btnUpdate = new Button("Update");
            {
                btnDelete.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    try {
                        utilisateurService.supprimer(user);
                        loadUsers();
                    } catch (SQLException e) {
                        System.err.println("Failed to delete user: " + e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete user: " + e.getMessage());
                        alert.showAndWait();
                    }
                });
                btnUpdate.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierUtilisateur.fxml"));
                        Parent root = loader.load();
                        ModifierUtilisateurController controller = loader.getController();
                        controller.setUtilisateur(user);
                        Stage stage = new Stage();
                        stage.setTitle("Modifier Utilisateur");
                        stage.setScene(new Scene(root));
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.showAndWait();
                        loadUsers();
                    } catch (IOException e) {
                        System.err.println("Failed to open update form: " + e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire de modification: " + e.getMessage());
                        alert.showAndWait();
                    }
                });
            }
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnUpdate, btnDelete));
            }
        });
    }
}