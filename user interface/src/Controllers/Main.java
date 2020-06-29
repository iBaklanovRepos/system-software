package Controllers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("MainWindow.fxml"));
        primaryStage.getIcons().add(new Image("resources/AppIcon.png"));
        primaryStage.setTitle("XML & CSV file processing");
        primaryStage.setScene(new Scene(root, 750, 600));
        primaryStage.show();
    }
}
