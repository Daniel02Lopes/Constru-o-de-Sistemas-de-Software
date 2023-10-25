package control;

import Models.ProjetoDeLeiDTO;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ProjetosEmApoioControler implements Initializable {

  private Scene scene;
  private Stage stage;
  private Parent root;

  private List<ProjetoDeLeiDTO> projetos;

  @FXML private TableView<ProjetoDeLeiDTO> table;
  @FXML private TableColumn<ProjetoDeLeiDTO, Long> id;
  @FXML private TableColumn<ProjetoDeLeiDTO, String> titulo;

  @FXML private Text text;
  @FXML private TextField textField;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    id.setCellValueFactory(a -> new SimpleObjectProperty<>(a.getValue().getId()));
    titulo.setCellValueFactory(a -> new SimpleObjectProperty<>(a.getValue().getTitulo()));

    try {
      URL url = new URL("http://localhost:8080/api/projetos-de-lei-n-expirados");
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
      ProjetoDeLeiDTO[] projetoDeLeiArray =
          gson.fromJson(resposta.toString(), ProjetoDeLeiDTO[].class);
      projetos = Arrays.asList(projetoDeLeiArray);
      ObservableList<ProjetoDeLeiDTO> projetosParaTable =
          FXCollections.observableArrayList(projetos);
      table.setItems(projetosParaTable);

      System.out.println("Acabou");
    } catch (Exception e) {

    }
  }

  public void goToProj(ActionEvent event) throws IOException {
    boolean found = false;
    for (ProjetoDeLeiDTO a : projetos) {
      if (a.getId() == Long.parseLong(textField.getText())) {
        found = true;
      }
    }
    if (found) {
      System.out.println("redirect");
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/paginas/apoiar.fxml"));
      root = loader.load();
      ApoiarControler apoiarControler = loader.getController();
      apoiarControler.setId(Long.parseLong(textField.getText()));
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.show();

    } else {
      StringBuilder sb = new StringBuilder();
      sb.append("Id: id nao existe!!");
      this.text.setText(sb.toString());
    }
  }

  @FXML
  public void voltar(ActionEvent event) throws IOException {
    root = FXMLLoader.load(getClass().getResource("/paginas/index.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
