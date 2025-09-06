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

    public static void main(String[] args) {
        launch(args);
    }
}
