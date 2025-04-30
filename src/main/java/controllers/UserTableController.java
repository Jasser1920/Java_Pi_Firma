package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import Models.Utilisateur;
import Services.UtilisateurService;

import java.sql.SQLException;
import java.util.Comparator;

public class UserTableController {
    @FXML private TableView<Utilisateur> userTable;
    @FXML private TableColumn<Utilisateur, String> colUsername;
    @FXML private TableColumn<Utilisateur, String> colEmail;
    @FXML private TableColumn<Utilisateur, String> colRole;
    @FXML private TableColumn<Utilisateur, Void> colActions;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private ComboBox<String> sortColumnCombo;
    @FXML private ToggleButton sortOrderToggle;
    @FXML private Button clearSearchButton;
    @FXML private Button refreshButton;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private ObservableList<Utilisateur> masterData = FXCollections.observableArrayList();
    private FilteredList<Utilisateur> filteredData;
    private SortedList<Utilisateur> sortedData;

    @FXML
    public void initialize() {
        // Initialize table columns
        colUsername.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        addActionButtonsToTable();

        // Initialize search and sort combos
        searchTypeCombo.setItems(FXCollections.observableArrayList("All", "Username", "Email", "Role"));
        sortColumnCombo.setItems(FXCollections.observableArrayList("Username", "Email", "Role"));
        sortColumnCombo.getSelectionModel().selectFirst();
        sortOrderToggle.setText(sortOrderToggle.isSelected() ? "Ascending" : "Descending");

        // Load data and setup filtering/sorting
        loadUsers();
        setupSearchFilter();
        setupSorting();
    }

    private void loadUsers() {
        try {
            masterData.setAll(utilisateurService.getAllUsers());
            if (filteredData == null) {
                filteredData = new FilteredList<>(masterData, p -> true);
                sortedData = new SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(userTable.comparatorProperty());
                userTable.setItems(sortedData);
            }
        } catch (SQLException e) {
            System.err.println("Failed to load users: " + e.getMessage());
            userTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                String searchType = searchTypeCombo.getValue();

                switch (searchType) {
                    case "Username":
                        return user.getNom().toLowerCase().contains(lowerCaseFilter);
                    case "Email":
                        return user.getEmail().toLowerCase().contains(lowerCaseFilter);
                    case "Role":
                        return user.getRole().toLowerCase().contains(lowerCaseFilter);
                    default: // All
                        return user.getNom().toLowerCase().contains(lowerCaseFilter) ||
                                user.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                                user.getRole().toLowerCase().contains(lowerCaseFilter);
                }
            });
        });
    }

    private void setupSorting() {
        sortColumnCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateSort());
        sortOrderToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            sortOrderToggle.setText(newVal ? "Ascending" : "Descending");
            updateSort();
        });
    }

    private void updateSort() {
        userTable.getSortOrder().clear();
        String selectedColumn = sortColumnCombo.getValue();
        boolean isAscending = sortOrderToggle.isSelected();

        TableColumn<Utilisateur, ?> sortColumn = switch (selectedColumn) {
            case "Email" -> colEmail;
            case "Role" -> colRole;
            default -> colUsername;
        };

        userTable.getSortOrder().add(sortColumn);
        sortColumn.setSortType(isAscending ? TableColumn.SortType.ASCENDING : TableColumn.SortType.DESCENDING);
    }

    @FXML
    private void handleSearch() {
        // Triggered by pressing Enter in search field; already handled by textProperty listener
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        searchTypeCombo.getSelectionModel().select("All");
    }

    @FXML
    private void toggleSortOrder() {
        // Handled by selectedProperty listener
    }

    @FXML
    private void refreshTable() {
        loadUsers();
        clearSearch();
        sortColumnCombo.getSelectionModel().selectFirst();
        sortOrderToggle.setSelected(true);
        updateSort();
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
                    }
                });
                btnUpdate.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    // TODO: Show update dialog or navigate to update view
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