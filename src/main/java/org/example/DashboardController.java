package org.example;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button categoriesButton;
    @FXML private Button myButtonFav;
    @FXML private Button myButtonLibrary;
    @FXML private VBox resultsSection;
    @FXML private Label resultsLabel;
    @FXML private FlowPane booksContainer;

    private GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurează search field pentru Enter key
        searchField.setOnAction(e -> handleSearch());
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            showAlert("Eroare", "Introdu un termen de căutare!");
            return;
        }

        searchBooks(query);
    }

    private void searchBooks(String query) {
        // Afișează zona de rezultate
        resultsSection.setVisible(true);
        resultsLabel.setText("Se caută...");
        booksContainer.getChildren().clear();

        // Creează task pentru căutare (async)
        Task<List<Book>> searchTask = new Task<List<Book>>() {
            @Override
            protected List<Book> call() throws Exception {
                return googleBooksAPI.searchBooks(query, 12);
            }
        };

        searchTask.setOnSucceeded(e -> {
            List<Book> books = searchTask.getValue();
            Platform.runLater(() -> {
                resultsLabel.setText("Găsite " + books.size() + " rezultate pentru: \"" + query + "\"");
                booksContainer.getChildren().clear();

                for (Book book : books) {
                    VBox bookCard = createBookCard(book);
                    booksContainer.getChildren().add(bookCard);
                }
            });
        });

        searchTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                resultsLabel.setText("Eroare la căutare");
                showAlert("Eroare", "Nu s-au putut căuta cărțile. Verifică conexiunea la internet.");
            });
        });

        // Rulează task-ul în background
        new Thread(searchTask).start();
    }

    private VBox createBookCard(Book book) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-padding: 10; " +
                "-fx-background-radius: 8; -fx-alignment: center; -fx-cursor: hand;");
        card.setPrefWidth(120);
        card.setPrefHeight(180);

        // Placeholder pentru imagine
        Label imageLabel = new Label("📖");
        imageLabel.setStyle("-fx-font-size: 40; -fx-alignment: center;");

        // Titlu (scurtat)
        String title = book.getTitle();
        if (title.length() > 25) {
            title = title.substring(0, 22) + "...";
        }
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 10; -fx-font-weight: bold; -fx-text-alignment: center;");
        titleLabel.setWrapText(true);

        // Autor (scurtat)
        String author = book.getAuthors() != null ? book.getAuthors() : "Autor necunoscut";
        if (author.length() > 20) {
            author = author.substring(0, 17) + "...";
        }
        Label authorLabel = new Label(author);
        authorLabel.setStyle("-fx-font-size: 9; -fx-text-fill: #666;");

        card.getChildren().addAll(imageLabel, titleLabel, authorLabel);

        // Click event pentru detalii
        card.setOnMouseClicked(e -> openBookDetails(book));

        // Hover effects
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("; -fx-scale-x: 1.05; -fx-scale-y: 1.05;", "")));

        return card;
    }

    private void openBookDetails(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book-details.fxml"));
            Parent root = loader.load();

            BookDetailsController controller = loader.getController();
            controller.setBookData(book, null);

            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Detalii carte - " + book.getTitle());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleCategories() {
        try {
            JavaFXApp.setRoot("categories");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFavorites() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/my-books.fxml"));
            Parent root = loader.load();

            MyBooksController controller = loader.getController();
            controller.setFavoritesMode(true);

            Stage stage = (Stage) myButtonFav.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setTitle("Cărțile Favorite");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyBooks() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/my-books.fxml"));
            Parent root = loader.load();

            MyBooksController controller = loader.getController();
            controller.setFavoritesMode(false);

            Stage stage = (Stage) myButtonLibrary.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setTitle("Biblioteca Mea");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}