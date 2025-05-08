package Models;

public enum Statut {
    EN_ATTENTE("en_attente", "En attente"),
    EN_COURS("en_cours", "En cours"),
    RESOLUE("resolue", "Résolue"),
    REJETEE("rejetee", "Rejetée");

    private final String value;
    private final String label;

    Statut(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static Statut fromString(String value) {
        if (value == null) {
            return EN_ATTENTE;
        }
        return switch (value.toLowerCase()) {
            case "en_attente" -> EN_ATTENTE;
            case "en_cours" -> EN_COURS;
            case "resolue" -> RESOLUE;
            case "rejetee" -> REJETEE;
            default -> EN_ATTENTE;
        };
    }
}