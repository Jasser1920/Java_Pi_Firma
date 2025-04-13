package controllers;

import Models.ReponseReclamation;
import Models.Reclammation;
import Services.ReponseReclamationService;
import Services.ReclammationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Date;

public class AjouterReponseReclamationController {

    @FXML
    private TextField reclamationIdFx;

    @FXML
    private TextArea messageFx;

    private final ReponseReclamationService reponseService = new ReponseReclamationService();
    private final ReclammationService reclammationService = new ReclammationService();
    private ReponseReclamation reponseToEdit;
    private static final int MAX_MESSAGE_LENGTH = 500; // Longueur maximale du message

    @FXML
    @SuppressWarnings("unused")
    public void AjouterReponse(ActionEvent actionEvent) {
        String reclamationIdStr = reclamationIdFx.getText().trim();
        String message = messageFx.getText().trim();

        // Contrôle de saisie : Vérification des champs vides
        if (reclamationIdStr.isEmpty() || message.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "Tous les champs doivent être remplis.");
            return;
        }

        // Contrôle de saisie : Vérification que l'ID est un nombre positif
        int reclamationId;
        try {
            reclamationId = Integer.parseInt(reclamationIdStr);
            if (reclamationId <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "L'ID de réclamation doit être un nombre positif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "L'ID de réclamation doit être un nombre valide.");
            return;
        }

        // Contrôle de saisie : Vérification de la longueur du message
        if (message.length() > MAX_MESSAGE_LENGTH) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "Le message ne peut pas dépasser " + MAX_MESSAGE_LENGTH + " caractères.");
            return;
        }

        // Contrôle de saisie : Vérification que le message contient du contenu significatif
        if (!message.matches(".*[a-zA-Z0-9].*")) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Saisie", "Le message doit contenir des lettres ou des chiffres significatifs (pas seulement des espaces ou des caractères spéciaux).");
            return;
        }

        // Vérification de l'existence de la réclamation
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

        // Ajout ou modification de la réponse
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
    @SuppressWarnings("unused")
    public void AnnulerReponse(ActionEvent actionEvent) {
        Stage stage = (Stage) reclamationIdFx.getScene().getWindow();
        stage.close();
    }

    public void setReponse(ReponseReclamation reponse) {
        this.reponseToEdit = reponse;
        reclamationIdFx.setText(String.valueOf(reponse.getReclamation() != null ? reponse.getReclamation().getId() : ""));
        messageFx.setText(reponse.getMessage());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}