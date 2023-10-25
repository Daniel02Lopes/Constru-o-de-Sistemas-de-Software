package control;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IndexControler {

  private Scene scene;
  private Stage stage;
  private Parent root;

  @FXML
  public void goToAtiv(ActionEvent event) throws IOException {

    System.out.println("Ativos");
    root = FXMLLoader.load(getClass().getResource("/paginas/projetosEmApoio.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void goToVot(ActionEvent event) throws IOException {
    System.out.println("Votação");
    root = FXMLLoader.load(getClass().getResource("/paginas/projetosEmVotacao.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
