package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Event;
import commons.ExpenseTest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.google.inject.Guice.createInjector;

public class AdminPageCtrl implements Controller, Initializable {

  @FXML
  private TableView<EventHelper> table;

  @FXML
  private TableColumn<EventHelper, Date> tableActivity;

  @FXML
  private TableColumn<EventHelper, Date> tableDate;

  @FXML
  private TableColumn<EventHelper, String> tableTitle;
  @FXML
  private Label showEvent;

  private EventHelper selectedEvent;
  //Imports used to swap scenes
  private static final Injector INJECTOR = createInjector(new MyModule());
  private static final MyFXML FXML = new MyFXML(INJECTOR);
  private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);
  private Stage stage;
  ServerUtils server;
  ObservableList<EventHelper> contents;
  @Inject
  public AdminPageCtrl(ServerUtils server) {
    this.server = server;
  }

  public void fillList(){
    List<EventHelper> list = new ArrayList<EventHelper>();
    for(Event e : server.getEvents()){
      System.out.println(e.getActivityDate());
      list.add(new EventHelper(e.getEventId(), e.getName(), e.getCreationDate(), e.getActivityDate()));
    }
    contents =  FXCollections.observableArrayList(
            list);
  }

  public void exportEvent(ActionEvent e) {
    System.out.println("export event to file");
  }
  public void importEvent(ActionEvent e) {
    System.out.println("import event from file");
  }

  public void editEvent(ActionEvent e){
    System.out.println("edit selected event");
  }

  public void deleteEvent(ActionEvent e){
    if(selectedEvent == null){
      System.out.println("No event selected!");
      return;
    }
    System.out.println("delete selected event");
    System.out.println(selectedEvent.getId());

    VBox layout = new VBox(10);
    Label label = new Label("Are you sure you want to remove the selected event?");
    Button removeButton = new Button("Remove");
    Button cancelButton = new Button("Cancel");

    // Set up the stage
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle("Remove Event");

    // This removes the entries from the file if pressed
    removeButton.setOnAction(event -> {
      popupStage.close();

      try {
        server.deleteEvent(selectedEvent.getId());
        contents.remove(selectedEvent);
        table.setItems(contents);
      }catch (Exception exception){
        System.out.println(exception);
      }
    });

    cancelButton.setOnAction(event -> {
      popupStage.close();
    });

    // Set up the layout
    layout.getChildren().addAll(label, removeButton, cancelButton);
    layout.setAlignment(Pos.CENTER);

    // Set the scene and show the stage
    Scene scene = new Scene(layout, 370, 150);
    popupStage.setScene(scene);
    popupStage.showAndWait();

  }
  public void close(ActionEvent e) throws IOException {
    System.out.println("close window");
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, MainPageCtrl.getPair(), MainPageCtrl.getTitle());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    fillList();
    tableTitle.setCellValueFactory(new PropertyValueFactory<EventHelper, String>("title"));
    tableActivity.setCellValueFactory(new PropertyValueFactory<EventHelper, Date>("lastActivity"));
    tableDate.setCellValueFactory(new PropertyValueFactory<EventHelper, Date>("creationDate"));
    table.setItems(contents);
    table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      selectedEvent = table.getSelectionModel().getSelectedItem();
      if(selectedEvent != null){
      showEvent.setText(selectedEvent.getTitle());
      }
      else{
        showEvent.setText("");
      }
    });
  }
  public static Pair<Controller, Parent> getPair() {
    return FXML.load(Controller.class, "client", "scenes", "adminPage.fxml");
  }
  public static String getTitle(){
    return "Admin Page";
  }
}

