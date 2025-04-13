package Services;

import Models.Location;
import Models.Terrain;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationService implements IService<Location> {
    private Connection connection;

    public LocationService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Location location) throws SQLException {
        String req = "INSERT INTO location (terrain_id, date_debut, date_fin, prix_total, mode_paiement) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, location.getTerrain().getId());
            ps.setDate(2, new java.sql.Date(location.getDateDebut().getTime()));
            ps.setDate(3, new java.sql.Date(location.getDateFin().getTime()));
            ps.setDouble(4, location.getPrixTotal());
            ps.setString(5, location.getModePaiement());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    location.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void modifier(Location location) throws SQLException {
        String req = "UPDATE location SET terrain_id=?, date_debut=?, date_fin=?, prix_total=?, mode_paiement=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, location.getTerrain().getId());
            ps.setDate(2, new java.sql.Date(location.getDateDebut().getTime()));
            ps.setDate(3, new java.sql.Date(location.getDateFin().getTime()));
            ps.setDouble(4, location.getPrixTotal());
            ps.setString(5, location.getModePaiement());
            ps.setInt(6, location.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(Location location) throws SQLException {
        String req = "DELETE FROM location WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, location.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Location> rechercher() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String req = "SELECT * FROM location";
        try (PreparedStatement ps = connection.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Terrain terrain = new TerrainService().rechercherParId(rs.getInt("terrain_id"));
                Location location = new Location(
                        rs.getInt("id"),
                        terrain,
                        rs.getDate("date_debut"),
                        rs.getDate("date_fin"),
                        rs.getDouble("prix_total"),
                        rs.getString("mode_paiement")
                );
                locations.add(location);
            }
        }
        return locations;
    }

    public boolean isTerrainAvailable(int terrainId, Date startDate, Date endDate) throws SQLException {
        String query = "SELECT COUNT(*) FROM location WHERE terrain_id = ? AND " +
                "((date_debut BETWEEN ? AND ?) OR (date_fin BETWEEN ? AND ?) OR " +
                "(date_debut <= ? AND date_fin >= ?))";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, terrainId);
            ps.setDate(2, new java.sql.Date(startDate.getTime()));
            ps.setDate(3, new java.sql.Date(endDate.getTime()));
            ps.setDate(4, new java.sql.Date(startDate.getTime()));
            ps.setDate(5, new java.sql.Date(endDate.getTime()));
            ps.setDate(6, new java.sql.Date(startDate.getTime()));
            ps.setDate(7, new java.sql.Date(endDate.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                return !(rs.next() && rs.getInt(1) > 0);
            }
        }
    }
}