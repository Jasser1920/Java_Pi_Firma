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
    private int quantite;
    private Date dateExpiration;

    // Constructeur
    public Produit(int id, Categorie categorie, Utilisateur utilisateur, String nom, double prix, String description,
                   String image, int quantite, Date dateExpiration) {
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

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public int getCategorieId() {
        return (categorie != null) ? categorie.getId() : 0;
    }

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

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public java.sql.Date getDateExpiration() {
        return (dateExpiration != null) ? new java.sql.Date(dateExpiration.getTime()) : null;
    }

    public void setDateExpiration(Date dateExpiration) { this.dateExpiration = dateExpiration; }

    public String getNomCategorie() {
        return (categorie != null) ? categorie.getNomCategorie() : "";
    }

}
