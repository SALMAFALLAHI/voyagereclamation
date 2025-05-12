package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
// import javafx.scene.Scene; // Scene n_est plus nécessaire ici pour la déconnexion
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
// import models.Users; // currentUser n_est pas utilisé dans ce contrôleur après les modifs
import test.Main; // Pour l_appel statique à changeScene

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminMENUController implements Initializable {
    @FXML
    private StackPane contentArea; // This is the central area to load content
    // private Users currentUser; // currentUser n_est pas utilisé, LoginController.setCurrentUser(null) suffit

    // Les fx:id pour les boutons ne sont pas nécessaires si les onAction sont définis dans le FXML
    // @FXML
    // private Button btnGererUtilisateurs;

    // @FXML
    // private Button btnGererReclamations;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger une vue initiale ou un placeholder si nécessaire
        // Par exemple, pour afficher un message de bienvenue ou les stats admin par défaut.
        // Pour l_instant, on peut laisser vide ou charger un FXML spécifique si admin_dashboard_placeholder.fxml n_existe pas.
        // loadContent("/view/admin_dashboard_placeholder.fxml"); 
        // Pour éviter une erreur si le placeholder n_existe pas, on peut charger directement la gestion utilisateurs
        if (contentArea != null && contentArea.getChildren().isEmpty()) {
             loadContent("/view/admin_dashboard.fxml"); // Charge la gestion des utilisateurs par défaut
        }
    }


    private void loadContent(String fxmlPath) {
        if (contentArea == null) {
            System.err.println("AdminMENUController: contentArea is null. Cannot load view: " + fxmlPath);
            showAlert(Alert.AlertType.ERROR, "Erreur Critique", "La zone de contenu principale n_est pas initialisée.");
            return;
        }
        try {
            contentArea.getChildren().clear(); // Clear previous content
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de Chargement", "Impossible de charger la vue : " + fxmlPath + "\n" + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur Critique", "Un composant FXML nécessaire est manquant ou non initialisé pour charger : " + fxmlPath + ". Vérifiez que le chemin FXML est correct et que le fichier existe.");
        }
    }

    @FXML
    public void handleGererUtilisateurs(ActionEvent event) { // ActionEvent ajouté pour cohérence, même si non utilisé ici
        loadContent("/view/admin_dashboard.fxml");
    }

    @FXML
    private void handleGererReservations(ActionEvent event) { 
        showAlert(Alert.AlertType.INFORMATION, "Fonctionnalité en cours", "La gestion des réservations sera bientôt disponible.");
    }

    @FXML
    private void handleGererHebergements(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Fonctionnalité en cours", "La gestion des hébergements sera bientôt disponible.");
    }

    @FXML
    private void handleGererVols(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Fonctionnalité en cours", "La gestion des vols sera bientôt disponible.");
    }

    @FXML
    public void handleGererReclamations(ActionEvent event) { // ActionEvent ajouté

        loadContent("/view/menuadmin.fxml");
    }

    @FXML
    private void handleGererExcursions(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Fonctionnalité en cours", "La gestion des excursions sera bientôt disponible.");
    }

    @FXML
    private void handleDeconnexion(ActionEvent event) {
        LoginController.setCurrentUser(null); // Efface l_utilisateur courant globalement
        // this.currentUser = null; // Plus besoin si currentUser n_est pas utilisé localement
        try {
            // Appel direct à la méthode statique changeScene de la classe Main
            Main.changeScene("/view/login.fxml", "Système de Gestion de Vols - Connexion", 600, 400);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
             showAlert(Alert.AlertType.ERROR, "Erreur Critique", "Erreur lors de la déconnexion. Le Stage principal (stg) dans Main.java pourrait ne pas être initialisé.");
             e.printStackTrace();
        }
    }

    @FXML
    public void handleAjouterReponseAdmin(ActionEvent event) {
        loadContent("/view/ajouterreponseadmin.fxml");
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

