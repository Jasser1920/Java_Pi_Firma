package Services;

import Models.ReponseReclamation;
import Models.Reclammation;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseReclamationService implements IService<ReponseReclamation> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(ReponseReclamation reponse) {
        String req = "INSERT INTO `reponse_reclamation` (`reclamation_id`, `message`, `date_reponse`) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, reponse.getReclamation() != null ? reponse.getReclamation().getId() : null);
            ps.setString(2, reponse.getMessage());
            ps.setTimestamp(3, new java.sql.Timestamp(reponse.getDateReponse().getTime()));
            ps.executeUpdate();
            System.out.println("Réponse à réclamation ajoutée");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void modifier(ReponseReclamation reponse) {
        String req = "UPDATE `reponse_reclamation` SET `reclamation_id`=?, `message`=?, `date_reponse`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, reponse.getReclamation() != null ? reponse.getReclamation().getId() : null);
            ps.setString(2, reponse.getMessage());
            ps.setTimestamp(3, new java.sql.Timestamp(reponse.getDateReponse().getTime()));
            ps.setInt(4, reponse.getId());
            ps.executeUpdate();
            System.out.println("Réponse à réclamation modifiée");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(ReponseReclamation reponse) {
        String req = "DELETE FROM `reponse_reclamation` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, reponse.getId());
            ps.executeUpdate();
            System.out.println("Réponse à réclamation supprimée");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        String req = "DELETE FROM `reponse_reclamation` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Réponse à réclamation supprimée");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<ReponseReclamation> rechercher() {
        String req = "SELECT * FROM `reponse_reclamation`";
        List<ReponseReclamation> reponses = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ReponseReclamation reponse = new ReponseReclamation(
                        rs.getInt("id"),
                        null,
                        rs.getString("message"),
                        rs.getTimestamp("date_reponse")
                );
                reponses.add(reponse);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération : " + e.getMessage());
        }
        return reponses;
    }
}