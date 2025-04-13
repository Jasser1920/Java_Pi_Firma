package Models;

import java.util.Date;

public class Location {
    private int id;
    private Terrain terrain;
    private Date dateDebut;
    private Date dateFin;
    private double prixTotal;
    private String modePaiement;

    // Constructor
    public Location(int id, Terrain terrain, Date dateDebut, Date dateFin, double prixTotal, String modePaiement) {
        this.id = id;
        this.terrain = terrain;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixTotal = prixTotal;
        this.modePaiement = modePaiement;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Terrain getTerrain() { return terrain; }
    public void setTerrain(Terrain terrain) { this.terrain = terrain; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }
    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
}