package Models;

public class ProduitSelectionne {
    private Produit produit;
    private int quantite;

    public ProduitSelectionne(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
    }

    public Produit getProduit() {
        return produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixTotal() {
        return produit.getPrix() * quantite;
    }
}
