module TestApp {
  exports Models;
  exports control;

  requires javafx.controls;
  requires javafx.base;
  requires javafx.graphics;
  requires javafx.fxml;
  requires java.net.http;
  requires com.google.gson;

  opens Models;
  opens control to
      javafx.fxml;
  opens application to
      javafx.graphics,
      javafx.fxml,
      com.google.gson,
      javafx.base;
}
