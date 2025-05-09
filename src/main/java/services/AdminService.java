package services;

import entities.entiteReclamation;
import entities.entiteReponseAdmin;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AdminService {
    private Connection connection;

    public AdminService() {
        connection = MyDataBase.getInstance().getConnection();
    }
    public List<entiteReclamation> afficherReclamationsTraitees() throws SQLException {
        List<entiteReclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamation WHERE statut = 'traite'";
        
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                entiteReclamation reclamation = new entiteReclamation(
                    resultSet.getInt("id"),
                    resultSet.getString("type"),
                    resultSet.getInt("iduser"),
                    resultSet.getDate("datedepublication"),
                    resultSet.getString("contenu"),
                    resultSet.getString("statut")
                );
                reclamations.add(reclamation);
            }
        }
        return reclamations;
    }
    public List<entiteReclamation> afficherReclamationsNonTraitees() throws SQLException {
        List<entiteReclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamation WHERE statut = 'non traite'";
        
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                entiteReclamation reclamation = new entiteReclamation(
                    resultSet.getInt("id"),
                    resultSet.getString("type"),
                    resultSet.getInt("iduser"),
                    resultSet.getDate("datedepublication"),
                    resultSet.getString("contenu"),
                    resultSet.getString("statut")
                );
                reclamations.add(reclamation);
            }
        }
        return reclamations;
    }
    // Méthode pour afficher toutes les réponses admin
    public List<Map<String, Object>> afficherReponsesAdmin() throws SQLException {
        List<Map<String, Object>> reponses = new ArrayList<>();
        String query = "SELECT ra.*, r.contenu, r.type, r.datedepublication " +
                      "FROM reponse_admin ra " +
                      "JOIN reclamation r ON ra.reclamation_id = r.id " +
                      "ORDER BY ra.date_reponse DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                Map<String, Object> reponse = new HashMap<>();
                reponse.put("id", resultSet.getInt("id"));
                reponse.put("reclamation_id", resultSet.getInt("reclamation_id"));
                reponse.put("reponse", resultSet.getString("reponse"));
                reponse.put("date_reponse", resultSet.getDate("date_reponse"));
                reponse.put("contenu_reclamation", resultSet.getString("contenu"));
                reponse.put("type_reclamation", resultSet.getString("type"));
                reponse.put("date_reclamation", resultSet.getDate("datedepublication"));
                reponses.add(reponse);
            }
        }
        return reponses;
    }


    public List<entiteReclamation> getReclamationsNonTraitees() throws SQLException {
        List<entiteReclamation> reclamations = new ArrayList<>();
        String req = "SELECT * FROM reclamation WHERE statut = 'non traite'";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                entiteReclamation reclamation = new entiteReclamation(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getInt("iduser"),
                        rs.getDate("datedepublication"),
                        rs.getString("contenu"),
                        rs.getString("statut")
                );
                reclamations.add(reclamation);
            }
        }
        return reclamations;
    }

    public void repondreReclamation(int idReclamation, String reponse) throws SQLException {
        // Commencer une transaction
        connection.setAutoCommit(false);

        try {
            // 1. Mettre à jour le statut de la réclamation
            String updateReq = "UPDATE reclamation SET statut = 'traite' WHERE id = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateReq)) {
                updateStatement.setInt(1, idReclamation);
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Aucune réclamation trouvée avec cet ID");
                }
            }

            // 2. Enregistrer la réponse
            String insertReq = "INSERT INTO reponse_admin (reclamation_id, reponse) VALUES (?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertReq)) {
                insertStatement.setInt(1, idReclamation);
                insertStatement.setString(2, reponse);
                insertStatement.executeUpdate();
            }

            // Valider la transaction
            connection.commit();
            System.out.println("Réclamation traitée et réponse enregistrée avec succès");
        } catch (SQLException e) {
            // En cas d'erreur, annuler la transaction
            connection.rollback();
            throw e;
        } finally {
            // Restaurer l'auto-commit
            connection.setAutoCommit(true);
        }
    }

    public List<entiteReponseAdmin> getReponsesPourReclamation(int idReclamation) throws SQLException {
        List<entiteReponseAdmin> reponses = new ArrayList<>();
        String req = "SELECT * FROM reponse_admin WHERE reclamation_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, idReclamation);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                entiteReponseAdmin reponse = new entiteReponseAdmin(
                        rs.getInt("id"),
                        rs.getInt("reclamation_id"),
                        rs.getString("reponse"),
                        rs.getTimestamp("date_reponse")
                );
                reponses.add(reponse);
            }
        }
        return reponses;
    }
    // Méthode pour afficher la réponse admin d'une réclamation spécifique
    public Map<String, Object> afficherReponseAdminReclamation(int reclamationId) throws SQLException {
        String query = "SELECT r.id, r.contenu, r.type, r.datedepublication, ra.reponse, ra.date_reponse " +
                      "FROM reclamation r " +
                      "LEFT JOIN reponse_admin ra ON r.id = ra.reclamation_id " +
                      "WHERE r.id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, reclamationId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Map<String, Object> details = new HashMap<>();
                details.put("id_reclamation", resultSet.getInt("id"));
                details.put("contenu", resultSet.getString("contenu"));
                details.put("type", resultSet.getString("type"));
                details.put("date_reclamation", resultSet.getDate("datedepublication"));
                details.put("reponse_admin", resultSet.getString("reponse"));
                details.put("date_reponse", resultSet.getDate("date_reponse"));
                return details;
            }
        }
        return null;
    }

}
