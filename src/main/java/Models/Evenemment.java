package Models;

import java.util.Date;

public class Evenemment {
    private int id;
    private Utilisateur utilisateur;
    private String titre;
    private String desecription; // Note: Typo in DB as "desecription", should be "description"
    private Date date;
    private String lieux;
    private String image;

    // Constructor
    public Evenemment(int id, Utilisateur utilisateur, String titre, String desecription, Date date, String lieux, String image) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.titre = titre;
        this.desecription = desecription;
        this.date = date;
        this.lieux = lieux;
        this.image = image;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDesecription() { return desecription; }
    public void setDesecription(String desecription) { this.desecription = desecription; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getLieux() { return lieux; }
    public void setLieux(String lieux) { this.lieux = lieux; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    @Override
    public String toString() {
        return titre;
    }
}