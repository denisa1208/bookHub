package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class JavaFXApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        setRoot("main"); // pornești în main.fxml
        primaryStage.setTitle("Biblioteca Mea - JavaFX");

        //min size
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        //enable fullscrean and maximize
        primaryStage.setMaximized(false);
        primaryStage.setResizable(true);

        primaryStage.show();
    }

    public static void setRoot(String fxml) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(JavaFXApp.class.getResource("/fxml/main.fxml")));        //Parent root = FXMLLoader.load(JavaFXApp.class.getResource("/fxml/" + fxml + ".fxml"));
        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(root, 900, 650);
            // (opțional) CSS global:
            scene.getStylesheets().add(Objects.requireNonNull(JavaFXApp.class.getResource("/styles.css")).toExternalForm());
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(root);
        }

        scene.getStylesheets().clear();
        scene.getStylesheets().add(Objects.requireNonNull(JavaFXApp.class.getResource("/styles.css")).toExternalForm());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
