package Services;

import Models.Reclammation;
import Models.ReponseReclamation;
import Models.Statut;
import Models.Utilisateur;
import Utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclammationService implements IService<Reclammation> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Reclammation reclammation) {
        String req = "INSERT INTO `reclammation` (`utilisateur_id`, `titre`, `description`, `date_creation`, `statut`) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, reclammation.getUtilisateur() != null ? reclammation.getUtilisateur().getId() : null);
            ps.setString(2, reclammation.getTitre());
            ps.setString(3, reclammation.getDescription());
            ps.setTimestamp(4, new java.sql.Timestamp(reclammation.getDateCreation().getTime()));
            ps.setString(5, reclammation.getStatut().name());
            ps.executeUpdate();
            System.out.println("Réclamation ajoutée");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Reclammation reclammation) {
        String req = "UPDATE `reclammation` SET `titre`=?, `description`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, reclammation.getTitre());
            ps.setString(2, reclammation.getDescription());
            ps.setInt(3, reclammation.getId());
            ps.executeUpdate();
            System.out.println("Réclamation modifiée");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Reclammation reclammation) {
        supprimer(reclammation.getId());
    }

    public void supprimer(int id) {
        String deleteReponseQuery = "DELETE FROM `reponse_reclamation` WHERE `reclamation_id`=?";
        String deleteReclamationQuery = "DELETE FROM `reclammation` WHERE `id`=?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement psReponse = connection.prepareStatement(deleteReponseQuery)) {
                psReponse.setInt(1, id);
                psReponse.executeUpdate();
            }
            try (PreparedStatement psReclamation = connection.prepareStatement(deleteReclamationQuery)) {
                psReclamation.setInt(1, id);
                psReclamation.executeUpdate();
            }
            connection.commit();
            System.out.println("Réclamation supprimée");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Erreur lors du rollback : " + rollbackEx.getMessage());
            }
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la réinitialisation de l'auto-commit : " + e.getMessage());
            }
        }
    }

    @Override
    public List<Reclammation> rechercher() {
        String req = "SELECT r.*, rp.id AS reponse_id, rp.message AS reponse_message, rp.date_reponse " +
                "FROM `reclammation` r " +
                "LEFT JOIN `reponse_reclamation` rp ON r.id = rp.reclamation_id";
        List<Reclammation> reclammations = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reclammation reclammation = new Reclammation(
                        rs.getInt("id"),
                        null,
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getTimestamp("date_creation"),
                        rs.getString("statut")
                );
                if (rs.getObject("reponse_id") != null) {
                    ReponseReclamation reponse = new ReponseReclamation(
                            rs.getInt("reponse_id"),
                            reclammation,
                            rs.getString("reponse_message"),
                            rs.getTimestamp("date_reponse")
                    );
                    reclammation.setReponse(reponse);
                }
                reclammations.add(reclammation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération : " + e.getMessage());
        }
        return reclammations;
    }
}