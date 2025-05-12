package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    private static Stage stg;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stg = primaryStage;
        primaryStage.setResizable(false);
        // Charger la nouvelle vue d_accueil initiale
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/welcome_screen.fxml")));
        primaryStage.setTitle("Bienvenue - Gestion de Vols");
        primaryStage.setScene(new Scene(root, 600, 400)); // Dimensions à ajuster si besoin pour welcome_screen
        primaryStage.show();
    }

    // Méthode pour changer de scène rendue statique
    public static void changeScene(String fxml, String title, int width, int height) throws IOException {
        Parent pane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(fxml))); // Utiliser Main.class pour le contexte statique
        stg.setTitle(title);
        stg.getScene().setRoot(pane);
        stg.setWidth(width);
        stg.setHeight(height);
        stg.centerOnScreen();
    }
    
    // Surcharge de la méthode changeScene pour accepter un Stage existant (utile si on ne veut pas dépendre de stg statique partout)
    // Mais pour l'instant, nous allons nous appuyer sur stg statique pour simplifier les appels depuis les contrôleurs.
    // public static void changeScene(Stage currentStage, String fxml, String title, int width, int height) throws IOException {
    //     Parent pane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(fxml)));
    //     currentStage.setTitle(title);
    //     currentStage.getScene().setRoot(pane);
    //     currentStage.setWidth(width);
    //     currentStage.setHeight(height);
    //     currentStage.centerOnScreen();
    // }


    public static void main(String[] args) {
        launch(args);
    }
}

