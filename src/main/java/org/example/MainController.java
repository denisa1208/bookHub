package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Button myButton;

    @FXML
    private Button myButton1;

    @FXML
    private void handleButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/autentificare.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) myButton.getScene().getWindow();

            Scene scene = new Scene(root, 500, 400);
            stage.setScene(scene);
            stage.setTitle("Autentificare");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("eroare la incarcarea paginii" + e.getMessage());
        }

    }
}