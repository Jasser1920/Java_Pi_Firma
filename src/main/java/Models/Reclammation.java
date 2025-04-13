package Models;

import java.util.Date;

public class Reclammation {
    private int id;
    private Utilisateur utilisateur;
    private String titre;
    private String description;
    private Date dateCreation;
    private Statut statut;
    private ReponseReclamation reponse;

    public Reclammation(int id, Utilisateur utilisateur, String titre, String description, Date dateCreation, Statut statut) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.titre = titre;
        this.description = description;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.reponse = null;
    }

    public Reclammation(int id, Utilisateur utilisateur, String titre, String description, Date dateCreation, String statut) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.titre = titre;
        this.description = description;
        this.dateCreation = dateCreation;
        this.statut = convertToStatut(statut);
        this.reponse = null;
    }

    private Statut convertToStatut(String statutStr) {
        if (statutStr == null) {
            return Statut.enattente;
        }
        String normalized = statutStr.trim().toLowerCase().replace(" ", "");
        try {
            return Statut.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            System.err.println("Valeur de statut invalide : " + statutStr + ". Utilisation de enattente par défaut.");
            return Statut.enattente;
        }
    }

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
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public ReponseReclamation getReponse() { return reponse; }
    public void setReponse(ReponseReclamation reponse) { this.reponse = reponse; }
}