package Models;

import java.util.Date;

public class Livraison {
    private int id;
    private String nomSociete;
    private String adresseLivraison;
    private Date dateLivraison;
    private String statut;

    // Constructor
    public Livraison(int id, String nomSociete, String adresseLivraison, Date dateLivraison, String statut) {
        this.id = id;
        this.nomSociete = nomSociete;
        this.adresseLivraison = adresseLivraison;
        this.dateLivraison = dateLivraison;
        this.statut = statut;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNomSociete() { return nomSociete; }
    public void setNomSociete(String nomSociete) { this.nomSociete = nomSociete; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }
    public Date getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(Date dateLivraison) { this.dateLivraison = dateLivraison; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}