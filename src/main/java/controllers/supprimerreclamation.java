package controllers;

import services.Reclamation;
import entities.entiteReclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class supprimerreclamation implements Initializable {

    @FXML
    private ListView<entiteReclamation> supprimerlist;
    private Reclamation service = new Reclamation();
    private int currentUserId = 1;

    @FXML
    void annulersupprimer(ActionEvent event) {
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
    void validersupprimer(ActionEvent event) {
        entiteReclamation selectedReclamation = supprimerlist.getSelectionModel().getSelectedItem();
        
        if (selectedReclamation == null) {
            showAlert("Warning", "No Selection", 
                "Please select a reclamation to delete.", 
                Alert.AlertType.WARNING);
            return;
        }

        // Confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Reclamation");
        confirmDialog.setContentText("Are you sure you want to delete this reclamation?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                service.supprimer(selectedReclamation.getId());
                showAlert("Success", "Reclamation Deleted", 
                    "The reclamation has been successfully deleted.", 
                    Alert.AlertType.INFORMATION);
                
                // Reload the list
                loadReclamations();
            } catch (SQLException e) {
                showAlert("Database Error", "Error", 
                    "Could not delete reclamation: " + e.getMessage(), 
                    Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
         // Configure ListView
         supprimerlist.setCellFactory(lv -> new ListCell<entiteReclamation>() {
            @Override
            protected void updateItem(entiteReclamation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("ID: %s | Type: %s | Date: %s | Statut: %s\n%s",
                            String.valueOf(item.getId()),
                            item.getType(),
                            item.getDatedepublication(),
                            item.getStatut(),
                            item.getContenu()));
                }
            }
        });

        // Set current user ID
        service.setCurrentUserId(currentUserId);

        // Load reclamations
        loadReclamations();
    }
    private void loadReclamations() {
        try {
            ObservableList<entiteReclamation> reclamations = FXCollections.observableArrayList(service.afficher());
            supprimerlist.setItems(reclamations);
            System.out.println("Nombre de réclamations chargées: " + reclamations.size());
        } catch (SQLException e) {
            showAlert("Error", "Database Error", "Could not load reclamations: " + e.getMessage(), Alert.AlertType.ERROR);
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
