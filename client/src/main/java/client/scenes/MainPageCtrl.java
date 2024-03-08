package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.google.inject.Injector;
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

import static com.google.inject.Guice.createInjector;

public class MainPageCtrl implements Controller, Initializable {

  @FXML
  private TextField createInput;
  @FXML
  private TextField joinInput;
  @FXML
  private ImageView flagImage;
  @FXML
  private ListView<String> recentList;

  private String selectedEv;
  //Imports used to swap scenes
  private Stage stage;
  private static final Injector INJECTOR = createInjector(new MyModule());
  private static final MyFXML FXML = new MyFXML(INJECTOR);

  private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);
  private ServerUtils server;
  @Inject
  public MainPageCtrl(ServerUtils server){
    this.server = server;
  }

  public void createEvent(ActionEvent e) {
    System.out.println("Crete event window");
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, CreateEventCtrl.getPair(), CreateEventCtrl.getTitle());
  }

  public void joinEvent(ActionEvent e) {
    System.out.println("Join event window");
    System.out.println(joinInput.getText());
    //EventPageCtrl eventCtrl = new EventPageCtrl(server);
    //eventCtrl.connectEvent(server.getEvent(Long.parseLong(joinInput.getText())));
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, EventPageCtrl.getPair(), EventPageCtrl.getTitle());
  }

  public void openAdmin(ActionEvent e){
    System.out.println("opening admin");
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, LoginAdminCtrl.getPair(), LoginAdminCtrl.getTitle());
  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    ArrayList<String> contents = new ArrayList<>();
    contents.add("New years");
    contents.add("Birthday");
    contents.add("Christmas");
    System.out.println(server.getEvents());
    recentList.getItems().addAll(contents);
    recentList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      selectedEv = recentList.getSelectionModel().getSelectedItem();
      joinInput.setText(selectedEv);
    });
  }

  public static Pair<Controller, Parent> getPair() {
    return FXML.load(Controller.class, "client", "scenes", "mainPage.fxml");
  }
  public static String getTitle(){
    return "Main Page";
  }
}
