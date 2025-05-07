package controllers;


import Services.CommandeService;
import Models.StatistiqueCommande;
import Models.StatistiqueCommande.ProduitStat;
import Models.StatutCommande;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class StatistiquesCommandeController {
    @FXML private ComboBox<String> periodeCombo;
    @FXML private Label chiffreAffairesLabel;
    @FXML private Label nombreCommandesLabel;
    @FXML private LineChart<String, Double> ventesChart;
    @FXML private BarChart<String, Number> produitsChart;
    @FXML private PieChart statutChart;
    @FXML private Button retourButton;

    private final StatistiqueCommande statsService = new StatistiqueCommande(new CommandeService());

    @FXML
    public void initialize() {
        // Initialiser le ComboBox
        periodeCombo.setItems(FXCollections.observableArrayList("Jour", "Semaine", "Mois", "Année"));
        periodeCombo.setValue("Mois"); // Valeur par défaut
        periodeCombo.setOnAction(e -> updateStats());

        // Configurer le bouton Retour
        retourButton.setOnAction(e -> goToAfficherCommande());

        // Charger les statistiques initiales
        updateStats();
    }

    private void updateStats() {
        // Déterminer la période
        LocalDate fin = LocalDate.now();
        LocalDate debut;
        switch (periodeCombo.getValue()) {
            case "Jour":
                debut = fin;
                break;
            case "Semaine":
                debut = fin.minusDays(7);
                break;
            case "Année":
                debut = fin.minusYears(1);
                break;
            default: // Mois
                debut = fin.minusMonths(1);
        }

        // Mettre à jour les métriques
        double ca = statsService.getChiffreAffairesTotal(debut, fin);
        chiffreAffairesLabel.setText(String.format("Chiffre d'affaires : %.2f €", ca));
        long nbCommandes = statsService.getNombreCommandes(debut, fin);
        nombreCommandesLabel.setText(String.format("Nombre de commandes : %d", nbCommandes));

        // Mettre à jour le graphique des ventes
        XYChart.Series<String, Double> ventesSeries = new XYChart.Series<>();
        ventesSeries.setName("Ventes (€)");
        statsService.getVentesQuotidiennes(debut, fin).forEach((date, montant) ->
                ventesSeries.getData().add(new XYChart.Data<>(
                        date.format(DateTimeFormatter.ofPattern("dd/MM")), montant)));
        ventesChart.getData().clear();
        ventesChart.getData().add(ventesSeries);

        // Mettre à jour le graphique des produits
        XYChart.Series<String, Number> produitSeries = new XYChart.Series<>();
        produitSeries.setName("Occurrences");
        statsService.getTopProduits(5).forEach(ps ->
                produitSeries.getData().add(new XYChart.Data<>(ps.getProduit().getNom(), ps.getQuantité())));
        produitsChart.getData().clear();
        produitsChart.getData().add(produitSeries);

        // Mettre à jour le graphique des statuts
        statutChart.getData().clear();
        statsService.getRepartitionParStatut().forEach((statut, count) ->
                statutChart.getData().add(new PieChart.Data(statut.toString(), count)));
    }

    @FXML
    private void goToAfficherCommande() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/affichercommand.fxml"));
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Liste des Commandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de affichercommand.fxml : " + e.getMessage());
        }
    }
}