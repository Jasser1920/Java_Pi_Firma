package Services;

import Models.Categorie;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements IService<Categorie> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Categorie categorie) {
        String req = "INSERT INTO `categorie` (`nom_categorie`) VALUES (?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, categorie.getNomCategorie());
            ps.executeUpdate();
            System.out.println("Catégorie ajoutée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Categorie categorie) {
        String req = "UPDATE `categorie` SET `nom_categorie`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, categorie.getNomCategorie());
            ps.setInt(2, categorie.getId());
            ps.executeUpdate();
            System.out.println("Catégorie modifiée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Categorie categorie) {
        String req = "DELETE FROM `categorie` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, categorie.getId());
            ps.executeUpdate();
            System.out.println("Catégorie supprimée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Categorie> rechercher() {
        String req = "SELECT * FROM `categorie`";
        List<Categorie> categories = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Categorie categorie = new Categorie(rs.getInt("id"), rs.getString("nom_categorie"));
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}