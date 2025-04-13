package Services;

import Models.Utilisateur;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IService<Utilisateur> {

    private Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Utilisateur utilisateur) throws SQLException {
        String req = "INSERT INTO `utilisateur` (`nom`, `prenom`, `email`, `motdepasse`, `telephone`, `adresse`, `role`, `profile_picture`, `blocked`, `is_verified`, `confirmation_code`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, utilisateur.getNom());
        ps.setString(2, utilisateur.getPrenom());
        ps.setString(3, utilisateur.getEmail());
        ps.setString(4, utilisateur.getMotdepasse());
        ps.setString(5, utilisateur.getTelephone());
        ps.setString(6, utilisateur.getAdresse());
        ps.setString(7, utilisateur.getRole());
        ps.setString(8, utilisateur.getProfilePicture());
        ps.setBoolean(9, utilisateur.isBlocked());
        ps.setBoolean(10, utilisateur.isVerified());
        ps.setString(11, utilisateur.getConfirmationCode());
        ps.executeUpdate();
        System.out.println("Utilisateur ajouté");

    }

    @Override
    public void modifier(Utilisateur utilisateur)  throws SQLException {
        String req = "UPDATE `utilisateur` SET `nom`=?, `prenom`=?, `email`=?, `motdepasse`=?, `telephone`=?, `adresse`=?, `role`=?, `profile_picture`=?, `blocked`=?, `is_verified`=?, `confirmation_code`=? WHERE `id`=?";

        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, utilisateur.getNom());
        ps.setString(2, utilisateur.getPrenom());
        ps.setString(3, utilisateur.getEmail());
        ps.setString(4, utilisateur.getMotdepasse());
        ps.setString(5, utilisateur.getTelephone());
        ps.setString(6, utilisateur.getAdresse());
        ps.setString(7, utilisateur.getRole());
        ps.setString(8, utilisateur.getProfilePicture());
        ps.setBoolean(9, utilisateur.isBlocked());
        ps.setBoolean(10, utilisateur.isVerified());
        ps.setString(11, utilisateur.getConfirmationCode());
        ps.setInt(12, utilisateur.getId());
        ps.executeUpdate();
        System.out.println("Utilisateur modifié");

    }

    @Override
    public void supprimer(Utilisateur utilisateur)  throws SQLException {
        String req = "DELETE FROM `utilisateur` WHERE `id`=?";

        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, utilisateur.getId());
        ps.executeUpdate();
        System.out.println("Utilisateur supprimé");

    }

    @Override
    public List<Utilisateur> rechercher()  throws SQLException {
        String req = "SELECT * FROM `utilisateur`";
        List<Utilisateur> utilisateurs = new ArrayList<>();

        PreparedStatement ps = connection.prepareStatement(req);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Utilisateur utilisateur = new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("motdepasse"),
                    rs.getString("telephone"),
                    rs.getString("adresse"),
                    rs.getString("role"),
                    rs.getString("profile_picture"),
                    rs.getBoolean("blocked"),
                    rs.getBoolean("is_verified"),
                    rs.getString("confirmation_code")
            );
            utilisateurs.add(utilisateur);
        }
        System.out.println("Utilisateurs récupérés: " + utilisateurs.size());

        return utilisateurs;
    }
}