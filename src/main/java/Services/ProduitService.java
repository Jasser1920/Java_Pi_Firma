package Services;

import Models.Categorie;
import Models.Produit;
import Utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements IService<Produit> {

    private final Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Produit produit) throws SQLException {
        String req = "INSERT INTO `produit` (`categorie_id`, `utilisateur_id`, `nom`, `prix`, `description`, `image`, `quantite`, `date_expiration`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setObject(1, produit.getCategorie() != null ? produit.getCategorie().getId() : null, java.sql.Types.INTEGER);
            ps.setObject(2, produit.getUtilisateur() != null ? produit.getUtilisateur().getId() : null, java.sql.Types.INTEGER);
            ps.setString(3, produit.getNom());
            ps.setDouble(4, produit.getPrix());
            ps.setString(5, produit.getDescription());
            ps.setString(6, produit.getImage());
            ps.setInt(7, produit.getQuantite());
            ps.setDate(8, produit.getDateExpiration());
            ps.executeUpdate();
            System.out.println("Produit ajouté");
        }
    }

    @Override
    public void modifier(Produit produit) throws SQLException {
        String req = "UPDATE `produit` SET `categorie_id`=?, `utilisateur_id`=?, `nom`=?, `prix`=?, `description`=?, `image`=?, `quantite`=?, `date_expiration`=? WHERE `id`=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setObject(1, produit.getCategorie() != null ? produit.getCategorie().getId() : null, java.sql.Types.INTEGER);
            ps.setObject(2, produit.getUtilisateur() != null ? produit.getUtilisateur().getId() : null, java.sql.Types.INTEGER);
            ps.setString(3, produit.getNom());
            ps.setDouble(4, produit.getPrix());
            ps.setString(5, produit.getDescription());
            ps.setString(6, produit.getImage());
            ps.setInt(7, produit.getQuantite());
            ps.setDate(8, produit.getDateExpiration());
            ps.setInt(9, produit.getId());
            ps.executeUpdate();
            System.out.println("Produit modifié avec succès");
        }
    }

    @Override
    public void supprimer(Produit produit) throws SQLException {
        String req = "DELETE FROM `produit` WHERE `id`=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, produit.getId());
            ps.executeUpdate();
            System.out.println("Produit supprimé");
        }
    }

    @Override
    public List<Produit> rechercher() throws SQLException {
        String req = "SELECT p.*, c.nom_categorie FROM produit p LEFT JOIN categorie c ON p.categorie_id = c.id";
        List<Produit> produits = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Categorie categorie = new Categorie(
                        rs.getInt("categorie_id"),
                        rs.getString("nom_categorie")
                );

                Produit produit = new Produit(
                        rs.getInt("id"),
                        categorie,
                        null, // utilisateur (à gérer plus tard)
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getInt("quantite"),
                        rs.getDate("date_expiration")
                );
                produits.add(produit);
            }
        }

        return produits;
    }

    public int countByCategorie(int categorieId) {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) FROM produit WHERE categorie_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, categorieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int sumQuantiteByCategorie(int categorieId) {
        int sum = 0;
        String sql = "SELECT SUM(quantite) FROM produit WHERE categorie_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categorieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sum = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sum;
    }

    public double avgPrixByCategorie(int categorieId) {
        double moyenne = 0;
        String sql = "SELECT AVG(prix) FROM produit WHERE categorie_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categorieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                moyenne = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return moyenne;
    }

    public List<Produit> afficher() {
        try {
            return rechercher(); // ta méthode déjà existante
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }




}