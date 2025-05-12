package controllers;

import models.entiteReclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.json.JSONObject;
import services.AdminService;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Ajouterreponseadmin implements Initializable {

    @FXML private Button addButton;
    @FXML private Button cancelButton;
    @FXML private VBox contentPane;
    @FXML private Label headerLabel;
    @FXML private ListView<entiteReclamation> reclamationlist;
    @FXML private Label responseLabel;
    @FXML private TextArea responselabel;

    // EmailJS configuration - Replace with your actual values
    private static final String EMAILJS_SERVICE_ID = "service_axgxjnj";
    private static final String EMAILJS_TEMPLATE_ID = "template_fxhxc6x";
    private static final String EMAILJS_USER_ID = "Y6IKiF25RRhW4fJ5N";
    private static final String EMAILJS_API_URL = "https://api.emailjs.com/api/v1.0/email/send";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadReclamations();
        } catch (SQLException e) {
            showAlert("Error", "Loading Error",
                    "Failed to load claims: " + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        reclamationlist.setCellFactory(lv -> new ListCell<entiteReclamation>() {
            @Override
            protected void updateItem(entiteReclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);
                if (empty || reclamation == null) {
                    setText(null);
                } else {
                    setText(String.format("ID: %d | Type: %s | Email: %s | Content: %s",
                            reclamation.getId(),
                            reclamation.getType(),
                            reclamation.getAdresseEmail(),
                            reclamation.getContenu().length() > 50
                                    ? reclamation.getContenu().substring(0, 50) + "..."
                                    : reclamation.getContenu()));
                }
            }
        });
    }

    @FXML
    void ajouterreponse(ActionEvent event) {
        try {
            entiteReclamation selectedReclamation = reclamationlist.getSelectionModel().getSelectedItem();
            if (selectedReclamation == null) {
                showAlert("Error", "Selection Required",
                        "Please select a claim to process.",
                        Alert.AlertType.WARNING);
                return;
            }

            String reponse = responselabel.getText();
            if (reponse.isEmpty()) {
                showAlert("Error", "Empty Field",
                        "Please enter a response.",
                        Alert.AlertType.WARNING);
                return;
            }

            // Save to database
            AdminService adminService = new AdminService();
            adminService.repondreReclamation(selectedReclamation.getId(), reponse);

            // Send email
            boolean emailSent = sendEmailNotification(selectedReclamation, reponse);

            if (emailSent) {
                showAlert("Success", "Operation Complete",
                        "Response saved and email sent successfully.",
                        Alert.AlertType.INFORMATION);
            } else {
                showAlert("Warning", "Partial Success",
                        "Response saved but email failed to send.",
                        Alert.AlertType.WARNING);
            }

            loadReclamations();
            responselabel.clear();

        } catch (SQLException e) {
            showAlert("Error", "Database Error",
                    "Failed to save response: " + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "Unexpected Error",
                    "An error occurred: " + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean sendEmailNotification(entiteReclamation reclamation, String reponse) {
        try {
            System.out.println("[DEBUG] Preparing email to: " + reclamation.getAdresseEmail());

            JSONObject requestBody = new JSONObject();
            requestBody.put("service_id", EMAILJS_SERVICE_ID);
            requestBody.put("template_id", EMAILJS_TEMPLATE_ID);
            requestBody.put("user_id", EMAILJS_USER_ID);

            JSONObject templateParams = new JSONObject();
            templateParams.put("to_email", reclamation.getAdresseEmail());
            templateParams.put("reclamation_id", reclamation.getId());
            templateParams.put("reclamation_type", reclamation.getType());
            templateParams.put("reclamation_content", reclamation.getContenu());
            templateParams.put("admin_response", reponse);
            templateParams.put("from_name", "Support Team");

            requestBody.put("template_params", templateParams);

            System.out.println("[DEBUG] Request payload: " + requestBody.toString(2));

            HttpClient client = createCustomHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(EMAILJS_API_URL))
                    .header("Content-Type", "application/json")
                    .header("origin", "http://localhost")
                    .POST(BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("[DEBUG] Response status: " + response.statusCode());
            System.out.println("[DEBUG] Response body: " + response.body());

            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("Email sending error:");
            e.printStackTrace();
            return false;
        }
    }

    private HttpClient createCustomHttpClient() {
        try {
            // Bypass SSL verification (for development only)
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            }, new SecureRandom());

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();
        } catch (Exception e) {
            return HttpClient.newHttpClient();
        }
    }

    @FXML
    void annulerreponse(ActionEvent event) {
        reclamationlist.getSelectionModel().clearSelection();
        responselabel.clear();
        cancelButton.getScene().getWindow().hide();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadReclamations() throws SQLException {
        AdminService adminService = new AdminService();
        ObservableList<entiteReclamation> reclamations =
                FXCollections.observableArrayList(adminService.getReclamationsNonTraitees());
        reclamationlist.setItems(reclamations);
    }
}