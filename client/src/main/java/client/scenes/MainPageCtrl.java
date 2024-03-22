package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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

  public void createEvent(ActionEvent e){
    if (createInput.getText().equals("")){
      popup("Name can't be empty!");
      return;
    }
    Event newEvent = new Event(createInput.getText());
    for(Event event : server.getEvents()) {
      if (event.getName().equals(newEvent.getName())) {
        popup("Event already exists!");
        return;
      }
    }
    server.createEvent(newEvent);
    newEvent = server.getEvents().getLast();
    System.out.println(newEvent.getEventId() + "id");
    System.out.println("Crete event window");
    System.out.println(createInput.getText());
    System.out.println(server.getEvent(newEvent.getEventId()));
    EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
    server.connect(newEvent.getEventId());
    System.out.println(server.getCurrentId() + "ID cur");
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
  }

  private void popup(String text){
    VBox layout = new VBox(10);
    Label label = new Label(text);
    Button cancelButton = new Button("Cancel");

    // Set up the stage
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle("Warning!");

    cancelButton.setOnAction(e -> {
      popupStage.close();
    });

    // Set up the layout
    layout.getChildren().addAll(label, cancelButton);
    layout.setAlignment(Pos.CENTER);

    // Set the scene and show the stage
    Scene scene = new Scene(layout, 370, 150);
    popupStage.setScene(scene);
    popupStage.showAndWait();
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
