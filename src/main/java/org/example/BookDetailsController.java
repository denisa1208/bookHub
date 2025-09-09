package org.example;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class BookDetailsController implements Initializable {

    @FXML private Label descriptionLabel;
    @FXML private Button bckBtn;

    @FXML private Button addToLibraryBtn;
    @FXML private Button addToFavoritesBtn;
    @FXML private Label titleLabel;
    @FXML private Label authorsLabel;
    @FXML private Label publisherLabel;
    @FXML private Label ratingLabel;
    @FXML private Label pagesLabel;




    private Book currentBook;
    private String currentUsername;
    private DatabaseManager db = new DatabaseManager();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Placeholder text
        descriptionLabel.setWrapText(true);
    }


    @FXML
    private void handleBackCategories() {
        try {
            JavaFXApp.setRoot("categories");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackDashboard() {
        try {
            JavaFXApp.setRoot("dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddToLibrary() {
        if (currentBook == null || currentUsername == null) return;

        // Salvează cartea în baza de date
        if (db.saveBook(currentBook)) {
            long userId = db.getUserId(currentUsername);
            if (userId != -1 && db.addBookToUserLibrary(userId, currentBook.getId(), false)) {
                addToLibraryBtn.setText("În bibliotecă ✓");
                addToLibraryBtn.setDisable(true);
                showAlert("Succes", "Cartea a fost adăugată în biblioteca ta!");
            } else {
                showAlert("Eroare", "Nu s-a putut adăuga cartea în bibliotecă.");
            }
        }
    }

    @FXML
    private void handleAddToFavorites() {
        System.out.println(currentBook + " cartea " + currentUsername);
        if (currentBook == null || currentUsername == null) return;

        if (db.saveBook(currentBook)) {
            long userId = db.getUserId(currentUsername);
            if (userId != -1 && db.addBookToUserLibrary(userId, currentBook.getId(), true))  {
                addToFavoritesBtn.setText("Favorita");
                addToFavoritesBtn.setDisable(true);
                showAlert("Succes", "Cartea a fost adaugata la favorite cu succes");
            } else {
                showAlert("Eroare", "Nu s-a putut adauga cartea la favorite");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void setBookData(Book book, String username) {
        this.currentBook = book;
        this.currentUsername = UserSession.getInstance().getCurrentUsername();

        titleLabel.setText(book.getTitle());
        authorsLabel.setText("Autor: " + (book.getAuthors() != null ? book.getAuthors() : "Necunoscut"));
        publisherLabel.setText("Editura: " + (book.getPublisher() != null ? book.getPublisher() : "Necunoscută"));
        pagesLabel.setText("Pagini: " + (book.getPageCount() > 0 ? book.getPageCount() : "Necunoscut"));
        ratingLabel.setText("Rating: " + (book.getAverageRating() > 0 ? String.format("%.1f", book.getAverageRating()) : "Fără rating"));
        descriptionLabel.setText(book.getDescription() != null ? book.getDescription() : "Fără descriere disponibilă.");

        // Verifică dacă cartea este deja în bibliotecă
        long userId = db.getUserId(username);
        if (userId != -1 && db.isBookInUserLibrary(userId, book.getId())) {
            addToLibraryBtn.setText("În bibliotecă ✓");
            addToLibraryBtn.setDisable(true);
        }
    }

}
