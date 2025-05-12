package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Users;
import test.Main;
import utils.DatabaseUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Users> usersTable;

    @FXML
    private TableColumn<Users, Integer> userIdCol;

    @FXML
    private TableColumn<Users, String> nomCol;

    @FXML
    private TableColumn<Users, String> prenomCol;

    @FXML
    private TableColumn<Users, String> emailCol;

    @FXML
    private TableColumn<Users, String> telephoneCol;

    @FXML
    private TableColumn<Users, String> statusCol;

    @FXML
    private TableColumn<Users, Void> actionsCol;

    @FXML
    private Button addUserButton;
    
    @FXML
    private Button refreshButton;

    @FXML
    private Button logoutButton;
    
    // Ces boutons sont dans le FXML admin_dashboard.fxml mais leur logique est dans AdminMENUController
    // On ajoute les handlers ici pour éviter la LoadException, mais ils ne feront rien ou afficheront un message.
    @FXML
    private Button btnGererUtilisateurs; 

    @FXML
    private Button btnGererReclamations;

    private ObservableList<Users> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (LoginController.getCurrentUser() != null && "adminjavapi@gmail.com".equals(LoginController.getCurrentUser().getEmail())) {
            welcomeLabel.setText("Bienvenue, " + LoginController.getCurrentUser().getPrenom() + " " + LoginController.getCurrentUser().getNom() + "!");
        } else {
            welcomeLabel.setText("Accès non autorisé ou utilisateur non défini.");
        }
        setupTableColumns();
        loadUsersData();
        addButtonsToTable();
    }

    private void setupTableColumns() {
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadUsersData() {
        userList.clear();
        String sql = "SELECT userId, nom, prenom, email, telephone, status FROM user WHERE email != ?"; 
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "adminjavapi@gmail.com");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userList.add(new Users(
                        rs.getInt("userId"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        null, 
                        rs.getString("status")
                ));
            }
            usersTable.setItems(userList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Impossible de charger les utilisateurs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addButtonsToTable() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox pane = new HBox(editButton, deleteButton);

            {
                pane.setSpacing(5);
                editButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    handleEditUserAction(user);
                });
                deleteButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    handleDeleteUserAction(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    @FXML
    void handleAddUserAction(ActionEvent event) {
        openUserForm(null, "Ajouter un nouvel utilisateur", true); 
    }

    @FXML
    void handleRefreshAction(ActionEvent event) {
        loadUsersData();
    }
    
    private void handleEditUserAction(Users user) {
        openUserForm(user, "Modifier l\\'utilisateur", true); 
    }

    private void openUserForm(Users user, String title, boolean isAdminContext) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user_form.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setUserToEdit(user, isAdminContext); 
            controller.setAdminController(this); 

            Stage stage = new Stage(); 
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d_ouvrir le formulaire utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteUserAction(Users user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l\\'utilisateur: " + user.getPrenom() + " " + user.getNom() + " ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ? Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM user WHERE userId = ?";
            try (Connection conn = DatabaseUtil.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, user.getUserId());
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé avec succès.");
                    loadUsersData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur n\\'a été supprimé. L\\'ID est peut-être incorrect.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Impossible de supprimer l\\'utilisateur: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleLogoutAction(ActionEvent event) {
        LoginController.setCurrentUser(null);
        try {
            Main.changeScene("/view/login.fxml", "Système de Gestion de Vols - Connexion", 600, 400);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Ajout des handlers pour résoudre la LoadException
    // Ces boutons sont logiquement gérés par AdminMENUController qui charge ce FXML dans son contentArea.
    // Ici, on les déclare pour que le FXML se charge, mais l_action réelle est dans AdminMENUController.
    @FXML
    void handleGererUtilisateurs(ActionEvent event) {
        // Ce FXML (admin_dashboard.fxml) EST la vue de gestion des utilisateurs.
        // Le bouton dans ce FXML ne devrait pas recharger lui-même.
        // Il est probable que ce bouton soit un reliquat ou une erreur de conception dans admin_dashboard.fxml
        // Si admin_dashboard.fxml est chargé par AdminMENUController via son propre bouton "Gérer Utilisateurs",
        // alors ce bouton interne à admin_dashboard.fxml est redondant.
        // Pour l_instant, on ne fait rien ou on affiche une alerte pour clarifier.
        // showAlert(Alert.AlertType.INFORMATION, "Info", "La vue de gestion des utilisateurs est déjà affichée.");
        System.out.println("handleGererUtilisateurs dans AdminDashboardController appelé. Normalement, cette vue est déjà la gestion des utilisateurs.");
    }

    @FXML
    void handleGererReclamations(ActionEvent event) {
        // La gestion des réclamations est gérée par AdminMENUController qui charge menuadmin.fxml
        // Ce bouton dans admin_dashboard.fxml ne devrait pas exister ou devrait appeler une méthode de AdminMENUController.
        // Pour l_instant, on affiche une alerte.
        showAlert(Alert.AlertType.INFORMATION, "Info", "La gestion des réclamations est accessible depuis le menu principal de l_administrateur.");
        System.out.println("handleGererReclamations dans AdminDashboardController appelé. Rediriger vers AdminMENUController si nécessaire.");
        // Idéalement, il faudrait une référence à AdminMENUController pour appeler sa méthode loadContent("/view/menuadmin.fxml")
        // ou supprimer ce bouton de admin_dashboard.fxml si la navigation se fait uniquement via AdminMENUController.
    }

    public void refreshUserList() {
        loadUsersData();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

