package Services;

import Models.Commande;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeService implements IService<Commande> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Commande commande) {
        String req = "INSERT INTO `commande` (`livraison_id`, `date_commande`, `total`, `statut`) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, commande.getLivraison() != null ? commande.getLivraison().getId() : null);
            ps.setDate(2, new java.sql.Date(commande.getDateCommande().getTime()));
            ps.setDouble(3, commande.getTotal());
            ps.setString(4, commande.getStatut());
            ps.executeUpdate();
            System.out.println("Commande ajoutée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Commande commande) {
        String req = "UPDATE `commande` SET `livraison_id`=?, `date_commande`=?, `total`=?, `statut`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, commande.getLivraison() != null ? commande.getLivraison().getId() : null);
            ps.setDate(2, new java.sql.Date(commande.getDateCommande().getTime()));
            ps.setDouble(3, commande.getTotal());
            ps.setString(4, commande.getStatut());
            ps.setInt(5, commande.getId());
            ps.executeUpdate();
            System.out.println("Commande modifiée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Commande commande) {
        String req = "DELETE FROM `commande` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, commande.getId());
            ps.executeUpdate();
            System.out.println("Commande supprimée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Commande> rechercher() {
        String req = "SELECT * FROM `commande`";
        List<Commande> commandes = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Commande commande = new Commande(
                        rs.getInt("id"), null, // Livraison set to null
                        rs.getDate("date_commande"), rs.getDouble("total"), rs.getString("statut"), null // Produits set to null
                );
                commandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
}