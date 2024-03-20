package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainPageCtrl implements Controller, Initializable {

  @FXML
  private TextField createInput;
  @FXML
  private TextField joinInput;
  @FXML
  private ImageView flagImage;
  @FXML
  private ListView<EventHelper> recentList;

  private EventHelper selectedEv;
  //Imports used to swap scenes
  private Stage stage;
  private ServerUtils server;
  @Inject
  public MainPageCtrl(ServerUtils server){
    this.server = server;
  }

  public void createEvent(ActionEvent e) {
    System.out.println("Crete event window");
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    CreateEventCtrl createEventCtrl = new CreateEventCtrl(server);
    mainCtrl.initialize(stage, createEventCtrl.getPair(), createEventCtrl.getTitle());
  }

  public void joinEvent(ActionEvent event) {
    System.out.println("Join event window");
    System.out.println(joinInput.getText());
    try {
      server.connect(Long.parseLong(joinInput.getText()));
    }catch (Exception e){
      System.out.println(e);
      return;
    }
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
    mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
  }

  public void openAdmin(ActionEvent e){
    System.out.println("opening admin");
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    LoginAdminCtrl loginAdminCtrl = new LoginAdminCtrl(server);
    mainCtrl.initialize(stage, loginAdminCtrl.getPair(), loginAdminCtrl.getTitle());
  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    ArrayList<EventHelper> contents = new ArrayList<>();
    for(Event e : server.getEvents()){
      contents.add(new EventHelper(e.getEventId(), e.getName(), e.getCreationDate(), e.getActivityDate()));
    }
    contents.sort(new EventActivitySort());
    System.out.println(server.getEvents());
    recentList.getItems().addAll(contents);
    recentList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      selectedEv = recentList.getSelectionModel().getSelectedItem();

      EventPageCtrl eventCtrl = new EventPageCtrl(server);
      try {
        String input = String.valueOf(eventCtrl.findEventId(selectedEv.getTitle()));
        joinInput.setText(input);
      } catch (Exception e) {
        System.out.println(e);
      }
    });
  }

  public Pair<Controller, Parent> getPair() {
    return FXML.load(Controller.class, "client", "scenes", "mainPage.fxml");
  }
  public String getTitle(){
    return "Main Page";
  }
}
