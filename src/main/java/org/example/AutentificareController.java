package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AutentificareController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button backButton;

    private final DatabaseManager db = new DatabaseManager();

    @FXML
    private void handleBackButtonAction() throws Exception {
        JavaFXApp.setRoot("main");  // /fxml/main.fxml
    }

    @FXML
    private void handleLogin() {
        String u = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String p = passwordField.getText() == null ? "" : passwordField.getText();

        if (u.isEmpty() || p.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Completează utilizator și parolă.");
            return;
        }

        boolean ok = db.authenticate(u, p);
        if (ok) {
            // Creează obiectul User
            User user = new User();
            user.setUsername(u);
            user.setId(db.getUserId(u));

            // Setează utilizatorul în sesiune
            UserSession.getInstance().setCurrentUser(user);

            goTo("/fxml/dashboard.fxml", "Dashboard");
        } else {
            alert(Alert.AlertType.ERROR, "Utilizator sau parolă greșite.");
        }
    }

    private void alert(Alert.AlertType type, String text) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }

    private void goTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Eroare la încărcarea paginii: " + e.getMessage());
        }
    }
}
