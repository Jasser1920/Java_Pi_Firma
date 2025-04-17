package Services;

import Models.Commande;
import Models.CommandeProduit;
import Models.Livraison;
import Models.Produit;
import Models.StatutCommande;
import Utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandeService implements IService<Commande> {
    private Connection connection = MyDatabase.getInstance().getConnection();
    private LivraisonService livraisonService = new LivraisonService();
    private ProduitService produitService = new ProduitService();

    @Override
    public void ajouter(Commande commande) {
        Livraison livraison = commande.getLivraison();
        if (livraison != null && livraison.getId() == 0) {
            System.out.println("Ajout de la livraison car elle n'a pas encore d'ID");
            livraisonService.ajouter(livraison);
        } else if (livraison != null) {
            System.out.println("Livraison déjà insérée avec ID : " + livraison.getId() + ", pas d'ajout nécessaire");
        }

        String req = "INSERT INTO `commande` (`livraison_id`, `date_commande`, `total`, `statut`) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, livraison != null ? livraison.getId() : null);
            ps.setDate(2, new java.sql.Date(commande.getDateCommande().getTime()));
            ps.setDouble(3, commande.getTotal());
            ps.setString(4, commande.getStatut().toString());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                commande.setId(rs.getInt(1));
            }

            if (commande.getProduits() != null && !commande.getProduits().isEmpty()) {
                // Regrouper les produits identiques et cumuler les quantités
                List<CommandeProduit> produitsRegroupes = new ArrayList<>();
                commande.getProduits().stream()
                        .collect(Collectors.groupingBy(
                                CommandeProduit::getProduit,
                                Collectors.summingInt(CommandeProduit::getQuantite)
                        ))
                        .forEach((produit, quantite) -> produitsRegroupes.add(new CommandeProduit(produit, quantite)));

                // Insérer une seule ligne par produit unique dans commande_produit
                String reqProduit = "INSERT INTO `commande_produit` (`commande_id`, `produit_id`) VALUES (?, ?)";
                PreparedStatement psProduit = connection.prepareStatement(reqProduit);
                for (CommandeProduit commandeProduit : produitsRegroupes) {
                    psProduit.setInt(1, commande.getId());
                    psProduit.setInt(2, commandeProduit.getProduit().getId());
                    psProduit.executeUpdate();
                }

                // Mettre à jour la liste des produits dans la commande avec les quantités regroupées
                commande.setProduits(produitsRegroupes);
            }
            System.out.println("Commande ajoutée avec ID: " + commande.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de la commande", e);
        }
    }

    @Override
    public void modifier(Commande commande) {
        if (commande.getLivraison() != null) {
            livraisonService.modifier(commande.getLivraison());
        }

        String req = "UPDATE `commande` SET `livraison_id`=?, `date_commande`=?, `total`=?, `statut`=? WHERE `id`=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, commande.getLivraison() != null ? commande.getLivraison().getId() : null);
            ps.setDate(2, new java.sql.Date(commande.getDateCommande().getTime()));
            ps.setDouble(3, commande.getTotal());
            ps.setString(4, commande.getStatut().toString());
            ps.setInt(5, commande.getId());
            ps.executeUpdate();

            String deleteReq = "DELETE FROM `commande_produit` WHERE `commande_id`=?";
            PreparedStatement deletePs = connection.prepareStatement(deleteReq);
            deletePs.setInt(1, commande.getId());
            deletePs.executeUpdate();

            if (commande.getProduits() != null && !commande.getProduits().isEmpty()) {
                List<CommandeProduit> produitsRegroupes = new ArrayList<>();
                commande.getProduits().stream()
                        .collect(Collectors.groupingBy(
                                CommandeProduit::getProduit,
                                Collectors.summingInt(CommandeProduit::getQuantite)
                        ))
                        .forEach((produit, quantite) -> produitsRegroupes.add(new CommandeProduit(produit, quantite)));

                String reqProduit = "INSERT INTO `commande_produit` (`commande_id`, `produit_id`) VALUES (?, ?)";
                PreparedStatement psProduit = connection.prepareStatement(reqProduit);
                for (CommandeProduit commandeProduit : produitsRegroupes) {
                    psProduit.setInt(1, commande.getId());
                    psProduit.setInt(2, commandeProduit.getProduit().getId());
                    psProduit.executeUpdate();
                }

                commande.setProduits(produitsRegroupes);
            }
            System.out.println("Commande modifiée");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Commande commande) {
        try {
            String deleteProduitReq = "DELETE FROM `commande_produit` WHERE `commande_id`=?";
            PreparedStatement deletePs = connection.prepareStatement(deleteProduitReq);
            deletePs.setInt(1, commande.getId());
            deletePs.executeUpdate();
            System.out.println("Associations commande_produit supprimées pour commande ID : " + commande.getId());

            String req = "DELETE FROM `commande` WHERE `id`=?";
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, commande.getId());
            ps.executeUpdate();
            System.out.println("Commande supprimée ID : " + commande.getId());

            if (commande.getLivraison() != null) {
                livraisonService.supprimer(commande.getLivraison());
                System.out.println("Livraison supprimée ID : " + commande.getLivraison().getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de la commande", e);
        }
    }

    @Override
    public List<Commande> rechercher() {
        String req = "SELECT * FROM `commande`";
        List<Commande> commandes = new ArrayList<>();
        try {
            System.out.println("Exécution de la requête pour récupérer les commandes...");
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int commandeId = rs.getInt("id");
                System.out.println("Traitement de la commande ID : " + commandeId);

                int livraisonId = rs.getInt("livraison_id");
                Livraison livraison = livraisonId > 0 ? livraisonService.rechercher().stream()
                        .filter(l -> l.getId() == livraisonId)
                        .findFirst().orElse(null) : null;

                List<CommandeProduit> produits = new ArrayList<>();
                String produitReq = "SELECT produit_id FROM `commande_produit` WHERE `commande_id`=?";
                PreparedStatement produitPs = connection.prepareStatement(produitReq);
                produitPs.setInt(1, commandeId);
                ResultSet produitRs = produitPs.executeQuery();
                while (produitRs.next()) {
                    int produitId = produitRs.getInt("produit_id");
                    System.out.println("Recherche du produit avec ID : " + produitId + " pour la commande ID : " + commandeId);
                    Produit produit = produitService.rechercher().stream()
                            .filter(p -> p.getId() == produitId)
                            .findFirst().orElse(null);
                    if (produit != null) {
                        System.out.println("Produit trouvé : " + produit.getNom() + " (ID : " + produit.getId() + ")");
                        // Puisqu'il n'y a pas de quantité dans la base, on initialise à 1
                        produits.add(new CommandeProduit(produit, 1));
                    } else {
                        System.err.println("Produit avec ID " + produitId + " non trouvé pour la commande ID : " + commandeId);
                    }
                }
                System.out.println("Nombre de produits récupérés pour commande ID " + commandeId + " : " + produits.size());

                Commande commande = new Commande(
                        commandeId,
                        livraison,
                        rs.getDate("date_commande"),
                        rs.getDouble("total"),
                        StatutCommande.valueOf(rs.getString("statut")),
                        produits
                );
                commandes.add(commande);
            }
            System.out.println("Nombre total de commandes récupérées : " + commandes.size());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des commandes : " + e.getMessage());
            e.printStackTrace();
        }
        return commandes;
    }
}