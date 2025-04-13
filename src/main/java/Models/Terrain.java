package Models;

import java.util.Date;

public class Terrain {
    private int id;
    private Utilisateur utilisateur;
    private String description;
    private double superficie;
    private Double latitude; // Nullable
    private Double longitude; // Nullable
    private double prixLocation;
    private boolean disponibilite;
    private Date dateCreation;

    // Constructor
    public Terrain(int id, Utilisateur utilisateur, String description, double superficie, Double latitude,
                   Double longitude, double prixLocation, boolean disponibilite, Date dateCreation) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.description = description;
        this.superficie = superficie;
        this.latitude = latitude;
        this.longitude = longitude;
        this.prixLocation = prixLocation;
        this.disponibilite = disponibilite;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getSuperficie() { return superficie; }
    public void setSuperficie(double superficie) { this.superficie = superficie; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public double getPrixLocation() { return prixLocation; }
    public void setPrixLocation(double prixLocation) { this.prixLocation = prixLocation; }
    public boolean isDisponibilite() { return disponibilite; }
    public void setDisponibilite(boolean disponibilite) { this.disponibilite = disponibilite; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
}