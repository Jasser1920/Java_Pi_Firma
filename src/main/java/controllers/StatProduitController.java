package controllers;

import Models.Categorie;
import Services.CategorieService;
import Services.StatistiqueProduitService;
import Utils.ChartGenerator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.util.Map;
import java.util.stream.Collectors;

public class StatProduitController {

    @FXML
    private javafx.scene.control.Button btnFiltrer;
    @FXML
    private javafx.scene.control.Button btnReinitialiser;

    @FXML
    private ImageView graph1, graph2, graph3, graph4, graph5;
    @FXML
    private ComboBox<Categorie> categorieFilterFX;
    @FXML
    private javafx.scene.control.ListView<String> stockFaibleListFX;


    private final CategorieService categorieService = new CategorieService();

    @FXML
    public void initialize() {
        btnFiltrer.setOnAction(e -> rafraichirGraphes());
        btnReinitialiser.setOnAction(e -> {
            categorieFilterFX.setValue(null);
            rafraichirGraphes();
        });
        try {
            categorieFilterFX.setItems(FXCollections.observableArrayList(categorieService.rechercher()));
            categorieFilterFX.setOnAction(e -> rafraichirGraphes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        rafraichirGraphes();
    }

    private void rafraichirGraphes() {
        afficherGraphique1();
        afficherGraphique2();
        afficherGraphique3();
        afficherGraphique4();
        /*afficherGraphique5();*/
        afficherProduitsStockFaible();

    }

    private void afficherGraphique1() {
        Map<String, Integer> data = StatistiqueProduitService.getProduitsParCategorie();
        Categorie selected = categorieFilterFX.getValue();
        if (selected != null) {
            // le filtrage par catégorie est maintenant fait dans le service, donc inutile ici
        }
        String url = ChartGenerator.generateCustomBarChartUrl(data, "Produits par catégorie", "#FFA500", true, "black", "bar");
        graph1.setImage(new Image(url));
    }

    private void afficherGraphique2() {
        Map<String, Integer> data = StatistiqueProduitService.getStockParCategorie();
        Categorie selected = categorieFilterFX.getValue();
        if (selected != null) {
            data = data.entrySet().stream()
                    .filter(e -> e.getKey().equalsIgnoreCase(selected.getNomCategorie()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        String url = ChartGenerator.generateCustomBarChartUrl(data, "Stock total par catégorie", "#85C20A", true, "black", "bar");
        graph2.setImage(new Image(url));
    }

    private void afficherGraphique3() {
        Map<String, Double> data = StatistiqueProduitService.getPrixMoyenParCategorie();
        Categorie selected = categorieFilterFX.getValue();
        if (selected != null) {
            data = data.entrySet().stream()
                    .filter(e -> e.getKey().equalsIgnoreCase(selected.getNomCategorie()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        Map<String, Integer> arrondi = data.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (int) Math.round(e.getValue())));
        String url = ChartGenerator.generateCustomBarChartUrl(arrondi, "Prix moyen par catégorie", "#FFB524", true, "black", "bar");
        graph3.setImage(new Image(url));
    }

    private void afficherGraphique4() {
        Categorie selected = categorieFilterFX.getValue();
        String nomCategorie = selected != null ? selected.getNomCategorie() : null;
        Map<String, Integer> data = StatistiqueProduitService.getProduitsExpirantsBientot(nomCategorie);
        String url = ChartGenerator.generateCustomBarChartUrl(data, "Produits expirants bientôt", "#f59c95", true, "black", "bar");
        graph4.setImage(new Image(url));
    }
/*
    private void afficherGraphique5() {
        Map<String, Integer> data = StatistiqueProduitService.getProduitsParStockCritique();
        Categorie selected = categorieFilterFX.getValue();
        if (selected != null) {
            data = data.entrySet().stream()
                    .filter(e -> e.getKey().equalsIgnoreCase(selected.getNomCategorie()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        String url = ChartGenerator.generateCustomBarChartUrl(data, "État du stock", "#f59c95", true, "black", "doughnut");
        graph5.setImage(new Image(url));
    }*/

    @FXML
    private void RetourProduit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void afficherProduitsStockFaible() {
        var liste = StatistiqueProduitService.getProduitsAvecStockFaible();
        Categorie selected = categorieFilterFX.getValue();

        if (selected != null) {
            liste = liste.stream()
                    .filter(p -> p.getNomCategorie().equalsIgnoreCase(selected.getNomCategorie()))
                    .collect(Collectors.toList());
        }

        stockFaibleListFX.getItems().clear();
        for (var p : liste) {
            stockFaibleListFX.getItems().add("🔸 " + p.getNom() + " (" + p.getQuantite() + " unités)");
        }
    }

}
