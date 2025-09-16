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
    private void handleBack() {
        try {
            JavaFXApp.setRoot("my-books");
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
    private void handleDelete() {
        if (currentBook == null || currentUsername == null) return;

        long userId = db.getUserId(currentUsername);
        if (userId != -1 && db.deleteBookFromUserLibrary(userId, currentBook.getId())) {
            // Actualizează interfața după ștergere
            addToLibraryBtn.setText("Adaugă în bibliotecă");
            addToLibraryBtn.setDisable(false);
            showAlert("Succes", "Cartea a fost ștearsă din biblioteca ta!");
        } else {
            showAlert("Eroare", "Nu s-a putut șterge cartea din bibliotecă.");
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


    @FXML
    private void handleFavoriteAction() {
        if (currentBook == null || currentUsername == null) return;

        long userId = db.getUserId(currentUsername);
        if (userId == -1) return;

        boolean isFavorite = db.isBookFavorite(userId, currentBook.getId());

        if (!isFavorite) {
            // Șterge de la favorite
            if (db.removeFromFavorites(userId, currentBook.getId())) {
                updateFavoriteButton(false);
                showAlert("Succes", "Cartea a fost ștearsă de la favorite!");
            } else {
                showAlert("Eroare", "Nu s-a putut șterge cartea de la favorite.");
            }
        } else {
            // Adaugă la favorite
            if (db.saveBook(currentBook) && db.addBookToUserLibrary(userId, currentBook.getId(), true)) {
                updateFavoriteButton(true);
                showAlert("Succes", "Cartea a fost adăugată la favorite!");
            } else {
                showAlert("Eroare", "Nu s-a putut adăuga cartea la favorite.");
            }
        }
    }

    // Metodă helper pentru actualizarea butonului de favorite
    private void updateFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            addToFavoritesBtn.setText("Șterge de la favorite");
            addToFavoritesBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14;");
        } else {
            addToFavoritesBtn.setText("Adaugă la favorite");
            addToFavoritesBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14;");
        }
    }

    @FXML
    private void handleLibraryAction() {
        if (currentBook == null || currentUsername == null) return;

        long userId = db.getUserId(currentUsername);
        if (userId == -1) return;

        boolean isInLibrary = db.isBookInUserLibrary(userId, currentBook.getId());

        if (isInLibrary) {
            // Șterge din bibliotecă
            if (db.deleteBookFromUserLibrary(userId, currentBook.getId())) {
                updateLibraryButton(false);
                showAlert("Succes", "Cartea a fost ștearsă din biblioteca ta!");
            } else {
                showAlert("Eroare", "Nu s-a putut șterge cartea din bibliotecă.");
            }
        } else {
            // Adaugă în bibliotecă
            if (db.saveBook(currentBook) && db.addBookToUserLibrary(userId, currentBook.getId(), false)) {
                updateLibraryButton(true);
                showAlert("Succes", "Cartea a fost adăugată în biblioteca ta!");
            }
        }
    }

    private void updateLibraryButton(boolean isInLibrary) {
        if (isInLibrary) {
            addToLibraryBtn.setText("Șterge din biblioteca");
            addToLibraryBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14;");
        } else {
            addToLibraryBtn.setText("Adauga in biblioteca");
            addToLibraryBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14;");
        }
    }

}
