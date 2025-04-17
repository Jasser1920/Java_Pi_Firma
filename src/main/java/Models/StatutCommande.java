package Models;

public enum StatutCommande {
    en_attente,
    confirmee,
    annulee;

    @Override
    public String toString() {
        return name();
    }
}