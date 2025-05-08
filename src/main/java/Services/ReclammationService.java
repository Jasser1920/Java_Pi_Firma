package Services;

import Models.Reclammation;
import Models.ReponseReclamation;
import Models.Statut;
import Models.Utilisateur;
import Utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReclammationService implements IService<Reclammation> {
    private static final Logger LOGGER = Logger.getLogger(ReclammationService.class.getName());
    private final Connection connection = MyDatabase.getInstance().getConnection();

    @Override
    public void ajouter(Reclammation reclammation) {
        String req = "INSERT INTO `reclammation` (`utilisateur_id`, `titre`, `description`, `date_creation`, `statut`) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, reclammation.getUtilisateur() != null ? reclammation.getUtilisateur().getId() : null);
            ps.setString(2, reclammation.getTitre());
            ps.setString(3, reclammation.getDescription());
            ps.setTimestamp(4, new java.sql.Timestamp(reclammation.getDateCreation().getTime()));
            ps.setString(5, reclammation.getStatut().getValue());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                reclammation.setId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de la réclamation", ex);
            throw new RuntimeException("Erreur lors de l'ajout de la réclamation", ex);
        }
    }

    @Override
    public void modifier(Reclammation reclammation) {
        String req = "UPDATE `reclammation` SET `titre` = ?, `description` = ?, `statut` = ? WHERE `id` = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, reclammation.getTitre());
            ps.setString(2, reclammation.getDescription());
            ps.setString(3, reclammation.getStatut().getValue());
            ps.setInt(4, reclammation.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification de la réclamation", ex);
            throw new RuntimeException("Erreur lors de la modification de la réclamation", ex);
        }
    }

    @Override
    public void supprimer(Reclammation reclammation) {
        supprimer(reclammation.getId());
    }

    public void supprimer(int id) {
        String deleteReponseQuery = "DELETE FROM `reponse_reclamation` WHERE `reclamation_id`=?";
        String deleteReclamationQuery = "DELETE FROM `reclammation` WHERE `id`=?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement psReponse = connection.prepareStatement(deleteReponseQuery)) {
                psReponse.setInt(1, id);
                psReponse.executeUpdate();
            }
            try (PreparedStatement psReclamation = connection.prepareStatement(deleteReclamationQuery)) {
                psReclamation.setInt(1, id);
                psReclamation.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Erreur lors du rollback", rollbackEx);
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la réclamation", e);
            throw new RuntimeException("Erreur lors de la suppression de la réclamation", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la réinitialisation de l'auto-commit", e);
            }
        }
    }

    @Override
    public List<Reclammation> rechercher() {
        String req = "SELECT r.*, rp.id AS reponse_id, rp.message AS reponse_message, rp.date_reponse, " +
                "u.id AS user_id, u.email AS user_email " +
                "FROM `reclammation` r " +
                "LEFT JOIN `reponse_reclamation` rp ON r.id = rp.reclamation_id " +
                "LEFT JOIN `utilisateur` u ON r.utilisateur_id = u.id";
        List<Reclammation> reclammations = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reclammation r = new Reclammation();
                r.setId(rs.getInt("id"));
                r.setTitre(rs.getString("titre"));
                r.setDescription(rs.getString("description"));
                r.setDateCreation(rs.getTimestamp("date_creation"));
                r.setStatut(Statut.fromString(rs.getString("statut")));

                // Créer l'utilisateur
                if (rs.getObject("user_id") != null) {
                    try {
                        UtilisateurService utilisateurService = new UtilisateurService();
                        Utilisateur u = utilisateurService.findById(rs.getInt("user_id"));
                        if (u != null) {
                            r.setUtilisateur(u);
                        } else {
                            System.err.println("[ERROR] Utilisateur introuvable pour user_id: " + rs.getInt("user_id"));
                        }
                    } catch (Exception e) {
                        System.err.println("[ERROR] Could not load full user for reclamation: " + e.getMessage());
                    }
                }

                // Créer la réponse si elle existe
                if (rs.getObject("reponse_id") != null) {
                    ReponseReclamation reponse = new ReponseReclamation();
                    reponse.setId(rs.getInt("reponse_id"));
                    reponse.setMessage(rs.getString("reponse_message"));
                    reponse.setDateReponse(rs.getTimestamp("date_reponse"));
                    reponse.setReclamation(r);
                    r.setReponse(reponse);
                }

                reclammations.add(r);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des réclamations", ex);
            throw new RuntimeException("Erreur lors de la recherche des réclamations", ex);
        }
        return reclammations;
    }

    public List<Reclammation> rechercherParUtilisateur(int userId) {
        String req = "SELECT r.*, rp.id AS reponse_id, rp.message AS reponse_message, rp.date_reponse, " +
                "u.id AS user_id, u.email AS user_email " +
                "FROM `reclammation` r " +
                "LEFT JOIN `reponse_reclamation` rp ON r.id = rp.reclamation_id " +
                "LEFT JOIN `utilisateur` u ON r.utilisateur_id = u.id " +
                "WHERE r.utilisateur_id = ?";
        List<Reclammation> reclammations = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reclammation r = new Reclammation();
                r.setId(rs.getInt("id"));
                r.setTitre(rs.getString("titre"));
                r.setDescription(rs.getString("description"));
                r.setDateCreation(rs.getTimestamp("date_creation"));
                r.setStatut(Statut.fromString(rs.getString("statut")));

                // Créer l'utilisateur
                if (rs.getObject("user_id") != null) {
                    try {
                        UtilisateurService utilisateurService = new UtilisateurService();
                        Utilisateur u = utilisateurService.findById(rs.getInt("user_id"));
                        if (u != null) {
                            r.setUtilisateur(u);
                        } else {
                            System.err.println("[ERROR] Utilisateur introuvable pour user_id: " + rs.getInt("user_id"));
                        }
                    } catch (Exception e) {
                        System.err.println("[ERROR] Could not load full user for reclamation: " + e.getMessage());
                    }
                }

                // Créer la réponse si elle existe
                if (rs.getObject("reponse_id") != null) {
                    ReponseReclamation reponse = new ReponseReclamation();
                    reponse.setId(rs.getInt("reponse_id"));
                    reponse.setMessage(rs.getString("reponse_message"));
                    reponse.setDateReponse(rs.getTimestamp("date_reponse"));
                    reponse.setReclamation(r);
                    r.setReponse(reponse);
                }

                reclammations.add(r);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des réclamations de l'utilisateur", ex);
            throw new RuntimeException("Erreur lors de la recherche des réclamations de l'utilisateur", ex);
        }
        return reclammations;
    }

    public void ajouterReponse(ReponseReclamation reponse, Statut statut) {
        LOGGER.info("Début de l'ajout d'une réponse à la réclamation ID: " + reponse.getReclamation().getId());
        String req = "INSERT INTO `reponse_reclamation` (`reclamation_id`, `message`, `date_reponse`) VALUES (?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            try {
                // 1. Ajouter la réponse
                LOGGER.info("Ajout de la réponse dans la base de données...");
                PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, reponse.getReclamation().getId());
                ps.setString(2, reponse.getMessage());
                ps.setTimestamp(3, new java.sql.Timestamp(reponse.getDateReponse().getTime()));
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    reponse.setId(rs.getInt(1));
                    LOGGER.info("Réponse ajoutée avec l'ID: " + reponse.getId());
                }

                // 2. Mettre à jour le statut de la réclamation
                LOGGER.info("Mise à jour du statut de la réclamation à: " + statut.getValue());
                String updateReq = "UPDATE `reclammation` SET `statut` = ? WHERE `id` = ?";
                PreparedStatement updatePs = connection.prepareStatement(updateReq);
                updatePs.setString(1, statut.getValue());
                updatePs.setInt(2, reponse.getReclamation().getId());
                updatePs.executeUpdate();

                // 3. Récupérer les informations de l'utilisateur
                LOGGER.info("Récupération des informations de l'utilisateur...");
                String userQuery = "SELECT u.telephone, u.nom, u.prenom FROM utilisateur u " +
                        "INNER JOIN reclammation r ON r.utilisateur_id = u.id " +
                        "WHERE r.id = ?";
                PreparedStatement userPs = connection.prepareStatement(userQuery);
                userPs.setInt(1, reponse.getReclamation().getId());
                ResultSet userRs = userPs.executeQuery();

                if (userRs.next()) {
                    String telephone = userRs.getString("telephone");
                    String nomComplet = userRs.getString("prenom") + " " + userRs.getString("nom");
                    LOGGER.info("Informations utilisateur trouvées - Nom: " + nomComplet + ", Téléphone: " + telephone);

                    // 4. Envoyer le message WhatsApp
                    if (telephone != null && !telephone.isEmpty()) {
                        LOGGER.info("Préparation de l'envoi du message WhatsApp...");
                        String statusMessage = statut == Statut.RESOLUE ?
                                "résolue" :
                                "rejetée";

                        // Envoi du message WhatsApp via l'API Business
                        Utils.WhatsAppBusinessService.sendReclamationStatus(
                                telephone,
                                statusMessage,
                                reponse.getMessage()
                        );
                    } else {
                        LOGGER.warning("Numéro de téléphone non trouvé ou vide pour l'utilisateur: " + nomComplet);
                    }
                } else {
                    LOGGER.warning("Aucune information utilisateur trouvée pour la réclamation ID: " + reponse.getReclamation().getId());
                }

                connection.commit();
                LOGGER.info("Transaction terminée avec succès");
            } catch (Exception e) {
                connection.rollback();
                LOGGER.severe("Erreur lors de l'ajout de la réponse: " + e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de la réponse", ex);
            throw new RuntimeException("Erreur lors de l'ajout de la réponse", ex);
        }
    }

    // Surcharge de la méthode pour la compatibilité avec l'ancien code
    public void ajouterReponse(ReponseReclamation reponse) {
        ajouterReponse(reponse, Statut.RESOLUE); // Par défaut, on considère que c'est résolu
    }
}