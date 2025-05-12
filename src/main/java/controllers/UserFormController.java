package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Users;
import utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFormController {

    @FXML
    private Label formTitleLabel;

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
    private ComboBox<String> statusComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorLabel;

    private Users userToEdit;
    private AdminDashboardController adminController;
    private UserProfileController userProfileController;
    private boolean isAdminEditing = false;

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList("actif", "bloqué"));
        // La logique pour désactiver statusComboBox sera dans setUserToEdit ou une méthode appelée par elle.
    }

    public void setUserToEdit(Users user, boolean isAdmin) {
        this.userToEdit = user;
        this.isAdminEditing = isAdmin;

        if (user != null) {
            formTitleLabel.setText("Modifier l\\'utilisateur");
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            telephoneField.setText(user.getTelephone());
            statusComboBox.setValue(user.getStatus());
        } else {
            formTitleLabel.setText("Ajouter un nouvel utilisateur");
            statusComboBox.setValue("bloqué"); // Statut par défaut pour un nouvel utilisateur
        }

        // Restreindre la modification du statut
        // Seul l'admin peut modifier le statut.
        // Si ce n'est pas l'admin qui édite, ou si c'est un ajout (où l'admin est implicite), le champ statut est désactivé.
        // Correction: L'admin doit toujours pouvoir modifier le statut, même pour un nouvel utilisateur.
        // Un utilisateur normal modifiant son propre profil ne doit PAS pouvoir changer son statut.
        if (isAdminEditing) {
            statusComboBox.setDisable(false);
        } else {
            statusComboBox.setDisable(true);
        }
        // Si c'est un nouvel utilisateur (userToEdit == null), c'est forcément l'admin qui l'ajoute, donc statusComboBox doit être enabled.
        if (userToEdit == null && isAdminEditing) { // Cas d'un ajout par l'admin
             statusComboBox.setDisable(false);
        } else if (userToEdit != null && !isAdminEditing) { // Cas d'un utilisateur normal modifiant son profil
             statusComboBox.setDisable(true);
        } else if (userToEdit != null && isAdminEditing) { // Cas d'un admin modifiant un utilisateur existant
             statusComboBox.setDisable(false);
        }

    }

    public void setAdminController(AdminDashboardController adminController) {
        this.adminController = adminController;
    }

    public void setUserProfileController(UserProfileController userProfileController) {
        this.userProfileController = userProfileController;
    }

    @FXML
    void handleSaveUserAction(ActionEvent event) {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();
        String password = passwordField.getText();
        String status = statusComboBox.getValue(); // Sera la valeur actuelle si désactivé, ou la nouvelle si activé

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || status == null) {
            errorLabel.setText("Veuillez remplir tous les champs obligatoires (Nom, Prénom, Email, Statut).");
            return;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorLabel.setText("Format d\\'email invalide.");
            return;
        }

        if (userToEdit == null) { // Ajout d'un nouvel utilisateur (par l'admin)
            if (password.isEmpty()) {
                errorLabel.setText("Le mot de passe est obligatoire pour un nouvel utilisateur.");
                return;
            }
            if (emailExists(email, 0)) {
                 errorLabel.setText("Cet email est déjà utilisé par un autre compte.");
                 return;
            }
            String sql = "INSERT INTO user (nom, prenom, email, telephone, password, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseUtil.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nom);
                pstmt.setString(2, prenom);
                pstmt.setString(3, email);
                pstmt.setString(4, telephone);
                pstmt.setString(5, password);
                pstmt.setString(6, status); // L'admin définit le statut à la création
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès.");
                if (adminController != null) adminController.refreshUserList();
                closeWindow();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Impossible d\\'ajouter l\\'utilisateur: " + e.getMessage());
                e.printStackTrace();
            }
        } else { // Modification d'un utilisateur existant
            if (emailExists(email, userToEdit.getUserId())) {
                 errorLabel.setText("Cet email est déjà utilisé par un autre compte.");
                 return;
            }
            String sql;
            boolean passwordChanged = !password.isEmpty();
            
            // Si le statut n'est pas modifiable par l'utilisateur actuel (non-admin), on récupère le statut existant de l'utilisateur.
            String finalStatus = statusComboBox.isDisabled() ? userToEdit.getStatus() : statusComboBox.getValue();

            if (!passwordChanged) {
                sql = "UPDATE user SET nom = ?, prenom = ?, email = ?, telephone = ?, status = ? WHERE userId = ?";
            } else {
                sql = "UPDATE user SET nom = ?, prenom = ?, email = ?, telephone = ?, password = ?, status = ? WHERE userId = ?";
            }

            try (Connection conn = DatabaseUtil.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nom);
                pstmt.setString(2, prenom);
                pstmt.setString(3, email);
                pstmt.setString(4, telephone);
                if (!passwordChanged) {
                    pstmt.setString(5, finalStatus);
                    pstmt.setInt(6, userToEdit.getUserId());
                } else {
                    pstmt.setString(5, password);
                    pstmt.setString(6, finalStatus);
                    pstmt.setInt(7, userToEdit.getUserId());
                }
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour avec succès.");
                if (adminController != null) adminController.refreshUserList();
                if (userProfileController != null) {
                    userToEdit.setNom(nom);
                    userToEdit.setPrenom(prenom);
                    userToEdit.setEmail(email);
                    userToEdit.setTelephone(telephone);
                    userToEdit.setStatus(finalStatus); // Mettre à jour avec le statut final
                    if (passwordChanged) userToEdit.setPassword(password);
                    userProfileController.loadUserProfile(userToEdit);
                }
                closeWindow();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Impossible de mettre à jour l\\'utilisateur: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean emailExists(String email, int currentUserId) {
        String sql = "SELECT userId FROM user WHERE email = ?";
        if (currentUserId > 0) {
            sql += " AND userId != ?";
        }
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            if (currentUserId > 0) {
                pstmt.setInt(2, currentUserId);
            }
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.WARNING, "Erreur de vérification", "Impossible de vérifier l\\'unicité de l\\'email. Veuillez réessayer.");
            return true; 
        }
    }

    @FXML
    void handleCancelAction(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

