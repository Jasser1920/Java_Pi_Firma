package Services;

import Models.Location;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationService implements IService<Location> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Location location) {
        String req = "INSERT INTO `location` (`terrain_id`, `date_debut`, `date_fin`, `prix_total`, `mode_paiement`) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, location.getTerrain() != null ? location.getTerrain().getId() : null);
            ps.setDate(2, new java.sql.Date(location.getDateDebut().getTime()));
            ps.setDate(3, new java.sql.Date(location.getDateFin().getTime()));
            ps.setDouble(4, location.getPrixTotal());
            ps.setString(5, location.getModePaiement());
            ps.executeUpdate();
            System.out.println("Location ajoutée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Location location) {
        String req = "UPDATE `location` SET `terrain_id`=?, `date_debut`=?, `date_fin`=?, `prix_total`=?, `mode_paiement`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, location.getTerrain() != null ? location.getTerrain().getId() : null);
            ps.setDate(2, new java.sql.Date(location.getDateDebut().getTime()));
            ps.setDate(3, new java.sql.Date(location.getDateFin().getTime()));
            ps.setDouble(4, location.getPrixTotal());
            ps.setString(5, location.getModePaiement());
            ps.setInt(6, location.getId());
            ps.executeUpdate();
            System.out.println("Location modifiée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Location location) {
        String req = "DELETE FROM `location` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, location.getId());
            ps.executeUpdate();
            System.out.println("Location supprimée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Location> rechercher() {
        String req = "SELECT * FROM `location`";
        List<Location> locations = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Location location = new Location(
                        rs.getInt("id"), null, // Terrain set to null
                        rs.getDate("date_debut"), rs.getDate("date_fin"),
                        rs.getDouble("prix_total"), rs.getString("mode_paiement")
                );
                locations.add(location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }
}