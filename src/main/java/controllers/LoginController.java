package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*; // Import Alert
import models.Users;
import test.Main; // Pour l_appel statique à changeScene
import utils.DatabaseUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button backToWelcomeButton;

    @FXML
    private Label errorLabel;

    private static Users currentUser;

    public static Users getCurrentUser() {
        return currentUser;
    }

    // Added public static setter for currentUser
    public static void setCurrentUser(Users user) {
        currentUser = user;
    }

    @FXML
    void handlePasswordEnter(ActionEvent event) {
        handleLoginButtonAction(event);
    }

    @FXML
    void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if ("adminjavapi@gmail.com".equals(email) && "javapi".equals(password)) {
            errorLabel.setText("");
            System.out.println("Connexion administrateur réussie !");
            setCurrentUser(new Users(0, "Admin", "Admin", "adminjavapi@gmail.com", "N/A", "javapi", "actif"));
            try {
                Main.changeScene("/view/admin_menu.fxml", "Tableau de Bord Administrateur", 1000, 700);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de Chargement", "Erreur lors du chargement du tableau de bord admin.");
                e.printStackTrace();
            }
            return;
        }

        String sql = "SELECT * FROM user WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                if ("actif".equalsIgnoreCase(status)) {
                    errorLabel.setText("");
                    System.out.println("Connexion utilisateur réussie !");
                    setCurrentUser(new Users(
                            rs.getInt("userId"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("telephone"),
                            rs.getString("password"), 
                            rs.getString("status")
                    ));
                    Main.changeScene("/view/user_dashboard.fxml", "Mon Espace Personnel", 900, 600);
                } else if ("bloqué".equalsIgnoreCase(status)) {
                    errorLabel.setText("");
                    showAlert(Alert.AlertType.WARNING, "Compte Bloqué", "Votre compte utilisateur est actuellement bloqué. Veuillez contacter l_administrateur du système pour plus d_informations.");
                } else {
                     errorLabel.setText("Statut de compte inconnu. Veuillez contacter l_administrateur.");
                }
            } else {
                errorLabel.setText("Email ou mot de passe incorrect.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Base de Données", "Un problème est survenu lors de la tentative de connexion. Veuillez réessayer plus tard ou contacter le support.");
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Chargement", "Erreur lors du chargement de la page suivante.");
            e.printStackTrace();
        }
    }

    @FXML
    void handleBackToWelcomeAction(ActionEvent event) {
        try {
            Main.changeScene("/view/welcome_screen.fxml", "Bienvenue - Gestion de Vols", 600, 400);
        } catch (IOException e) {
            System.err.println("Erreur lors du retour à l_écran d_accueil: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner à l_écran d_accueil.");
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

