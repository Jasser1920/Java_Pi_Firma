package Models;
import java.util.Date;

public class Participation {
    private int id;
    private Utilisateur user;
    private Evenemment evenement;
    private Date participationDate;

    // Constructor
    public Participation(int id, Utilisateur user, Evenemment evenement, Date participationDate) {
        this.id = id;
        this.user = user;
        this.evenement = evenement;
        this.participationDate = participationDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Utilisateur getUser() { return user; }
    public void setUser(Utilisateur user) { this.user = user; }
    public Evenemment getEvenement() { return evenement; }
    public void setEvenement(Evenemment evenement) { this.evenement = evenement; }
    public Date getParticipationDate() { return participationDate; }
    public void setParticipationDate(Date participationDate) { this.participationDate = participationDate; }
}