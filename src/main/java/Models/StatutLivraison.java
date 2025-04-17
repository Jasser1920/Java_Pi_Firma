package Models;

public enum StatutLivraison {
    en_attente,
    en_cours,
    livree;

    @Override
    public String toString() {
        return name();
    }
}