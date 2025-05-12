package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import test.Main; // Pour la navigation
import utils.DatabaseUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpFormController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button signUpButton;

    @FXML
    private Button backToWelcomeButton;

    @FXML
    private Label errorLabel;

    @FXML
    void handleSignUpAction(ActionEvent event) {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText(); // Optionnel
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorLabel.setText("Format d_email invalide.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        // Vérifier si l_email existe déjà
        if (emailExists(email)) {
            errorLabel.setText("Cet email est déjà utilisé par un autre compte.");
            return;
        }

        // Statut par défaut pour un nouvel utilisateur
        String defaultStatus = "bloqué";

        String sql = "INSERT INTO user (nom, prenom, email, telephone, password, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email);
            pstmt.setString(4, telephone.isEmpty() ? null : telephone);
            pstmt.setString(5, password); // Idéalement, hacher le mot de passe avant de le stocker
            pstmt.setString(6, defaultStatus);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Inscription Réussie",
                        "Votre compte a été créé avec succès. Il est actuellement en attente d_approbation par un administrateur. Vous serez notifié une fois activé.");
                // Rediriger vers l_écran d_accueil ou de connexion
                handleBackToWelcomeAction(null); // Ou vers login directement
            } else {
                errorLabel.setText("Erreur lors de la création du compte. Veuillez réessayer.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Base de Données", "Impossible de créer le compte : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean emailExists(String email) {
        String sql = "SELECT userId FROM user WHERE email = ?";
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // True si l_email existe déjà
        } catch (SQLException e) {
            e.printStackTrace();
            // En cas d_erreur, on peut considérer que l_email n_existe pas pour éviter de bloquer l_inscription
            // ou afficher une erreur plus générique.
            showAlert(Alert.AlertType.WARNING, "Erreur de Vérification", "Impossible de vérifier l_unicité de l_email. Veuillez réessayer.");
            return true; // Pour être prudent, considérer que l_email existe en cas d_erreur
        }
    }

    @FXML
    void handleBackToWelcomeAction(ActionEvent event) {
        try {
            Main m = new Main();
            m.changeScene("/view/welcome_screen.fxml", "Bienvenue - Gestion de Vols", 600, 400);
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

