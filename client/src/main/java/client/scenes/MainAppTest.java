package client.scenes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainAppTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/client/scenes/EventPage.fxml")));
        primaryStage.setTitle("FXML Example");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
