package Models;

import java.util.Date;
import java.util.List;

public class Commande {
    private int id;
    private Livraison livraison;
    private Date dateCommande;
    private double total;
    private String statut;
    private List<Produit> produits; // Many-to-many via commande_produit

    // Constructor
    public Commande(int id, Livraison livraison, Date dateCommande, double total, String statut, List<Produit> produits) {
        this.id = id;
        this.livraison = livraison;
        this.dateCommande = dateCommande;
        this.total = total;
        this.statut = statut;
        this.produits = produits;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Livraison getLivraison() { return livraison; }
    public void setLivraison(Livraison livraison) { this.livraison = livraison; }
    public Date getDateCommande() { return dateCommande; }
    public void setDateCommande(Date dateCommande) { this.dateCommande = dateCommande; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public List<Produit> getProduits() { return produits; }
    public void setProduits(List<Produit> produits) { this.produits = produits; }
}