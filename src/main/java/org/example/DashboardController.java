package org.example;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.List;
public class DashboardController {
    @FXML private Button myButtonS;

    @FXML private TextField searchField;

    private GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();

    @FXML
    private void initialize() {
        // Enter la searchBar
        searchField.setOnAction(e -> handleSearch());
    }


    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            System.out.println("Introdu un termen de căutare!");
            return;
        }

        System.out.println("Căutare pentru: " + query);

        // Caută cărțile
        List<Book> books = googleBooksAPI.searchBooks(query);

        System.out.println("Găsite " + books.size() + " cărți:");
        for (Book book : books) {
            System.out.println("- " + book.getTitle() + " de " + book.getAuthors());
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


}
