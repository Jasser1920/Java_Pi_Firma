package Models;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "reclammations")
public class Reclammation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    private String titre;
    private String description;

    @Temporal(TemporalType.DATE)
    private Date dateCreation;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reponse_id")
    private ReponseReclamation reponse;

    public Reclammation() {
        this.dateCreation = new Date();
        this.statut = Statut.EN_ATTENTE;
    }

    public Reclammation(int id, Utilisateur utilisateur, String titre, String description, Date dateCreation, Statut statut) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.titre = titre;
        this.description = description;
        this.dateCreation = dateCreation;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public ReponseReclamation getReponse() {
        return reponse;
    }

    public void setReponse(ReponseReclamation reponse) {
        this.reponse = reponse;
    }

    @Override
    public String toString() {
        return "Reclammation{" +
                "id=" + id +
                ", utilisateur=" + (utilisateur != null ? utilisateur.getEmail() : "N/A") +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", dateCreation=" + dateCreation +
                ", statut=" + statut.getLabel() +
                '}';
    }
}