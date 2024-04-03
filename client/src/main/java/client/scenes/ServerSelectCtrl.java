package client.scenes;

import client.utils.ServerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import static client.scenes.MainPageCtrl.currentLocale;


public class ServerSelectCtrl implements Controller, Initializable {
  @FXML
  private Label serverText;

  @FXML
  private TextField loginInput;

  @FXML
  private Button loginButton;

  //Imports used to swap scenes
  private Stage stage;
  private ServerUtils server;

  @Inject
  public ServerSelectCtrl(ServerUtils server) {
    this.server = server;
    //this.loginInput.setText(ServerUtils.getSERVER());
  }

  public void login(ActionEvent e){
    String passcode = loginInput.getText();
    try{
      ServerUtils.setServerUrl(passcode);
    }catch (Exception exception){
        loginInput.setStyle("-fx-background-color: #FF999C;");
        loginInput.setText("");
        serverText.setText("Invalid url");
        return;
    }
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    MainPageCtrl mainPageCtrl = new MainPageCtrl(server);
    mainCtrl.initialize(stage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());
  }

  public Pair<Controller, Parent> getPair() {
    return FXML.load(Controller.class, "client", "scenes", "ServerSelect.fxml");
  }

  @Override
  public String getTitle() {
    return "Server Page";
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
    loginButton.setText(resourceBundle.getString("loginText"));
    loginInput.setText(mainCtrl.getUrl());
  }
}