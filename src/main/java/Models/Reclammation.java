package Models;
import java.util.Date;

public class Reclammation {
    private int id;
    private Utilisateur utilisateur;
    private String titre;
    private String description;
    private Date dateCreation;
    private String statut;

    // Constructor
    public Reclammation(int id, Utilisateur utilisateur, String titre, String description, Date dateCreation, String statut) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.titre = titre;
        this.description = description;
        this.dateCreation = dateCreation;
        this.statut = statut;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}