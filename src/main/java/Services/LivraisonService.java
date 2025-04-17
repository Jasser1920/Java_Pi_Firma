package Services;

import Models.Livraison;
import Models.StatutLivraison;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivraisonService implements IService<Livraison> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Livraison livraison) {
        String req = "INSERT INTO `livraison` (`nom_societe`, `adresse_livraison`, `date_livraison`, `statut`) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, livraison.getNomSociete());
            ps.setString(2, livraison.getAdresseLivraison());
            ps.setDate(3, new java.sql.Date(livraison.getDateLivraison().getTime()));
            ps.setString(4, livraison.getStatut().toString());
            int rowsAffected = ps.executeUpdate();
            System.out.println("Livraison ajoutée, lignes affectées : " + rowsAffected);

            // Récupérer l'ID généré
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                livraison.setId(rs.getInt(1));
                System.out.println("ID de la livraison généré : " + livraison.getId());
            } else {
                System.err.println("Erreur : Aucun ID généré pour la livraison");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de la livraison", e);
        }
    }

    @Override
    public void modifier(Livraison livraison) {
        String req = "UPDATE `livraison` SET `nom_societe`=?, `adresse_livraison`=?, `date_livraison`=?, `statut`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, livraison.getNomSociete());
            ps.setString(2, livraison.getAdresseLivraison());
            ps.setDate(3, new java.sql.Date(livraison.getDateLivraison().getTime()));
            ps.setString(4, livraison.getStatut().toString()); // Utiliser toString() pour l'énumération
            ps.setInt(5, livraison.getId());
            ps.executeUpdate();
            System.out.println("Livraison modifiée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Livraison livraison) {
        String req = "DELETE FROM `livraison` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, livraison.getId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Livraison supprimée ID : " + livraison.getId());
            } else {
                System.out.println("Aucune livraison supprimée pour ID : " + livraison.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de la livraison ID : " + livraison.getId(), e);
        }
    }
    @Override
    public List<Livraison> rechercher() {
        String req = "SELECT * FROM `livraison`";
        List<Livraison> livraisons = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Livraison livraison = new Livraison(
                        rs.getInt("id"),
                        rs.getString("nom_societe"),
                        rs.getString("adresse_livraison"),
                        rs.getDate("date_livraison"),
                        StatutLivraison.valueOf(rs.getString("statut")) // Convertir la chaîne en énumération
                );
                livraisons.add(livraison);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livraisons;
    }
}