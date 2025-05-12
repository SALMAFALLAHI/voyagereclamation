package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.Users;
import test.Main; // Pour l_appel statique à changeScene

import java.io.IOException;

public class UserDashboardController {

    @FXML
    private Label welcomeUserLabel;

    @FXML
    private Button btnGestionCompte;

    @FXML
    private Button btnGestionVols;

    @FXML
    private Button btnGestionHebergements;

    @FXML
    private Button btnGestionReservations;

    @FXML
    private Button btnGestionExcursions;

    @FXML
    private Button btnGestionAvis;

    @FXML
    private Button btnLogoutUser;

    @FXML
    private StackPane userMainContentArea;

    @FXML
    private VBox userPlaceholderView;
    
    @FXML
    private Label userPlaceholderLabel;

    private Users currentUser;

    @FXML
    public void initialize() {
        if (welcomeUserLabel == null || userMainContentArea == null || userPlaceholderView == null || userPlaceholderLabel == null) {
            System.err.println("UserDashboardController: Critical FXML elements not injected. Dashboard cannot function.");
            showAlert(Alert.AlertType.ERROR, "Erreur Critique", "Le tableau de bord n_a pas pu être initialisé correctement. Composants manquants.");
            try {
                Main.changeScene("/view/login.fxml", "Système de Gestion de Vols - Connexion", 600, 400);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        this.currentUser = LoginController.getCurrentUser();

        if (this.currentUser != null) {
            welcomeUserLabel.setText("Bienvenue, " + this.currentUser.getPrenom() + " " + this.currentUser.getNom() + " !");
            showPlaceholderView("Bienvenue sur votre espace personnel. Sélectionnez une option dans le menu de gauche.");
        } else {
            welcomeUserLabel.setText("Utilisateur non identifié.");
            System.err.println("UserDashboardController: currentUser is null. Cannot display user-specific info.");
            showAlert(Alert.AlertType.ERROR, "Erreur de Session", "Impossible de récupérer les informations de l_utilisateur. Veuillez vous reconnecter.");
            disableDashboardControls(true);
            showPlaceholderView("Erreur de session. Veuillez vous reconnecter.");
            try {
                Main.changeScene("/view/login.fxml", "Système de Gestion de Vols - Connexion", 600, 400);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void disableDashboardControls(boolean disable) {
        if (btnGestionCompte != null) btnGestionCompte.setDisable(disable);
        if (btnGestionVols != null) btnGestionVols.setDisable(disable);
        if (btnGestionHebergements != null) btnGestionHebergements.setDisable(disable);
        if (btnGestionReservations != null) btnGestionReservations.setDisable(disable);
        if (btnGestionExcursions != null) btnGestionExcursions.setDisable(disable);
        if (btnGestionAvis != null) btnGestionAvis.setDisable(disable);
    }

    private void clearMainContentArea() {
        if (userMainContentArea != null) {
            userMainContentArea.getChildren().clear();
        }
    }

    private void showPlaceholderView(String message) {
        clearMainContentArea();
        if (userPlaceholderLabel != null && userMainContentArea != null && userPlaceholderView != null) {
            userPlaceholderLabel.setText(message);
            if (!userMainContentArea.getChildren().contains(userPlaceholderView)) {
                 userMainContentArea.getChildren().add(userPlaceholderView);
            }
            userPlaceholderView.setVisible(true);
        } else {
            System.err.println("UserDashboardController: One or more FXML elements for placeholder view are null during showPlaceholderView.");
        }
    }

    // Méthode renommée pour clarifier son usage interne et externe
    public void loadNestedView(String fxmlPath) {
        if (this.currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Session", "Session utilisateur invalide. Veuillez vous reconnecter.");
            try {
                Main.changeScene("/view/login.fxml", "Système de Gestion de Vols - Connexion", 600, 400);
            } catch (IOException e) { e.printStackTrace(); }
            return;
        }
        try {
            clearMainContentArea();
            if (userMainContentArea == null) {
                 System.err.println("UserDashboardController: userMainContentArea is null, cannot load view.");
                 showPlaceholderView("Erreur critique : La zone de contenu principal n_est pas initialisée.");
                 return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            userMainContentArea.getChildren().add(view);
            if (userPlaceholderView != null) {
                userPlaceholderView.setVisible(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de Chargement", "Impossible de charger la vue : " + fxmlPath + "\n" + e.getMessage());
            showPlaceholderView("Erreur lors du chargement de la section demandée.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur Critique", "Un composant FXML nécessaire est manquant ou non initialisé pour charger : " + fxmlPath);
            showPlaceholderView("Erreur critique lors du chargement de la section.");
        }
    }

    @FXML
    void handleGestionCompte(ActionEvent event) {
        loadNestedView("/view/user_profile.fxml");
    }

    @FXML
    void handleGestionVols(ActionEvent event) {
        showPlaceholderView("La section Gestion des Vols sera bientôt disponible.\nVos collègues intégreront cette fonctionnalité ici.");
    }

    @FXML
    void handleGestionHebergements(ActionEvent event) {
        showPlaceholderView("La section Gestion d_Hébergement sera bientôt disponible.\nVos collègues intégreront cette fonctionnalité ici.");
    }

    @FXML
    void handleGestionReservations(ActionEvent event) {
        showPlaceholderView("La section Gestion de Réservation sera bientôt disponible.\nVos collègues intégreront cette fonctionnalité ici.");
    }

    @FXML
    void handleGestionExcursions(ActionEvent event) {
        showPlaceholderView("La section Gestion d_Excursion sera bientôt disponible.\nVos collègues intégreront cette fonctionnalité ici.");
    }

    @FXML
    void handleGestionAvis(ActionEvent event) {
        try {
            clearMainContentArea();
            if (userMainContentArea == null) {
                 System.err.println("UserDashboardController: userMainContentArea is null, cannot load view for Avis.");
                 showPlaceholderView("Erreur critique : La zone de contenu principal n_est pas initialisée.");
                 return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/menu.fxml"));
            Parent view = loader.load();

            Menureclamation menureclamationController = loader.getController();
            if (menureclamationController != null) {
                menureclamationController.setUserDashboardController(this);
            } else {
                 System.err.println("UserDashboardController: Menureclamation controller not found after loading menu.fxml");
                 showAlert(Alert.AlertType.ERROR, "Erreur Critique", "Impossible de charger le module de réclamation correctement.");
                 showPlaceholderView("Erreur critique lors du chargement du module de réclamation.");
                 return;
            }

            userMainContentArea.getChildren().add(view);
            if (userPlaceholderView != null) {
                userPlaceholderView.setVisible(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de Chargement", "Impossible de charger la vue : /view/menu.fxml" + "\n" + e.getMessage());
            showPlaceholderView("Erreur lors du chargement de la section demandée.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur Critique", "Un composant FXML nécessaire est manquant ou non initialisé pour charger : /view/menu.fxml");
            showPlaceholderView("Erreur critique lors du chargement de la section.");
        }
    }

    @FXML
    void handleLogoutUserAction(ActionEvent event) {
        LoginController.setCurrentUser(null);
        this.currentUser = null;
        try {
            Main.changeScene("/view/login.fxml", "Système de Gestion de Vols - Connexion", 600, 400);
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

