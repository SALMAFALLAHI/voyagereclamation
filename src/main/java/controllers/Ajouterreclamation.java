package controllers;

import entities.entiteReclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import services.Reclamation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Ajouterreclamation implements Initializable {

    @FXML
    private TextArea ajoutcommentaire;

   

    @FXML
    private ComboBox<String> ajoutype;

    private Reclamation service = new Reclamation();
    private int currentUserId = 1; // ID de l'utilisateur connecté
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
      /*  // Initialize type mapping
        typeMapping.put("1 problem technique", 1);
        typeMapping.put("2 service client", 2);
        typeMapping.put("3 facturation", 3);
        typeMapping.put("4 autre", 4);*/

        // Set up ComboBox
        ObservableList<String> types = FXCollections.observableArrayList(
                "problem technique",
                "service client",
                "facturation",
                "autre"
        );

        ajoutype.setItems(types);

        if (!types.isEmpty()) {
            ajoutype.getSelectionModel().selectFirst();
        }

        // Set current user ID
        service.setCurrentUserId(currentUserId);
    }

    @FXML
    void annulerajout(ActionEvent event) {
        
        ajoutcommentaire.clear();
        ajoutype.getSelectionModel().clearSelection();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Menu Réclamations");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Fermer la fenêtre actuelle
            ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void validerajout(ActionEvent event) {
        try {
            
        // 1. Récupération des données du formulaire
            String commentaire = ajoutcommentaire.getText();
            String selectedTypeName = ajoutype.getValue();

            // 2. Vérification des champs
            if (commentaire.isEmpty() || selectedTypeName == null) {
                showAlert("Warning", "Missing Information", "Please fill all fields before submitting.", Alert.AlertType.WARNING);
                return;
            }

            // Create reclamation object with current user ID
            entiteReclamation reclamation = new entiteReclamation(
                    0, // auto-generated
                    selectedTypeName,
                    currentUserId, // implement this
                    new Date(),
                    commentaire,
                    "non traite"
            );

            // 5. Appel du service pour ajouter la réclamation

            service.ajouter(reclamation);

            // 6. Affichage d'un message de succès

            showAlert("Success", "Reclamation Added", "Your reclamation has been successfully submitted.", Alert.AlertType.INFORMATION);
            annulerajout(event);

        } catch (SQLException e) {
            showAlert("Database Error", "Error", "Could not add reclamation: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}