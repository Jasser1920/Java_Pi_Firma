package Models;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    private int id;
    private final StringProperty nom;
    private final StringProperty prenom;
    private final StringProperty email;
    private final StringProperty motdepasse;
    private final StringProperty telephone;
    private final StringProperty adresse;
    private final StringProperty role;
    private final StringProperty profilePicture;
    private boolean blocked;
    private boolean isVerified;
    private String confirmationCode;
    @OneToMany(mappedBy = "dons_user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Don> dons = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Evenemment> evenement = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reclammation> reclamation = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Terrain> terrain = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Produit> produit = new HashSet<>();

    // Constructor
    public Utilisateur(int id, String nom, String prenom, String email, String motdepasse, String telephone,
                       String adresse, String role, String profilePicture, boolean blocked, boolean isVerified,
                       String confirmationCode) {
        this.id = id;
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.email = new SimpleStringProperty(email);
        this.motdepasse = new SimpleStringProperty(motdepasse);
        this.telephone = new SimpleStringProperty(telephone);
        this.adresse = new SimpleStringProperty(adresse);
        this.role = new SimpleStringProperty(role);
        this.profilePicture = new SimpleStringProperty(profilePicture);
        this.blocked = blocked;
        this.isVerified = isVerified;
        this.confirmationCode = confirmationCode;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }
    public StringProperty nomProperty() { return nom; }

    public String getPrenom() { return prenom.get(); }
    public void setPrenom(String prenom) { this.prenom.set(prenom); }
    public StringProperty prenomProperty() { return prenom; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public String getMotdepasse() { return motdepasse.get(); }
    public void setMotdepasse(String motdepasse) { this.motdepasse.set(motdepasse); }
    public StringProperty motdepasseProperty() { return motdepasse; }

    public String getTelephone() { return telephone.get(); }
    public void setTelephone(String telephone) { this.telephone.set(telephone); }
    public StringProperty telephoneProperty() { return telephone; }

    public String getAdresse() { return adresse.get(); }
    public void setAdresse(String adresse) { this.adresse.set(adresse); }
    public StringProperty adresseProperty() { return adresse; }

    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }
    public StringProperty roleProperty() { return role; }

    public String getProfilePicture() { return profilePicture.get(); }
    public void setProfilePicture(String profilePicture) { this.profilePicture.set(profilePicture); }
    public StringProperty profilePictureProperty() { return profilePicture; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public String getConfirmationCode() { return confirmationCode; }
    public void setConfirmationCode(String confirmationCode) { this.confirmationCode = confirmationCode; }
    public Set<Don> getDons() { return dons; }
    public void setDons(Set<Don> dons) { this.dons = dons; }
    public Set<Evenemment> getEvenement() { return evenement; }
    public void setEvenement(Set<Evenemment> evenement) { this.evenement = evenement; }
    public Set<Reclammation> getReclamation() { return reclamation; }
    public void setReclamation(Set<Reclammation> reclamation) { this.reclamation = reclamation; }
    public Set<Terrain> getTerrain() { return terrain; }
    public void setTerrain(Set<Terrain> terrain) { this.terrain = terrain; }
    public Set<Produit> getProduit() { return produit; }
    public void setProduit(Set<Produit> produit) { this.produit = produit; }
}