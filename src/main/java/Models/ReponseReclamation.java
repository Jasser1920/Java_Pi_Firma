package Models;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "reponses_reclamations")
public class ReponseReclamation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "reponse")
    private Reclammation reclamation;

    private String message;

    @Temporal(TemporalType.DATE)
    private Date dateReponse;

    // Constructor
    public ReponseReclamation(int id, Reclammation reclamation, String message, Date dateReponse) {
        this.id = id;
        this.reclamation = reclamation;
        this.message = message;
        this.dateReponse = dateReponse;
    }

    // Default constructor for JPA
    public ReponseReclamation() {
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Reclammation getReclamation() { return reclamation; }
    public void setReclamation(Reclammation reclamation) { this.reclamation = reclamation; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Date getDateReponse() { return dateReponse; }
    public void setDateReponse(Date dateReponse) { this.dateReponse = dateReponse; }
}