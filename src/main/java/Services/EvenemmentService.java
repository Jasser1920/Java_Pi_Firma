package Services;

import Models.Evenemment;
import Utils.MyDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EvenemmentService implements IService<Evenemment> {
    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Evenemment evenement) {
        String req = "INSERT INTO `evenemment` (`utilisateur_id`, `titre`, `desecription`, `date`, `lieux`, `image`) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, evenement.getUtilisateur() != null ? evenement.getUtilisateur().getId() : null);
            ps.setString(2, evenement.getTitre());
            ps.setString(3, evenement.getDesecription());
            ps.setDate(4, new java.sql.Date(evenement.getDate().getTime()));
            ps.setString(5, evenement.getLieux());
            ps.setString(6, evenement.getImage());
            ps.executeUpdate();
            System.out.println("Événement ajouté");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Evenemment evenement) {
        String req = "UPDATE `evenemment` SET `utilisateur_id`=?, `titre`=?, `desecription`=?, `date`=?, `lieux`=?, `image`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setObject(1, evenement.getUtilisateur() != null ? evenement.getUtilisateur().getId() : null);
            ps.setString(2, evenement.getTitre());
            ps.setString(3, evenement.getDesecription());
            ps.setDate(4, new java.sql.Date(evenement.getDate().getTime()));
            ps.setString(5, evenement.getLieux());
            ps.setString(6, evenement.getImage());
            ps.setInt(7, evenement.getId());
            ps.executeUpdate();
            System.out.println("Événement modifié");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Evenemment evenement) {
        String req = "DELETE FROM `evenemment` WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, evenement.getId());
            ps.executeUpdate();
            System.out.println("Événement supprimé");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Evenemment> rechercher() {
        String req = "SELECT * FROM `evenemment`";
        List<Evenemment> evenements = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Evenemment evenement = new Evenemment(
                        rs.getInt("id"), null, // Utilisateur set to null
                        rs.getString("titre"), rs.getString("desecription"), rs.getDate("date"),
                        rs.getString("lieux"), rs.getString("image")
                );
                evenements.add(evenement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    public List<Evenemment> afficher() {
        List<Evenemment> evenements = new ArrayList<>();

        String sql = "SELECT * FROM evenemment";

        try (Statement ste = connection.createStatement();
             ResultSet rs = ste.executeQuery(sql)) {

            while (rs.next()) {
                Evenemment e = new Evenemment(
                        rs.getInt("id"),
                        null, // utilisateur non chargé ici
                        rs.getString("titre"),
                        rs.getString("desecription"), // attention : champ mal orthographié en BDD
                        rs.getDate("date"),
                        rs.getString("lieux"),
                        rs.getString("image")
                );

                evenements.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de l'affichage des événements : " + ex.getMessage());
        }

        return evenements;
    }






}