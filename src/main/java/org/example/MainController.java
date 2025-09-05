package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {

    @FXML private Button myButton;   // Autentificare
    @FXML private Button myButton1;  // Creare cont (deocamdată tot spre autentificare)

    @FXML
    private void handleAuthClick() {
        goTo("/fxml/autentificare.fxml", "Autentificare");
    }

    @FXML
    private void handleCreateClick() {
        // Deocamdată mergem tot la aceeași pagină. Poți face alt FXML pentru înregistrare.
        goTo("/fxml/autentificare.fxml", "Autentificare");
    }

    private void goTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) myButton.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Eroare la încărcarea paginii: " + e.getMessage());
        }
    }
}
