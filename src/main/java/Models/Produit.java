package Models;

import java.util.Date;

public class Produit {
    private int id;
    private Categorie categorie;
    private Utilisateur utilisateur;
    private String nom;
    private double prix;
    private String description;
    private String image;
    private String quantite;
    private Date dateExpiration;

    // Constructor
    public Produit(int id, Categorie categorie, Utilisateur utilisateur, String nom, double prix, String description,
                   String image, String quantite, Date dateExpiration) {
        this.id = id;
        this.categorie = categorie;
        this.utilisateur = utilisateur;
        this.nom = nom;
        this.prix = prix;
        this.description = description;
        this.image = image;
        this.quantite = quantite;
        this.dateExpiration = dateExpiration;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getQuantite() { return quantite; }
    public void setQuantite(String quantite) { this.quantite = quantite; }
    public Date getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(Date dateExpiration) { this.dateExpiration = dateExpiration; }
}