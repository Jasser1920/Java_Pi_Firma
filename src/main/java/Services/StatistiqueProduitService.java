package Services;

import Models.Categorie;
import Models.Produit;
import Services.CategorieService;
import Services.ProduitService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatistiqueProduitService {

    public static Map<String, Integer> getProduitsParCategorie() {
        Map<String, Integer> result = new HashMap<>();
        CategorieService categorieService = new CategorieService();
        ProduitService produitService = new ProduitService();

        try {
            List<Categorie> categories = categorieService.rechercher();

            for (Categorie cat : categories) {
                int count = produitService.countByCategorie(cat.getId());
                result.put(cat.getNomCategorie(), count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Map<String, Integer> getStockParCategorie() {
        Map<String, Integer> result = new HashMap<>();
        CategorieService categorieService = new CategorieService();
        ProduitService produitService = new ProduitService();

        try {
            List<Categorie> categories = categorieService.rechercher();

            for (Categorie cat : categories) {
                int sum = produitService.sumQuantiteByCategorie(cat.getId());
                result.put(cat.getNomCategorie(), sum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Map<String, Double> getPrixMoyenParCategorie() {
        Map<String, Double> result = new HashMap<>();
        CategorieService categorieService = new CategorieService();
        ProduitService produitService = new ProduitService();

        try {
            List<Categorie> categories = categorieService.rechercher();

            for (Categorie cat : categories) {
                double moyenne = produitService.avgPrixByCategorie(cat.getId());
                result.put(cat.getNomCategorie(), moyenne);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Map<String, Integer> getProduitsExpirantsBientot(String categorie) {
        Map<String, Integer> result = new HashMap<>();
        ProduitService produitService = new ProduitService();
        List<Produit> produits = produitService.afficher();

        int moins7 = 0, moins30 = 0, plus30 = 0;
        LocalDate now = LocalDate.now();

        for (Produit p : produits) {
            if (categorie != null && !p.getCategorie().getNomCategorie().equalsIgnoreCase(categorie)) continue;

            if (p.getDateExpiration().toLocalDate().isBefore(now.plusDays(7))) {
                moins7++;
            } else if (p.getDateExpiration().toLocalDate().isBefore(now.plusDays(30))) {
                moins30++;
            } else {
                plus30++;
            }
        }



        result.put("< 7j", moins7);
        result.put("< 30j", moins30);
        result.put("> 30j", plus30);
        return result;
    }

    public static Map<String, Integer> getProduitsParStockCritique() {
        Map<String, Integer> result = new HashMap<>();
        ProduitService produitService = new ProduitService();
        List<Produit> produits = produitService.afficher();

        int rupture = 0, critique = 0, ok = 0;

        for (Produit p : produits) {
            int q = p.getQuantite();
            if (q == 0) rupture++;
            else if (q <= 5) critique++;
            else ok++;
        }

        result.put("Rupture", rupture);
        result.put("Critique", critique);
        result.put("OK", ok);
        return result;
    }

    public static Map<String, Integer> getStockParProduit() {
        ProduitService produitService = new ProduitService();
        Map<String, Integer> result = new HashMap<>();

        try {
            for (Produit p : produitService.afficher()) {
                result.put(p.getNom(), p.getQuantite());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static List<Produit> getProduitsAvecStockFaible() {
        return new ProduitService().afficher()
                .stream()
                .filter(p -> p.getQuantite() < 5)
                .collect(Collectors.toList());
    }


}
