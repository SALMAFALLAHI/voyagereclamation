package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Users;
import test.Main;

import java.io.IOException;

public class UserProfileController {

    @FXML
    private Label nomLabel;

    @FXML
    private Label prenomLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label telephoneLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button editProfileButton;

    @FXML
    private Button logoutButton;

    private Users currentUser;

    @FXML
    public void initialize() {
        currentUser = LoginController.getCurrentUser();
        if (currentUser != null) {
            loadUserProfile(currentUser);
        }
    }

    public void loadUserProfile(Users user) {
        this.currentUser = user; 
        nomLabel.setText(user.getNom());
        prenomLabel.setText(user.getPrenom());
        emailLabel.setText(user.getEmail());
        telephoneLabel.setText(user.getTelephone() != null && !user.getTelephone().isEmpty() ? user.getTelephone() : "N/A");
        statusLabel.setText(user.getStatus());
    }

    @FXML
    void handleEditProfile(ActionEvent event) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur n\'est actuellement connecté pour modification.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user_form.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            // Ici, l'utilisateur modifie son propre profil, donc ce n'est PAS un contexte admin pour la modification du statut.
            controller.setUserToEdit(currentUser, false); 
            controller.setUserProfileController(this); 

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier mes informations");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d\'ouvrir le formulaire de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogoutAction(ActionEvent event) {
        LoginController.setCurrentUser(null);
        try {
            Main m = new Main();
            m.changeScene("/view/login.fxml", "Système de Gestion de Vols - Connexion", 600, 400);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
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

