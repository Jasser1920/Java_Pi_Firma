package controllers;

import Models.ReponseReclamation;
import Models.Reclammation;
import Services.ReponseReclamationService;
import Services.ReclammationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Date;

public class AjouterReponseReclamationController {

    @FXML
    private TextField reclamationIdFx;

    @FXML
    private TextArea messageFx;

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button homeButton;

    @FXML
    private Label reclamationIdError;

    @FXML
    private Label messageError;

    private final ReponseReclamationService reponseService = new ReponseReclamationService();
    private final ReclammationService reclammationService = new ReclammationService();
    private ReponseReclamation reponseToEdit;
    private static final int MAX_MESSAGE_LENGTH = 500;

    @FXML
    public void initialize() {
        if (reponseToEdit != null) {
            reclamationIdFx.setText(String.valueOf(reponseToEdit.getReclamation() != null ? reponseToEdit.getReclamation().getId() : ""));
            messageFx.setText(reponseToEdit.getMessage());
        }
        validateForm(); // Initial validation
    }

    @FXML
    public void AjouterReponse(ActionEvent actionEvent) {
        String reclamationIdStr = reclamationIdFx.getText().trim();
        String message = messageFx.getText().trim();

        if (!validateForm()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez corriger les erreurs dans le formulaire.");
            return;
        }

        int reclamationId = Integer.parseInt(reclamationIdStr); // Already validated
        Reclammation reclamation = null;
        try {
            for (Reclammation r : reclammationService.rechercher()) {
                if (r.getId() == reclamationId) {
                    reclamation = r;
                    break;
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche de réclamation : " + e.getMessage());
            return;
        }

        if (reclamation == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune réclamation trouvée avec l'ID : " + reclamationId);
            return;
        }

        try {
            if (reponseToEdit != null) {
                reponseToEdit.setReclamation(reclamation);
                reponseToEdit.setMessage(message);
                reponseService.modifier(reponseToEdit);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponse modifiée avec succès !");
            } else {
                ReponseReclamation reponse = new ReponseReclamation(
                        0, reclamation, message, new Date()
                );
                reponseService.ajouter(reponse);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponse ajoutée avec succès !");
            }
            Stage stage = (Stage) reclamationIdFx.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de sauvegarder la réponse : " + e.getMessage());
        }
    }

    @FXML
    public void AnnulerReponse(ActionEvent actionEvent) {
        Stage stage = (Stage) reclamationIdFx.getScene().getWindow();
        stage.close();
    }

    @FXML
    public boolean validateForm() {
        String reclamationIdStr = reclamationIdFx.getText().trim();
        String message = messageFx.getText().trim();
        boolean isValid = true;

        // Validate reclamation ID
        int reclamationId;
        if (reclamationIdStr.isEmpty()) {
            reclamationIdError.setText("L'ID de réclamation est requis.");
            isValid = false;
        } else {
            try {
                reclamationId = Integer.parseInt(reclamationIdStr);
                if (reclamationId <= 0) {
                    reclamationIdError.setText("L'ID doit être un nombre positif.");
                    isValid = false;
                } else {
                    // Check if reclamation exists
                    boolean exists = false;
                    try {
                        for (Reclammation r : reclammationService.rechercher()) {
                            if (r.getId() == reclamationId) {
                                exists = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        reclamationIdError.setText("Erreur de vérification de l'ID.");
                        isValid = false;
                    }
                    if (!exists) {
                        reclamationIdError.setText("Aucune réclamation avec cet ID.");
                        isValid = false;
                    } else {
                        reclamationIdError.setText("");
                    }
                }
            } catch (NumberFormatException e) {
                reclamationIdError.setText("L'ID doit être un nombre valide.");
                isValid = false;
            }
        }

        // Validate message
        if (message.isEmpty()) {
            messageError.setText("Le message est requis.");
            isValid = false;
        } else if (message.length() > MAX_MESSAGE_LENGTH) {
            messageError.setText("Le message ne peut pas dépasser " + MAX_MESSAGE_LENGTH + " caractères.");
            isValid = false;
        } else if (!message.matches(".*[a-zA-Z0-9].*")) {
            messageError.setText("Le message doit contenir des lettres ou chiffres.");
            isValid = false;
        } else {
            messageError.setText("");
        }

        // Enable/disable Ajouter button
        ajouterBtn.setDisable(!isValid);
        return isValid;
    }

    @FXML
    public void navigateHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de Bord");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner au tableau de bord : " + e.getMessage());
        }
    }

    public void setReponse(ReponseReclamation reponse) {
        this.reponseToEdit = reponse;
        if (reponse != null) {
            reclamationIdFx.setText(String.valueOf(reponse.getReclamation() != null ? reponse.getReclamation().getId() : ""));
            messageFx.setText(reponse.getMessage());
            validateForm();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}