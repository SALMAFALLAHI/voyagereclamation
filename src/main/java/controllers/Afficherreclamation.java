
package controllers;

import entities.entiteReclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import services.Reclamation;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Afficherreclamation  implements Initializable {

    @FXML
    private ListView<entiteReclamation> afficherlist;
    private Reclamation service = new Reclamation();
    
    private int currentUserId = 1;
    @Override
    public void initialize(URL location, ResourceBundle resources){
        // Configure ListView
        afficherlist.setCellFactory(lv -> new ListCell<entiteReclamation>() {
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
        // Set current user ID (à remplacer par l'ID de l'utilisateur connecté)
        service.setCurrentUserId(currentUserId); // Temporaire, à remplacer par l'ID réel de l'utilisateur

        // Load reclamations
        loadReclamations();
    }
    private void loadReclamations() {
        try {
            ObservableList<entiteReclamation> reclamations = FXCollections.observableArrayList(service.afficher());
            afficherlist.setItems(reclamations);
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
    @FXML
    void annulerafficher(ActionEvent event) {
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



}


