package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Intro.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            String css = Objects.requireNonNull(this.getClass().getResource("application.css")).toExternalForm();
            scene.getStylesheets().add(css);

            stage.setTitle("Tic-Tac-Toe");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}