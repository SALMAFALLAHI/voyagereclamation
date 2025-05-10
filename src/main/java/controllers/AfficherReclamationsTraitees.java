package controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import services.AdminService;
import entities.entiteReclamation;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class AfficherReclamationsTraitees {

    @FXML
    private TableColumn<entiteReclamation, String> contenuColumn;

    @FXML
    private TableColumn<entiteReclamation, String> dateColumn;

    @FXML
    private TableColumn<entiteReclamation, Integer> idColumn;

    @FXML
    private TableView<entiteReclamation> reclamationTable;

    @FXML
    private TableColumn<entiteReclamation, String> reponseColumn;

    @FXML
    private Label totalLabel;

    @FXML
    private TableColumn<entiteReclamation, String> typeColumn;

    @FXML
    private javafx.scene.control.Button retourButton;

    private final AdminService adminService = new AdminService();

    @FXML
    public void initialize() {
        try {
            ObservableList<entiteReclamation> data = FXCollections.observableArrayList(adminService.getAllTreatedReclamations());

            idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
            dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getDatedepublication())));
            contenuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContenu()));
            reponseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReponse()));

            reclamationTable.setItems(data);

            // Afficher le total
            totalLabel.setText("Total des réclamations traitées : " + data.size());
        } catch (SQLException e) {
            totalLabel.setText("Erreur de chargement !");
            e.printStackTrace();
        }
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "N/A";
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @FXML
    private void handleRetour() {
        // fermer la fenêtre actuelle
        retourButton.getScene().getWindow().hide();

    }
}
