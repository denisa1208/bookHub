package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

import java.util.Objects;

public class JavaFXApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        setRoot("main");
        primaryStage.setTitle("Biblioteca Mea - JavaFX");

        //min size
        primaryStage.setMinWidth(50);
        primaryStage.setMinHeight(50);

        //enable fullscrean and maximize
        primaryStage.setMaximized(false);
        primaryStage.setResizable(true);

        primaryStage.show();
    }

    public static void setRoot(String fxml) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(JavaFXApp.class.getResource("/fxml/" + fxml + ".fxml")));
        Scene scene = primaryStage.getScene();

        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();
        boolean isMaximized = primaryStage.isMaximized();
        boolean isFullScreen = primaryStage.isFullScreen();

        if (scene == null) {
            scene = new Scene(root, 50, 50);
            // (opțional) CSS global:
            scene.getStylesheets().add(Objects.requireNonNull(JavaFXApp.class.getResource("/styles.css")).toExternalForm());
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(root);
            primaryStage.setWidth(50);
            primaryStage.setHeight(50);
            String cssPath = Objects.requireNonNull(JavaFXApp.class.getResource("/styles.css")).toExternalForm();
            if (!scene.getStylesheets().contains(cssPath)) {
                scene.getStylesheets().add(cssPath);
            }
        }
        //scene.getStylesheets().clear();
        //scene.getStylesheets().add(Objects.requireNonNull(JavaFXApp.class.getResource("/styles.css")).toExternalForm());
        if (isFullScreen) {
            primaryStage.setFullScreen(true);
        } else if (isMaximized) {
            primaryStage.setMaximized(true);
        } else {
            primaryStage.setWidth(currentWidth);
            primaryStage.setHeight(currentHeight);
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
