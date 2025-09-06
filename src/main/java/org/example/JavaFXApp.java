package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class JavaFXApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        setRoot("main");
        primaryStage.setTitle("Biblioteca Mea - JavaFX");
        primaryStage.show();
    }

    public static void setRoot(String fxml) throws Exception {
        Parent root = FXMLLoader.load(JavaFXApp.class.getResource("/fxml/" + fxml + ".fxml"));
        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(root, 600, 400);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
    }

    // Metodă pentru testarea API-ului
    private static void testGoogleBooksAPI() {
        System.out.println("=== Test Google Books API ===");

        GoogleBooksAPI googleAPI = new GoogleBooksAPI();

        // Căutare simplă
        List<Book> books = googleAPI.searchBooks("Harry Potter");
        System.out.println("Harry Potter - găsite " + books.size() + " cărți:");
        for (int i = 0; i < Math.min(3, books.size()); i++) {
            System.out.println("- " + books.get(i).getDisplayText());
        }

        // Căutare cu număr limitat de rezultate
        List<Book> limitedBooks = googleAPI.searchBooks("cooking", 5);
        System.out.println("\nProgramming - găsite " + limitedBooks.size() + " cărți:");
        for (Book book : limitedBooks) {
            System.out.println("- " + book.getDisplayText());
        }

        System.out.println("=== Sfârșit test ===\n");
    }

    public static void main(String[] args) {
        // Testează API-ul înainte de a porni interfața
        testGoogleBooksAPI(); // Decomentează pentru test

        launch(args);
    }
}