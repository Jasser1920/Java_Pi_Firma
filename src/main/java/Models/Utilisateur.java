package Models;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String prenom;
    private String email;
    private String motdepasse;
    private String telephone;
    private String adresse;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String profilePicture;
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
                       String adresse, Role role, String profilePicture, boolean blocked, boolean isVerified,
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

    // Default constructor for JPA
    public Utilisateur() {
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

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { this.isVerified = verified; }

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