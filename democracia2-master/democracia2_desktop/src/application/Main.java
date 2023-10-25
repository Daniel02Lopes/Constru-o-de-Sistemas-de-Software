package application;

import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("/paginas/login.fxml"));
    String css = this.getClass().getResource("application.css").toExternalForm();
    primaryStage.setTitle("Democracia2");
    primaryStage.setResizable(false);
    Scene scene = new Scene(root, 600, 400);
    scene.getStylesheets().add(css);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
