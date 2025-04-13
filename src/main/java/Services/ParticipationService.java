package Services;

import Models.Participation;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationService implements IService<Participation> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Participation participation) {
        String req = "INSERT INTO `participation` (`user_id`, `evenement_id`, `participation_date`) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, participation.getUser().getId());
            ps.setInt(2, participation.getEvenement().getId());
            ps.setTimestamp(3, new java.sql.Timestamp(participation.getParticipationDate().getTime()));
            ps.executeUpdate();
            System.out.println("Participation ajoutée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Participation participation) {
        String req = "UPDATE `participation` SET `user_id`=?, `evenement_id`=?, `participation_date`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, participation.getUser().getId());
            ps.setInt(2, participation.getEvenement().getId());
            ps.setTimestamp(3, new java.sql.Timestamp(participation.getParticipationDate().getTime()));
            ps.setInt(4, participation.getId());
            ps.executeUpdate();
            System.out.println("Participation modifiée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Participation participation) {
        String req = "DELETE FROM `participation` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, participation.getId());
            ps.executeUpdate();
            System.out.println("Participation supprimée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Participation> rechercher() {
        String req = "SELECT * FROM `participation`";
        List<Participation> participations = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Participation participation = new Participation(
                        rs.getInt("id"), null, null, // User and Evenement set to null
                        rs.getTimestamp("participation_date")
                );
                participations.add(participation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participations;
    }
}