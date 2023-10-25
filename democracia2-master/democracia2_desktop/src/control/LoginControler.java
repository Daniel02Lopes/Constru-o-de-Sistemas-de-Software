package control;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginControler {
  public static long ccAtual;
  @FXML private TextField cc;
  private Parent root;
  private Stage stage;
  private Scene scene;
  @FXML private TextField textField;
  @FXML private Text errorText;

  @FXML
  public void login(ActionEvent event) throws IOException {

    ccAtual = Long.parseLong(textField.getText());
    URL url = new URL("http://localhost:8080/api/login/" + ccAtual);
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("POST");
    int response = httpURLConnection.getResponseCode();
    System.out.println(response);

    int resposta = httpURLConnection.getResponseCode();
    System.out.println(resposta);
    if (resposta == HttpURLConnection.HTTP_OK) {
      root = FXMLLoader.load(getClass().getResource("/paginas/index.fxml"));
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
    }
    StringBuilder sb = new StringBuilder();
    sb.append("Error");
    errorText.setText(sb.toString());
  }
}
