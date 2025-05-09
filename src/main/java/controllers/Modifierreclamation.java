package controllers;

/*import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import entities.entiteReclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import services.Reclamation;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;*/
import javafx.fxml.FXMLLoader;


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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;


import java.util.ResourceBundle;


public class Modifierreclamation implements Initializable {

    @FXML
    private TextField modifierid;

    @FXML
    private TextArea modifierreclamation;

    


    @FXML
    private ListView<entiteReclamation> reclamationList;

    @FXML
    private ComboBox<String> type;

    private Reclamation service = new Reclamation();
    private int currentReclamationId;
    private String originalContent;
    private int currentUserId = 1;
    /* // Méthode pour définir l'ID de l'utilisateur connecté
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        service.setCurrentUserId(userId); // Met à jour aussi le service
    }*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up ComboBox with types
        ObservableList<String> types = FXCollections.observableArrayList(
                "problem technique",
                "service client",
                "facturation",
                "autre"
        );

        type.setItems(types);

        if (!types.isEmpty()) {
            type.getSelectionModel().selectFirst();
        }

        // Configure ListView
        reclamationList.setCellFactory(lv -> new ListCell<entiteReclamation>() {
            @Override
            protected void updateItem(entiteReclamation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("ID: %d|  Type: %s | Date: %s | Statut: %s\n%s",
                            item.getId(),
                            item.getType(),
                            item.getDatedepublication(),
                            item.getStatut(),
                            item.getContenu()));
                }
            }
        });
          // Add selection listener to ListView
        reclamationList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Réclamation sélectionnée - ID: " + newValue.getId()); // Debug
                loadReclamationData(newValue);
            }
        });

        // Set current user ID (à remplacer par l'ID de l'utilisateur connecté)
        service.setCurrentUserId(1); // Temporaire, à remplacer par l'ID réel de l'utilisateur

        // Load reclamations
        loadReclamations();
        }
    
        private void loadReclamations() {
            try {
                ObservableList<entiteReclamation> reclamations = FXCollections.observableArrayList(service.afficher());
                reclamationList.setItems(reclamations);
                System.out.println("Nombre de réclamations chargées: " + reclamations.size());

            } catch (SQLException e) {
                showAlert("Error", "Database Error", "Could not load reclamations: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    
        public void loadReclamationData(entiteReclamation reclamation) {
            if (reclamation != null) {
                currentReclamationId = reclamation.getId();
                System.out.println("Chargement de la réclamation ID: " + currentReclamationId);
                
                modifierid.setText(String.valueOf(reclamation.getId()));
                type.setValue(reclamation.getType());
                modifierreclamation.setText(reclamation.getContenu());
                originalContent = reclamation.getContenu();
            }
        }

    @FXML
    void annulermodification(ActionEvent event) {
        modifierid.clear();
        modifierreclamation.clear();
        type.getSelectionModel().clearSelection();
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
    void validermodification(ActionEvent event) {
        try {
            System.out.println("Début de la modification pour l'ID: " + currentReclamationId);
            
            String commentaire = modifierreclamation.getText();
            String selectedType = type.getValue();

            if (commentaire.isEmpty() || selectedType == null) {
                showAlert("Warning", "Missing Information", 
                    "Please fill all fields before submitting.", 
                    Alert.AlertType.WARNING);
                return;
            }

            entiteReclamation reclamation = new entiteReclamation(
                    currentReclamationId,
                    selectedType,
                    currentUserId,
                    new Date(),
                    commentaire,
                    commentaire.equals(originalContent) ? "traite" : "non traite"
            );

            service.modifier(reclamation);
            showAlert("Success", "Reclamation Modified", 
                "Your reclamation has been successfully modified.", 
                Alert.AlertType.INFORMATION);
            
            loadReclamations();
            annulermodification(event);

        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification: " + e.getMessage());
            showAlert("Database Error", "Error", 
                "Could not modify reclamation: " + e.getMessage(), 
                Alert.AlertType.ERROR);
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
