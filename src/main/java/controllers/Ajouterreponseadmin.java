package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.AdminService;
import entities.entiteReclamation;
import java.sql.SQLException;


import javafx.fxml.Initializable;


import java.net.URL;

import java.util.ResourceBundle;

public class Ajouterreponseadmin implements Initializable {

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private VBox contentPane;

    @FXML
    private Label headerLabel;

    @FXML
    private ListView<entiteReclamation> reclamationlist;

    @FXML
    private Label responseLabel;

    @FXML
    private TextArea responselabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load non-treated reclamations when the interface is initialized
        try {
            loadReclamations();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de chargement", 
                "Impossible de charger les réclamations: " + e.getMessage(), 
                Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        // Customize how reclamations are displayed in the ListView
        reclamationlist.setCellFactory(lv -> new ListCell<entiteReclamation>() {
            @Override
            protected void updateItem(entiteReclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);
                if (empty || reclamation == null) {
                    setText(null);
                } else {
                    // Display reclamation details (e.g., ID, type, content)
                    setText(String.format("ID: %d | Type: %s | Contenu: %s",
                        reclamation.getId(),
                        reclamation.getType(),
                        reclamation.getContenu().length() > 50 
                            ? reclamation.getContenu().substring(0, 50) + "..." 
                            : reclamation.getContenu()));
                }
            }
        });
    }

    @FXML
    void ajouterreponse(ActionEvent event) {
        try {
            // Récupérer l'ID de la réclamation sélectionnée
            entiteReclamation selectedReclamation = reclamationlist.getSelectionModel().getSelectedItem();
            if (selectedReclamation == null) {
                showAlert("Erreur", "Sélection requise", 
                    "Veuillez sélectionner une réclamation à traiter.", 
                    Alert.AlertType.WARNING);
                return;
            }
    
            // Récupérer la réponse de l'admin
            String reponse = responselabel.getText();
            if (reponse.isEmpty()) {
                showAlert("Erreur", "Champ vide", 
                    "Veuillez saisir une réponse.", 
                    Alert.AlertType.WARNING);
                return;
            }
    
            // Appeler le service pour enregistrer la réponse
            AdminService adminService = new AdminService();
            adminService.repondreReclamation(selectedReclamation.getId(), reponse);
             // Afficher un message de succès
        showAlert("Succès", "Réponse enregistrée", 
        "La réponse a été enregistrée avec succès.", 
        Alert.AlertType.INFORMATION);

    // Rafraîchir la liste des réclamations
    loadReclamations();
    
    // Vider le champ de réponse
            responselabel.clear();

} catch (SQLException e) {
    showAlert("Erreur", "Erreur de base de données", 
        "Impossible d'enregistrer la réponse: " + e.getMessage(), 
        Alert.AlertType.ERROR);
    e.printStackTrace();
}


    }

    @FXML
    void annulerreponse(ActionEvent event) {
        // Clear the selected reclamation and response text
        reclamationlist.getSelectionModel().clearSelection();
        responselabel.clear();

    }
    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void loadReclamations() throws SQLException {
        AdminService adminService = new AdminService();
        ObservableList<entiteReclamation> reclamations = 
            FXCollections.observableArrayList(adminService.getReclamationsNonTraitees());
        reclamationlist.setItems(reclamations);
    }

}
