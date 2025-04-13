package Services;

import Models.Reclammation;
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
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, reclammation.getUtilisateur() != null ? reclammation.getUtilisateur().getId() : null);
            ps.setString(2, reclammation.getTitre());
            ps.setString(3, reclammation.getDescription());
            ps.setTimestamp(4, new java.sql.Timestamp(reclammation.getDateCreation().getTime()));
            ps.setString(5, reclammation.getStatut());
            ps.executeUpdate();
            System.out.println("Réclamation ajoutée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Reclammation reclammation) {
        String req = "UPDATE `reclammation` SET `utilisateur_id`=?, `titre`=?, `description`=?, `date_creation`=?, `statut`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, reclammation.getUtilisateur() != null ? reclammation.getUtilisateur().getId() : null);
            ps.setString(2, reclammation.getTitre());
            ps.setString(3, reclammation.getDescription());
            ps.setTimestamp(4, new java.sql.Timestamp(reclammation.getDateCreation().getTime()));
            ps.setString(5, reclammation.getStatut());
            ps.setInt(6, reclammation.getId());
            ps.executeUpdate();
            System.out.println("Réclamation modifiée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Reclammation reclammation) {
        String req = "DELETE FROM `reclammation` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, reclammation.getId());
            ps.executeUpdate();
            System.out.println("Réclamation supprimée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Reclammation> rechercher() {
        String req = "SELECT * FROM `reclammation`";
        List<Reclammation> reclammations = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reclammation reclammation = new Reclammation(
                        rs.getInt("id"), null, // Utilisateur set to null
                        rs.getString("titre"), rs.getString("description"),
                        rs.getTimestamp("date_creation"), rs.getString("statut")
                );
                reclammations.add(reclammation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reclammations;
    }
}