package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MyBooksController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ListView<Book> booksListView;
    @FXML private Label countLabel;

    private DatabaseManager db = new DatabaseManager();
    private boolean isFavoritesPage = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupBooksListView();
        loadUserBooks();
    }

    public void setFavoritesMode(boolean isFavorites) {
        this.isFavoritesPage = isFavorites;
        if (isFavorites) {
            titleLabel.setText("Favorites");
            loadFavoriteBooks();
        } else {
            titleLabel.setText("My Books");
            loadUserBooks();
        }
    }

    private void setupBooksListView() {
        booksListView.setCellFactory(listView -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);

                if (empty || book == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(createBookListItem(book));
                    setText(null);
                }
            }
        });
    }

    private void loadUserBooks() {
        long userId = UserSession.getInstance().getCurrentUserId();
        if (userId != -1) {
            List<Book> books = db.getUserBooks(userId);
            booksListView.getItems().clear();
            booksListView.getItems().addAll(books);
            countLabel.setText(books.size() + " cărți în bibliotecă");
        }
    }

    private VBox createBookListItem(Book book) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-padding: 15; -fx-cursor: hand;");
        card.setPrefWidth(500);

        // Titlu
        Label titleLabel = new Label(book.getTitle());
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setWrapText(true);

        // Autor și Publisher
        String authorText = book.getAuthors() != null ? "de " + book.getAuthors() : "Autor necunoscut";
        if (book.getPublisher() != null && !book.getPublisher().isEmpty()) {
            authorText += " • " + book.getPublisher();
        }
        Label authorLabel = new Label(authorText);
        authorLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #6c757d;");

        // Info suplimentare
        HBox infoBox = new HBox(15);
        if (book.getPageCount() > 0) {
            Label pagesLabel = new Label(book.getPageCount() + " pagini");
            pagesLabel.setStyle("-fx-text-fill: #868e96; -fx-font-size: 10;");
            infoBox.getChildren().add(pagesLabel);
        }

        if (book.getAverageRating() > 0) {
            Label ratingLabel = new Label("Rating: " + String.format("%.1f", book.getAverageRating()));
            ratingLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 10; -fx-font-weight: bold;");
            infoBox.getChildren().add(ratingLabel);
        }

        card.getChildren().addAll(titleLabel, authorLabel, infoBox);

        // Click pentru detalii
        card.setOnMouseClicked(e -> openBookDetails(book));

        return card;
    }

    private void openBookDetails(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book-details.fxml"));
            Parent root = loader.load();

            BookDetailsController controller = loader.getController();
            controller.setBookData(book, null); // Username se va lua din UserSession

            Stage stage = (Stage) booksListView.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Detalii carte - " + book.getTitle());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void loadFavoriteBooks() {
        long userId = UserSession.getInstance().getCurrentUserId();
        if (userId != -1) {
            List<Book> books = db.getUserFavoriteBooks(userId);
            booksListView.getItems().clear();
            booksListView.getItems().addAll(books);
            countLabel.setText(books.size() + " cărți favorite");
        }
    }

    @FXML
    private void handleBack() {
        try {
            JavaFXApp.setRoot("dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
