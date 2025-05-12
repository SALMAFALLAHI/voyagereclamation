package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import test.Main; // Assurez-vous que la classe Main est accessible et a la méthode changeScene statique
import java.io.IOException;

public class WelcomeScreenController {

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    void handleLoginAction(ActionEvent event) {
        try {
            // Appel direct à la méthode statique changeScene de la classe Main
            Main.changeScene("/view/login.fxml", "Système de Gestion de Vols - Connexion", 600, 400);
        } catch (IOException e) {
            // Gérer l_erreur, par exemple afficher une alerte
            System.err.println("Erreur lors du chargement de la page de connexion: " + e.getMessage());
            e.printStackTrace();
            // Idéalement, afficher une Alert à l_utilisateur
        }
    }

    @FXML
    void handleSignUpAction(ActionEvent event) {
        try {
            // Appel direct à la méthode statique changeScene de la classe Main
            Main.changeScene("/view/signup_form.fxml", "Inscription Utilisateur", 600, 500); // Dimensions à ajuster
        } catch (IOException e) {
            // Gérer l_erreur
            System.err.println("Erreur lors du chargement de la page d_inscription: " + e.getMessage());
            e.printStackTrace();
            // Idéalement, afficher une Alert à l_utilisateur
        }
    }
}

