package Services;

import Models.Produit;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements IService<Produit> {

    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Produit produit) {
        String req = "INSERT INTO `produit` (`categorie_id`, `utilisateur_id`, `nom`, `prix`, `description`, `image`, `quantite`, `date_expiration`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, produit.getCategorie() != null ? produit.getCategorie().getId() : null);
            ps.setInt(2, produit.getUtilisateur() != null ? produit.getUtilisateur().getId() : null);
            ps.setString(3, produit.getNom());
            ps.setDouble(4, produit.getPrix());
            ps.setString(5, produit.getDescription());
            ps.setString(6, produit.getImage());
            ps.setString(7, produit.getQuantite());
            ps.setDate(8, new java.sql.Date(produit.getDateExpiration().getTime()));
            ps.executeUpdate();
            System.out.println("Produit ajouté");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Produit produit) {
        String req = "UPDATE `produit` SET `categorie_id`=?, `utilisateur_id`=?, `nom`=?, `prix`=?, `description`=?, `image`=?, `quantite`=?, `date_expiration`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, produit.getCategorie() != null ? produit.getCategorie().getId() : null);
            ps.setInt(2, produit.getUtilisateur() != null ? produit.getUtilisateur().getId() : null);
            ps.setString(3, produit.getNom());
            ps.setDouble(4, produit.getPrix());
            ps.setString(5, produit.getDescription());
            ps.setString(6, produit.getImage());
            ps.setString(7, produit.getQuantite());
            ps.setDate(8, new java.sql.Date(produit.getDateExpiration().getTime()));
            ps.setInt(9, produit.getId());
            ps.executeUpdate();
            System.out.println("Produit modifié");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Produit produit) {
        String req = "DELETE FROM `produit` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, produit.getId());
            ps.executeUpdate();
            System.out.println("Produit supprimé");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Produit> rechercher() {
        String req = "SELECT * FROM `produit`";
        List<Produit> produits = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Note: This assumes you fetch related Categorie and Utilisateur separately if needed
                Produit produit = new Produit(
                        rs.getInt("id"),
                        null, // Categorie (fetch separately if needed)
                        null, // Utilisateur (fetch separately if needed)
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getString("quantite"),
                        rs.getDate("date_expiration")
                );
                produits.add(produit);
            }
            System.out.println("Produits récupérés: " + produits.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
}