package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class Menureclamation {

    @FXML
    private Button btnafficher;

    @FXML
    private Button btnajouter;

    @FXML
    private Button btnmodifier;

    @FXML
    private Button btnsupprimer;

    private UserDashboardController userDashboardController;

    public void setUserDashboardController(UserDashboardController userDashboardController) {
        this.userDashboardController = userDashboardController;
    }

    private void loadReclamationView(String fxmlPath) {
        if (userDashboardController != null) {
            userDashboardController.loadNestedView(fxmlPath);
        } else {
            System.err.println("Menureclamation: UserDashboardController is not set. Cannot load view: " + fxmlPath);
            // Potentiellement afficher une alerte à l_utilisateur ici, bien que ce soit un problème de logique interne
        }
    }


    @FXML
    void afficherlesreclamation(ActionEvent event) {
        loadReclamationView("/view/afficherreclamation.fxml");
    }

    @FXML
    void ajouterunereclamation(ActionEvent event) {
        loadReclamationView("/view/ajouterreclamation.fxml");
    }

    @FXML
    void modifierunereclamation(ActionEvent event) {
        // Il faudra s_assurer que la vue de modification sait quelle réclamation modifier.
        // Cela pourrait nécessiter de passer des données à la vue de modification.
        // Pour l_instant, on charge simplement la vue.
        loadReclamationView("/view/modifierrecllamation.fxml");
    }

    @FXML
    void supprimerunereclamation(ActionEvent event) {
        // Similaire à la modification, il faudra identifier la réclamation à supprimer.
        loadReclamationView("/view/supprimerreclamation.fxml");
    }
}

