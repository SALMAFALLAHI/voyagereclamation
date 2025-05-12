package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static DatabaseUtil instance;
    private static final String URL = "jdbc:mysql://localhost:3306/flight_management_db"; // Mettez à jour si votre nom de BD est différent
    private static final String USER = "root"; // Mettez à jour avec votre utilisateur MySQL
    private static final String PASSWORD = ""; // Mettez à jour avec votre mot de passe MySQL. Laissez vide si pas de mot de passe pour root.

    private DatabaseUtil() {
        // Private constructor to prevent instantiation
    }

    public static DatabaseUtil getInstance() {
        if (instance == null) {
            instance = new DatabaseUtil();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            // Charger le driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL non trouvé. Assurez-vous que le connecteur MySQL est dans le classpath.");
            e.printStackTrace();
            throw new SQLException("Driver MySQL non trouvé", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        // Test de la connexion
        try (Connection conn = getInstance().getConnection()) {
            if (conn != null) {
                System.out.println("Connexion à la base de données établie avec succès !");
            } else {
                System.out.println("Échec de la connexion à la base de données.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

