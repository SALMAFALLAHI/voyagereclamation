package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class Menuadmin {

    @FXML
    private Button addResponseButton;

    @FXML
    private Label headerLabel;

    @FXML
    private Button responseHistoryButton;

    @FXML
    private Button searchReclamationButton;

    @FXML
    private StackPane menuContentArea; // This must match fx:id in FXML

    @FXML
    void goToAddResponse(ActionEvent event) {
        loadMenuContent("/view/ajouterreponseadmin.fxml");
    }
    @FXML
    public void handleAjouterReponseAdmin(ActionEvent event) {
        loadMenuContent("/view/ajouterreponseadmin.fxml");
    }

    @FXML
    void goToResponseHistory(ActionEvent event) {
        loadMenuContent("/view/AfficherReclamationTraitees.fxml");
    }

    @FXML
    void goToSearchReclamation(ActionEvent event) {
        loadMenuContent("/view/afficherreponsespecifiqueadmin.fxml");
    }

    // Load FXML into the StackPane instead of opening a new window
    private void loadMenuContent(String fxmlPath) {
        try {
            menuContentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            menuContentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de Chargement", "Impossible de charger la vue : " + fxmlPath + "\n" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}