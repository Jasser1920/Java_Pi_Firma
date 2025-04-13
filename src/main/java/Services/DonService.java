package Services;

import Models.Don;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonService implements IService<Don> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Don don) {
        String req = "INSERT INTO `don` (`evenement_id`, `dons_user_id`, `donateur`, `description`, `date`) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, don.getEvenement() != null ? don.getEvenement().getId() : null);
            ps.setObject(2, don.getDonsUser() != null ? don.getDonsUser().getId() : null);
            ps.setString(3, don.getDonateur());
            ps.setString(4, don.getDescription());
            ps.setDate(5, new java.sql.Date(don.getDate().getTime()));
            ps.executeUpdate();
            System.out.println("Don ajouté");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Don don) {
        String req = "UPDATE `don` SET `evenement_id`=?, `dons_user_id`=?, `donateur`=?, `description`=?, `date`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, don.getEvenement() != null ? don.getEvenement().getId() : null);
            ps.setObject(2, don.getDonsUser() != null ? don.getDonsUser().getId() : null);
            ps.setString(3, don.getDonateur());
            ps.setString(4, don.getDescription());
            ps.setDate(5, new java.sql.Date(don.getDate().getTime()));
            ps.setInt(6, don.getId());
            ps.executeUpdate();
            System.out.println("Don modifié");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Don don) {
        String req = "DELETE FROM `don` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, don.getId());
            ps.executeUpdate();
            System.out.println("Don supprimé");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Don> rechercher() {
        String req = "SELECT * FROM `don`";
        List<Don> dons = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Don don = new Don(
                        rs.getInt("id"), null, null, // Evenement and DonsUser set to null
                        rs.getString("donateur"), rs.getString("description"), rs.getDate("date")
                );
                dons.add(don);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dons;
    }
}