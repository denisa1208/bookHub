package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CreareCont {
    @FXML
    private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button backButton;

    private final DatabaseManager db = new DatabaseManager();

    @FXML
    private void handleBackButtonAction() throws Exception {
        JavaFXApp.setRoot("main");  // /fxml/main.fxml
    }

    @FXML
    private void handleRegister() {
        String u = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String p = passwordField.getText() == null ? "" : passwordField.getText();

        if (u.isEmpty() || p.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Completează utilizator și parolă.");
            return;
        }
        //saas

        boolean ok = db.authenticate(u, p);
        if (ok) {
            alert(Alert.AlertType.INFORMATION, "Autentificare reușită. Salut, " + u + "!");
            // aici poți schimba view-ul către un dashboard
        } else {
            alert(Alert.AlertType.ERROR, "Utilizator sau parolă greșite.");
        }
    }
//scdsz
    private void alert(Alert.AlertType type, String text) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }
}
