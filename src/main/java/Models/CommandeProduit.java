package Models;

public class CommandeProduit {
    private Produit produit;
    private int quantite;

    public CommandeProduit(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandeProduit that = (CommandeProduit) o;
        return produit.getId() == that.produit.getId();
    }

    @Override
    public int hashCode() {
        return produit.getId();
    }
}