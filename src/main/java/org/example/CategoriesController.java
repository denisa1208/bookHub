package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.List;

public class CategoriesController {

    @FXML private Label categoryTitle;
    @FXML private FlowPane booksContainer;

    private String currentUsername;


    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }


    private String getCurrentUsername() {
        return UserSession.getInstance().getCurrentUsername();
    }

    private GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();

    @FXML
    private void handleBack() {
        try {
            JavaFXApp.setRoot("dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCooking() {
        loadBooksForCategory("cooking recipes", "Cărți de Cooking");
    }

    @FXML
    private void handleProgramming() {
        loadBooksForCategory("programming computer science", "Cărți de Programming");
    }

    @FXML
    private void handleFiction() {
        loadBooksForCategory("fiction novels", "Cărți Fiction");
    }

    @FXML
    private void handleScience() {
        loadBooksForCategory("science physics chemistry", "Cărți Științifice");
    }

    @FXML
    private void handleHistory() {
        loadBooksForCategory("history world", "Cărți de Istorie");
    }

    @FXML
    private void handleSelfHelp() {
        loadBooksForCategory("self help personal development", "Cărți Self-Help");
    }

    private void loadBooksForCategory(String searchQuery, String categoryName) {
        categoryTitle.setText(categoryName);
        booksContainer.getChildren().clear();

        // Loading indicator
        Label loadingLabel = new Label("Se încarcă...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
        booksContainer.getChildren().add(loadingLabel);

        // Async search
        Task<List<Book>> searchTask = new Task<List<Book>>() {
            @Override
            protected List<Book> call() throws Exception {
                return googleBooksAPI.searchBooks(searchQuery, 12);
            }
        };

        searchTask.setOnSucceeded(e -> {
            List<Book> books = searchTask.getValue();
            Platform.runLater(() -> {
                booksContainer.getChildren().clear();

                for (Book book : books) {
                    VBox bookCard = createBookCard(book);
                    booksContainer.getChildren().add(bookCard);
                }
            });
        });

        new Thread(searchTask).start();
    }

    private VBox createBookCard(Book book) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-padding: 10; " +
                "-fx-background-radius: 8; -fx-alignment: center;");
        card.setPrefWidth(120);
        card.setPrefHeight(180);

        // Placeholder pentru imagine (pentru moment)
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


        card.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book-details.fxml"));
                Parent root = loader.load();

                BookDetailsController controller = loader.getController();
                controller.setBookData(book, getCurrentUsername());

                Stage stage = (Stage) card.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
                stage.setTitle("Detalii carte - " + book.getTitle());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("; -fx-scale-x: 1.05; -fx-scale-y: 1.05;", "")));

        return card;
    }


}