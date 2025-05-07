package Models;

import Models.Commande;
import Models.CommandeProduit;
import Models.Produit;
import Models.StatutCommande;
import Services.CommandeService;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class StatistiqueCommande {
    private final CommandeService commandeService;

    public StatistiqueCommande(CommandeService commandeService) {
        this.commandeService = Objects.requireNonNull(commandeService, "commandeService ne peut pas être null");
    }

    private LocalDate convertToLocalDate(java.util.Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    // Chiffre d'affaires total pour une période
    public double getChiffreAffairesTotal(LocalDate debut, LocalDate fin) {
        return commandeService.rechercher().stream()
                .filter(c -> {
                    LocalDate dateCommande = convertToLocalDate(c.getDateCommande());
                    return !dateCommande.isBefore(debut) && !dateCommande.isAfter(fin);
                })
                .mapToDouble(Commande::getTotal)
                .sum();
    }

    // Nombre total de commandes pour une période
    public long getNombreCommandes(LocalDate debut, LocalDate fin) {
        return commandeService.rechercher().stream()
                .filter(c -> {
                    LocalDate dateCommande = convertToLocalDate(c.getDateCommande());
                    return !dateCommande.isBefore(debut) && !dateCommande.isAfter(fin);
                })
                .count();
    }

    // Répartition des commandes par statut
    public Map<StatutCommande, Long> getRepartitionParStatut() {
        return commandeService.rechercher().stream()
                .collect(Collectors.groupingBy(Commande::getStatut, Collectors.counting()));
    }

    // Top 5 produits les plus vendus (compte les occurrences au lieu de sommer les quantités)
    public List<ProduitStat> getTopProduits(int limit) {
        return commandeService.rechercher().stream()
                .flatMap(c -> c.getProduits().stream())
                .collect(Collectors.groupingBy(
                        CommandeProduit::getProduit,
                        Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new ProduitStat(entry.getKey(), entry.getValue().intValue()))
                .sorted(Comparator.comparingInt(ProduitStat::getQuantité).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Ventes quotidiennes pour un graphique
    public Map<LocalDate, Double> getVentesQuotidiennes(LocalDate debut, LocalDate fin) {
        Map<LocalDate, Double> ventes = new TreeMap<>();
        for (LocalDate date = debut; !date.isAfter(fin); date = date.plusDays(1)) {
            ventes.put(date, 0.0);
        }
        commandeService.rechercher().forEach(c -> {
            LocalDate dateCommande = convertToLocalDate(c.getDateCommande());
            if (!dateCommande.isBefore(debut) && !dateCommande.isAfter(fin)) {
                ventes.merge(dateCommande, c.getTotal(), Double::sum);
            }
        });
        return ventes;
    }

    // Classe interne pour les statistiques de produits
    public static class ProduitStat {
        private final Produit produit;
        private final int quantité;

        public ProduitStat(Produit produit, int quantité) {
            this.produit = produit;
            this.quantité = quantité;
        }

        public Produit getProduit() {
            return produit;
        }

        public int getQuantité() {
            return quantité;
        }
    }
}