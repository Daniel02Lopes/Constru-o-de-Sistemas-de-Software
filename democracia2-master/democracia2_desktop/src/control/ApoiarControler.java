package control;

import Models.*;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ApoiarControler {

  private Scene scene;
  private Stage stage;
  private Parent root;
  private long id;
  @FXML private Text pJL;
  @FXML private Text titulo;
  @FXML private Text tema;
  @FXML private Text texto;

  @FXML
  public void goToAtiv(ActionEvent event) throws IOException {

    System.out.println("Ativos");
    root = FXMLLoader.load(getClass().getResource("/paginas/projetosEmApoio.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void setId(long id) {
    System.out.println("id");
    this.id = id;
    init();
  }

  @FXML
  public void sendApoiar(ActionEvent event) {
    try {
      URL url =
          new URL(
              "http://localhost:8080/api/projetos-de-lei-n-expirados/apoio/"
                  + this.id
                  + "/"
                  + LoginControler.ccAtual);
      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setDoOutput(true);
      int resposta = httpURLConnection.getResponseCode();
      System.out.println(resposta);
      if (resposta == 200) {
        System.out.println("index");
        root = FXMLLoader.load(getClass().getResource("/paginas/index.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  public void init() {
    System.out.println("correr");
    try {
      URL url = new URL("http://localhost:8080/api/projetos-de-lei-n-expirados/" + this.id);
      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setRequestMethod("GET");
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
      String s;
      StringBuilder resposta = new StringBuilder();
      while ((s = bufferedReader.readLine()) != null) {
        resposta.append(s);
      }
      bufferedReader.close();
      System.out.println(resposta);

      Gson gson = new Gson();
      ProjetoDeLeiDTO projetoDeLei = gson.fromJson(resposta.toString(), ProjetoDeLeiDTO.class);

      System.out.println(projetoDeLei.getId());
      StringBuilder sb = new StringBuilder(this.pJL.getText());
      sb.append(" " + projetoDeLei.getId());
      this.pJL.setText(sb.toString());

      StringBuilder sb1 = new StringBuilder(this.titulo.getText());
      sb1.append(" " + projetoDeLei.getTitulo());
      this.titulo.setText(sb1.toString());

      StringBuilder sb2 = new StringBuilder(this.tema.getText());
      sb2.append(" " + projetoDeLei.getTema().getTitulo());
      this.tema.setText(sb2.toString());

      StringBuilder sb3 = new StringBuilder(this.texto.getText());
      sb3.append(" " + projetoDeLei.getTextoDescriptivo());
      this.texto.setText(sb3.toString());

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @FXML
  public void voltar(ActionEvent event) throws IOException {
    root = FXMLLoader.load(getClass().getResource("/paginas/projetosEmApoio.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
