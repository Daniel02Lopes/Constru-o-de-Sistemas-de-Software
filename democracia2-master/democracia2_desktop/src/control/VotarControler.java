package control;

import Models.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class VotarControler {

  private Scene scene;
  private Stage stage;
  private Parent root;
  private long id;
  @FXML private Text votoOmissao;
  @FXML private ComboBox<String> comboBox;

  @FXML
  public void goToAtiv(ActionEvent event) throws IOException {

    System.out.println("Ativos");
    System.out.println("Votação");
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

  public void init() {
    comboBox.setItems(
        FXCollections.observableArrayList("FAVORAVEL", "DESFAVORAVEL", "POR_OMISSAO"));
    System.out.println("correr");
    try {
      URL url =
          new URL(
              "http://localhost:8080/api/votacao/" + this.id + "/votar/" + LoginControler.ccAtual);
      System.out.println(
          "http://localhost:8080/api/votacao/" + this.id + "/votar/" + LoginControler.ccAtual);
      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setRequestMethod("GET");
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
      String s;
      StringBuilder resposta = new StringBuilder();
      while ((s = reader.readLine()) != null) {
        resposta.append(s);
      }
      reader.close();
      System.out.println("Resposta");
      System.out.println(resposta);

      Gson gson = new Gson();

      JsonObject jsonObject = gson.fromJson(resposta.toString(), JsonObject.class);

      String votoPorOmissao = "";
      if (!jsonObject.get("votoPorOmissao").isJsonNull()) {
        votoPorOmissao = jsonObject.get("votoPorOmissao").getAsString();
      } else {
        votoPorOmissao = "Nao existe";
      }

      StringBuilder sb = new StringBuilder(this.votoOmissao.getText());
      System.out.println(this.votoOmissao.getText());
      sb.append(" " + votoPorOmissao);

      this.votoOmissao.setText(sb.toString());

    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println("Boas");
    }
  }

  @FXML
  public void voltar(ActionEvent event) throws IOException {
    root = FXMLLoader.load(getClass().getResource("/paginas/projetosEmVotacao.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void sendVoto(ActionEvent event) throws Exception {
    URL url =
        new URL(
            "http://localhost:8080/api/votacao/"
                + this.id
                + "/votar/"
                + LoginControler.ccAtual
                + "/"
                + comboBox.getValue());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("POST");
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
  }
}
