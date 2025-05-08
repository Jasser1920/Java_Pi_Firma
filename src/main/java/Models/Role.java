package Models;

public enum Role {
    ADMIN("Admin"),
    AGRICULTURE("Agriculture"),
    CLIENT("Client"),
    ASSOCIATION("Association");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Role fromString(String value) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(value) || role.label.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
