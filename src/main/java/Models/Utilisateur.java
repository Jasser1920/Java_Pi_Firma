package Models;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motdepasse;
    private String telephone;
    private String adresse;
    private String role;
    private String profilePicture;
    private boolean blocked;
    private boolean isVerified;
    private String confirmationCode;

    // Constructor
    public Utilisateur(int id, String nom, String prenom, String email, String motdepasse, String telephone,
                       String adresse, String role, String profilePicture, boolean blocked, boolean isVerified,
                       String confirmationCode) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motdepasse = motdepasse;
        this.telephone = telephone;
        this.adresse = adresse;
        this.role = role;
        this.profilePicture = profilePicture;
        this.blocked = blocked;
        this.isVerified = isVerified;
        this.confirmationCode = confirmationCode;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotdepasse() { return motdepasse; }
    public void setMotdepasse(String motdepasse) { this.motdepasse = motdepasse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public String getConfirmationCode() { return confirmationCode; }
    public void setConfirmationCode(String confirmationCode) { this.confirmationCode = confirmationCode; }
}