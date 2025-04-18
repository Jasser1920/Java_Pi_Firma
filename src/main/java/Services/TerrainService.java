package Services;

import Models.Terrain;
import Models.Utilisateur;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TerrainService implements IService<Terrain> {
    private Connection connection;

    public TerrainService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Terrain terrain) throws SQLException {
        String req = "INSERT INTO terrain (utilisateur_id, description, superficie, latitude, longitude, prix_location, disponibilite, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, terrain.getUtilisateur() != null ? terrain.getUtilisateur().getId() : null);
            ps.setString(2, terrain.getDescription());
            ps.setDouble(3, terrain.getSuperficie());
            ps.setObject(4, terrain.getLatitude());
            ps.setObject(5, terrain.getLongitude());
            ps.setDouble(6, terrain.getPrixLocation());
            ps.setBoolean(7, terrain.isDisponibilite());
            ps.setTimestamp(8, new java.sql.Timestamp(terrain.getDateCreation().getTime()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    terrain.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void modifier(Terrain terrain) throws SQLException {
        String req = "UPDATE terrain SET utilisateur_id=?, description=?, superficie=?, latitude=?, longitude=?, prix_location=?, disponibilite=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setObject(1, terrain.getUtilisateur() != null ? terrain.getUtilisateur().getId() : null);
            ps.setString(2, terrain.getDescription());
            ps.setDouble(3, terrain.getSuperficie());
            ps.setObject(4, terrain.getLatitude());
            ps.setObject(5, terrain.getLongitude());
            ps.setDouble(6, terrain.getPrixLocation());
            ps.setBoolean(7, terrain.isDisponibilite());
            ps.setInt(8, terrain.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(Terrain terrain) throws SQLException {
        // First delete related locations
        String req = "DELETE FROM location WHERE terrain_id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, terrain.getId());
            ps.executeUpdate();
        }

        // Then delete the terrain
        req = "DELETE FROM terrain WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, terrain.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Terrain> rechercher() throws SQLException {
        List<Terrain> terrains = new ArrayList<>();
        String req = "SELECT * FROM terrain";
        try (PreparedStatement ps = connection.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Terrain terrain = new Terrain(
                        rs.getInt("id"),
                        null, // User not loaded by default
                        rs.getString("description"),
                        rs.getDouble("superficie"),
                        rs.getObject("latitude") != null ? rs.getDouble("latitude") : null,
                        rs.getObject("longitude") != null ? rs.getDouble("longitude") : null,
                        rs.getDouble("prix_location"),
                        rs.getBoolean("disponibilite"),
                        rs.getTimestamp("date_creation")
                );
                terrains.add(terrain);
            }
        }
        return terrains;
    }

    public Terrain rechercherParId(int id) throws SQLException {
        String req = "SELECT * FROM terrain WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Terrain(
                            rs.getInt("id"),
                            null, // User not loaded by default
                            rs.getString("description"),
                            rs.getDouble("superficie"),
                            rs.getObject("latitude") != null ? rs.getDouble("latitude") : null,
                            rs.getObject("longitude") != null ? rs.getDouble("longitude") : null,
                            rs.getDouble("prix_location"),
                            rs.getBoolean("disponibilite"),
                            rs.getTimestamp("date_creation")
                    );
                }
            }
        }
        return null;
    }
}