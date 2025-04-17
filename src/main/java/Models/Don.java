package Models;

import java.util.Date;

public class Don {
    private int id;
    private Evenemment evenement;
    private Utilisateur donsUser;
    private String donateur;
    private String description;
    private Date date;

    // Constructor
    public Don(int id, Evenemment evenement, Utilisateur donsUser, String donateur, String description, Date date) {
        this.id = id;
        this.evenement = evenement;
        this.donsUser = donsUser;
        this.donateur = donateur;
        this.description = description;
        this.date = date;
    }



    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Evenemment getEvenement() { return evenement; }
    public void setEvenement(Evenemment evenement) { this.evenement = evenement; }
    public Utilisateur getDonsUser() { return donsUser; }
    public void setDonsUser(Utilisateur donsUser) { this.donsUser = donsUser; }
    public String getDonateur() { return donateur; }
    public void setDonateur(String donateur) { this.donateur = donateur; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}