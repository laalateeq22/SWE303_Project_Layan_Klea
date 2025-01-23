package Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML file directly
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/HomeFormView.fxml"));
        Scene scene = new Scene(loader.load());

        // Configure and display the stage
        stage.setScene(scene);
        stage.setTitle("TestFX System Test");
        stage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}


