package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        setRoot("main"); // pornești în main.fxml
        primaryStage.setTitle("Biblioteca Mea - JavaFX");
        primaryStage.show();
    }

    public static void setRoot(String fxml) throws Exception {
        Parent root = FXMLLoader.load(JavaFXApp.class.getResource("/fxml/" + fxml + ".fxml"));
        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(root, 600, 400);
            // (opțional) CSS global:
            // scene.getStylesheets().add(JavaFXApp.class.getResource("/styles.css").toExternalForm());
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
