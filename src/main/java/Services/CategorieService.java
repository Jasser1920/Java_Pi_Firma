package Services;

import Models.Categorie;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements IService<Categorie> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Categorie categorie) throws SQLException {
        String req = "INSERT INTO `categorie` (`nom_categorie`) VALUES (?)";

        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, categorie.getNomCategorie());
        ps.executeUpdate();
        System.out.println("Catégorie ajoutée");

    }

    @Override
    public void modifier(Categorie categorie) throws SQLException {
        String req = "UPDATE `categorie` SET `nom_categorie`=? WHERE `id`=?";

        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, categorie.getNomCategorie());
        ps.setInt(2, categorie.getId());
        ps.executeUpdate();
        System.out.println("Catégorie modifiée");

    }

    @Override
    public void supprimer(Categorie categorie) throws SQLException {
        String req = "DELETE FROM `categorie` WHERE `id`=?";

        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, categorie.getId());
        ps.executeUpdate();
        System.out.println("Catégorie supprimée");

    }

    @Override
    public List<Categorie> rechercher()  throws SQLException{
        String req = "SELECT * FROM `categorie`";
        List<Categorie> categories = new ArrayList<>();

        PreparedStatement ps = connection.prepareStatement(req);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Categorie categorie = new Categorie(rs.getInt("id"), rs.getString("nom_categorie"));
            categories.add(categorie);
        }

        return categories;
    }

    public boolean existeCategorie(String nom) throws SQLException {
        String req = "SELECT COUNT(*) FROM categorie WHERE nom_categorie = ?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, nom);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
    public Categorie getCategorieLaPlusUtilisee() throws SQLException {
        String query = "SELECT c.*, COUNT(p.id) AS total_produits " +
                "FROM categorie c " +
                "JOIN produit p ON c.id = p.categorie_id " +
                "GROUP BY c.id " +
                "ORDER BY total_produits DESC " +
                "LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new Categorie(rs.getInt("id"), rs.getString("nom_categorie"));

            }
        }

        return null;
    }




}