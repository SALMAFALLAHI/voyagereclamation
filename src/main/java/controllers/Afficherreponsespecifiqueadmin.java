package controllers;

import models.entiteReclamation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import services.AdminService;

import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Afficherreponsespecifiqueadmin {

    @FXML
    private TextArea contenuArea;

    @FXML
    private Label dateLabel;

    @FXML
    private VBox detailsPane;

    @FXML
    private Label headerLabel;

    @FXML
    private TextField idField;

    @FXML
    private TextArea responseArea;

    @FXML
    private Label responseDateLabel;

    @FXML
    private Button searchButton;

    @FXML
    private Label typeLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Label idLabel;

    private AdminService adminService;

    public Afficherreponsespecifiqueadmin() {
        adminService = new AdminService();
    }

    @FXML
    void rechercherReclamation(ActionEvent event) {
        try {
            // Get and validate the ID input
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                showAlert("Erreur", "Champ vide", 
                    "Veuillez entrer un ID de réclamation.", 
                    Alert.AlertType.WARNING);
                return;
            }

            // Parse the ID
            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Format invalide", 
                    "L'ID doit être un nombre entier.", 
                    Alert.AlertType.ERROR);
                return;
            }

            // Search for the reclamation
            entiteReclamation reclamation = adminService.getReclamationById(id);
            
            if (reclamation == null) {
                showAlert("Information", "Réclamation non trouvée", 
                    "Aucune réclamation trouvée avec l'ID " + id, 
                    Alert.AlertType.INFORMATION);
                annulerRecherche(event);
                return;
            }

            // Display reclamation details
            idLabel.setText(String.valueOf(reclamation.getId()));
            typeLabel.setText(reclamation.getType());
            
            // Format and display the date
            if (reclamation.getDatedepublication() != null) {
                dateLabel.setText(formatDate(reclamation.getDatedepublication()));
            } else {
                dateLabel.setText("N/A");
            }
            
            contenuArea.setText(reclamation.getContenu());

            // Display response details if available
            if (reclamation.getReponse() != null && !reclamation.getReponse().isEmpty()) {
                if (reclamation.getDateReponse() != null) {
                    responseDateLabel.setText(formatDate(reclamation.getDateReponse()));
                } else {
                    responseDateLabel.setText("N/A");
                }
                responseArea.setText(reclamation.getReponse());
            } else {
                responseDateLabel.setText("N/A");
                responseArea.setText("Aucune réponse disponible");
            }

            // Make details visible
            detailsPane.setVisible(true);

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de base de données", 
                "Impossible de récupérer la réclamation: " + e.getMessage(), 
                Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void annulerRecherche(ActionEvent event) {
        // Clear the ID field
        idField.clear();
        
        // Reset all detail fields
        idLabel.setText("N/A");
        typeLabel.setText("N/A");
        dateLabel.setText("N/A");
        contenuArea.clear();
        responseDateLabel.setText("N/A");
        responseArea.clear();

        cancelButton.getScene().getWindow().hide();
        
        // Hide the details pane
        detailsPane.setVisible(false);
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "N/A";
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

