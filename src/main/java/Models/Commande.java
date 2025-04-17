package Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commande {
    private int id;
    private Livraison livraison;
    private Date dateCommande;
    private double total;
    private StatutCommande statut;
    private List<CommandeProduit> produits;

    public Commande() {
        this.produits = new ArrayList<>();
    }

    public Commande(int id, Livraison livraison, Date dateCommande, double total, StatutCommande statut, List<CommandeProduit> produits) {
        this.id = id;
        this.livraison = livraison;
        this.dateCommande = dateCommande;
        this.total = total;
        this.statut = statut;
        this.produits = produits != null ? produits : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Livraison getLivraison() {
        return livraison;
    }

    public void setLivraison(Livraison livraison) {
        this.livraison = livraison;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public List<CommandeProduit> getProduits() {
        return produits;
    }

    public void setProduits(List<CommandeProduit> produits) {
        this.produits = produits != null ? produits : new ArrayList<>();
    }
}