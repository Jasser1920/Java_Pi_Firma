package Models;

import java.util.Date;

public class ReponseReclamation {
    private int id;
    private Reclammation reclamation;
    private String message;
    private Date dateReponse;

    // Constructor
    public ReponseReclamation(int id, Reclammation reclamation, String message, Date dateReponse) {
        this.id = id;
        this.reclamation = reclamation;
        this.message = message;
        this.dateReponse = dateReponse;
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