package Services;

import Models.Terrain;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TerrainService implements IService<Terrain> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Terrain terrain) {
        String req = "INSERT INTO `terrain` (`utilisateur_id`, `description`, `superficie`, `latitude`, `longitude`, `prix_location`, `disponibilite`, `date_creation`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, terrain.getUtilisateur() != null ? terrain.getUtilisateur().getId() : null);
            ps.setString(2, terrain.getDescription());
            ps.setDouble(3, terrain.getSuperficie());
            ps.setObject(4, terrain.getLatitude());
            ps.setObject(5, terrain.getLongitude());
            ps.setDouble(6, terrain.getPrixLocation());
            ps.setBoolean(7, terrain.isDisponibilite());
            ps.setTimestamp(8, new java.sql.Timestamp(terrain.getDateCreation().getTime()));
            ps.executeUpdate();
            System.out.println("Terrain ajouté");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Terrain terrain) {
        String req = "UPDATE `terrain` SET `utilisateur_id`=?, `description`=?, `superficie`=?, `latitude`=?, `longitude`=?, `prix_location`=?, `disponibilite`=?, `date_creation`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, terrain.getUtilisateur() != null ? terrain.getUtilisateur().getId() : null);
            ps.setString(2, terrain.getDescription());
            ps.setDouble(3, terrain.getSuperficie());
            ps.setObject(4, terrain.getLatitude());
            ps.setObject(5, terrain.getLongitude());
            ps.setDouble(6, terrain.getPrixLocation());
            ps.setBoolean(7, terrain.isDisponibilite());
            ps.setTimestamp(8, new java.sql.Timestamp(terrain.getDateCreation().getTime()));
            ps.setInt(9, terrain.getId());
            ps.executeUpdate();
            System.out.println("Terrain modifié");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Terrain terrain) {
        String req = "DELETE FROM `terrain` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, terrain.getId());
            ps.executeUpdate();
            System.out.println("Terrain supprimé");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Terrain> rechercher() {
        String req = "SELECT * FROM `terrain`";
        List<Terrain> terrains = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Terrain terrain = new Terrain(
                        rs.getInt("id"), null, // Utilisateur set to null
                        rs.getString("description"), rs.getDouble("superficie"),
                        rs.getObject("latitude") != null ? rs.getDouble("latitude") : null,
                        rs.getObject("longitude") != null ? rs.getDouble("longitude") : null,
                        rs.getDouble("prix_location"), rs.getBoolean("disponibilite"),
                        rs.getTimestamp("date_creation")
                );
                terrains.add(terrain);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return terrains;
    }
}