package services;

import utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entities.entiteReclamation;

public class Reclamation implements IReclamation<entiteReclamation> {
    private Connection connection;
    private int currentUserId; // Pour stocker l'ID de l'utilisateur connecté

    public Reclamation() {
        connection = MyDataBase.getInstance().getConnection();
    }

    // Méthode pour définir l'utilisateur connecté
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    @Override
    public List<entiteReclamation> afficher() throws SQLException {
        List<entiteReclamation> reclamations = new ArrayList<>();
        String req = "SELECT * FROM reclamation WHERE iduser = ?";
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, currentUserId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                entiteReclamation reclamation = new entiteReclamation(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getInt("iduser"),
                        rs.getDate("datedepublication"),
                        rs.getString("contenu"),
                        rs.getString("statut"),
                        rs.getString("adresse_email")
                );
                reclamation.setAdresseEmail(rs.getString("adresse_email"));
                reclamations.add(reclamation);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réclamations: " + e.getMessage());
            throw e;
        }
        return reclamations;
    }

    @Override
    public void ajouter(entiteReclamation reclamation) throws SQLException {
        String query = "INSERT INTO reclamation (type, iduser, datedepublication, contenu, statut, adresse_email) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, reclamation.getType());
            pst.setInt(2, currentUserId); // Utiliser l'ID de l'utilisateur connecté
            pst.setDate(3, new java.sql.Date(reclamation.getDatedepublication().getTime()));
            pst.setString(4, reclamation.getContenu());
            pst.setString(5, reclamation.getStatut());
            pst.setString(6, reclamation.getAdresseEmail());

            pst.executeUpdate();
        }
    }

    @Override
    public void modifier(entiteReclamation reclamation) throws SQLException {
        System.out.println("Tentative de modification de la réclamation ID: " + reclamation.getId());
        
        // Vérification préalable avec plus de détails
        String checkQuery = "SELECT * FROM reclamation WHERE id = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setInt(1, reclamation.getId());
            ResultSet rs = checkStatement.executeQuery();
            
            if (rs.next()) {
                System.out.println("Réclamation trouvée dans la base de données");
                // Procéder à la modification
                String req = "UPDATE reclamation SET type=?, contenu=?, statut=?, adresse_email=?, datedepublication=? WHERE id=?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
                    preparedStatement.setString(1, reclamation.getType());
                    preparedStatement.setString(2, reclamation.getContenu());
                    preparedStatement.setString(3, reclamation.getStatut());
                    preparedStatement.setString(4, reclamation.getAdresseEmail());
                    preparedStatement.setDate(5, new java.sql.Date(reclamation.getDatedepublication().getTime()));
                    preparedStatement.setInt(6, reclamation.getId());

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Réclamation modifiée avec succès");
                    } else {
                        throw new SQLException("Échec de la modification");
                    }
                }
            } else {
                System.out.println("Aucune réclamation trouvée avec l'ID: " + reclamation.getId());
                throw new SQLException("Réclamation non trouvée");
            }
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        // Vérifier d'abord si la réclamation appartient à l'utilisateur connecté
        String checkQuery = "SELECT COUNT(*) FROM reclamation WHERE id = ? AND iduser = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setInt(1, id);
            checkStatement.setInt(2, currentUserId);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count == 0) {
                throw new SQLException("Vous n'avez pas la permission de supprimer cette réclamation");
            }
        }

        String deleteQuery = "DELETE FROM reclamation WHERE id = ? AND iduser = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1, id);
            deleteStatement.setInt(2, currentUserId);
            int rowsAffected = deleteStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Reclamation avec ID " + id + " a été supprimée avec succès.");
            }
        }
    }
}
