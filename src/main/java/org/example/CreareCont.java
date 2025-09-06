package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class CreareCont implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField cityField;


    @FXML
    private ComboBox<String> boxField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Adaugă opțiunile în ComboBox
        boxField.getItems().addAll("Admin", "Utilizator");

        // Opțional: setează o valoare default
        boxField.setValue("Utilizator");
    }

    @FXML private Button createButton;
    @FXML private Button backButton;

    private final DatabaseManager db = new DatabaseManager();

    @FXML
    private void handleBackButtonAction() throws Exception {
        JavaFXApp.setRoot("main");  // /fxml/main.fxml
    }

    @FXML
    private void handleCreateButtonAction() throws Exception {
        String u = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String p = passwordField.getText() == null ? "" : passwordField.getText();
        String e = emailField.getText() == null ? "" : emailField.getText();
        String c = cityField.getText() == null ? "" : cityField.getText();
        String statut = boxField.getValue() == null ? "" : boxField.getValue();

        if (u.isEmpty() || p.isEmpty() || e.isEmpty() || c.isEmpty() || statut.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Completeaza toate campurile");
            return;
        }

        String result = db.createUser(u, p, e, c, statut);

        switch (result) {
            case "USERNAME_EXISTS":
                alert(Alert.AlertType.ERROR, "Numele de utilizator '" + u + "' este deja folosit!");
                break;
            case "EMAIL_EXISTS":
                alert(Alert.AlertType.ERROR, "Adresa de email '" + e + "' este deja înregistrată!");
                break;
            case "ADMIN_CREATED":
                alert(Alert.AlertType.INFORMATION, "Cont administrator creat cu succes! Bun venit, " + u + "!");
                break;
            case "USER_CREATED":
                alert(Alert.AlertType.INFORMATION, "Cont utilizator creat cu succes! Salut, " + u + "!");
                break;
            case "ERROR":
            default:
                alert(Alert.AlertType.ERROR, "A apărut o eroare la crearea contului. Mai încearcă!");
                break;
        }
    }


    private void alert(Alert.AlertType type, String text) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }
}
