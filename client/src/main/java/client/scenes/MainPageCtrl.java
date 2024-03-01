package client.scenes;
import client.utils.ServerUtils;
import commons.Event;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainPageCtrl implements Initializable {

@FXML
private TextField createInput;
@FXML
private TextField joinInput;
@FXML
private ImageView flagImage;
@FXML
private ListView<String> recentList;

private final ServerUtils server;
private final MainCtrl mainCtrl;

private String selectedEv;
private Stage stage;
private Scene scene;
private Parent root;

@Inject
  public MainPageCtrl(ServerUtils server, MainCtrl mainCtrl) {
    this.server = server;
    this.mainCtrl = mainCtrl;
  }

  public void createEvent(ActionEvent e) {
    Event newEvent = new Event(createInput.getText());
    server.createEvent(newEvent);
    System.out.println("Crete event window");
    System.out.println(newEvent.toString());
    System.out.println(createInput.getText());
    /*    Creating a new event
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/event.fxml"));
    root = loader.load();
    EventCtrl eC = loader.getController();
    eC.passInput(createInput.getText());
    stage = (Stage)((Node) e.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
     */
  }
  public void joinEvent(ActionEvent e) {
    System.out.println("Join event window");
    System.out.println(joinInput.getText());

    /*    Joining an event
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/event.fxml"));
    root = loader.load();
    EventCtrl eC = loader.getController();
    eC.passInput(joinInput.getText());
    stage = (Stage)((Node) e.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
     */
  }
  public void openAdmin(ActionEvent e) throws IOException {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginAdmin.fxml"));
    root = loader.load();
    stage = (Stage)((Node) e.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();

  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    ArrayList<String> contents= new ArrayList<>();
    contents.add("New years");
    contents.add("Birthday");
    contents.add("Christmas");
    recentList.getItems().addAll(contents);
    recentList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        selectedEv = recentList.getSelectionModel().getSelectedItem();
        joinInput.setText(selectedEv);
      }
    });
  }
}
