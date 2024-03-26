package client.scenes;

import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileOutputStream;
import java.io.IOException;
import commons.*;
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
import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

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
      System.out.println("activity date: " + e.getActivityDate());
      list.add(new EventHelper(e.getEventId(), e.getName(), e.getCreationDate(), e.getActivityDate()));
    }
    contents =  FXCollections.observableArrayList(
            list);
    server.registerForUpdatesEvents(server.getCurrentId(), e -> {
      boolean alreadyExists = false;
      for (EventHelper event: contents) {
        if (event.getTitle().equals(e.getName())){
          alreadyExists = true;
        }
      }
      if (!alreadyExists){
        contents.add(new EventHelper(e.getEventId(), e.getName(), e.getCreationDate(), e.getActivityDate()));
      }
    });
  }

  public void exportEvent(ActionEvent e) {
    Event event = server.getEvent(selectedEvent.getId());
    List<Expense> expenses = server.getAllExpensesOfEvent(event.getEventId());
    List<Participant> participants = server.getParticipantsOfEvent(event.getEventId());
    List<Tag> tags= server.getAllTagsFromEvent(event.getEventId());
    List<Debt> debts = event.debts;
    event.setExpenses(expenses);
    event.setParticipants(participants);
    System.out.println(event + " \nTags " + tags);
    ObjectMapper mapper = new ObjectMapper();
    StringWriter writer = new StringWriter();
    try{
        mapper.writeValue(writer, event);
        String json  = writer.toString();
        System.out.println(json);
        String filePath = new File("").getAbsolutePath().replace("\\", "/");
        filePath += ("/EventsBackup/");
        String fileName = event.getName() + ".json";
        //Open file
        // FileOutputStream Class Used
        FileOutputStream fileOutputStream = new FileOutputStream(filePath + fileName);
        // Write data to the file if needed.
        fileOutputStream.write(json.getBytes());
        //Close file
        fileOutputStream.close();

        System.out.println("Exported succesfully to " + filePath + fileName);
      }
    catch(Exception exception){
      System.out.println(exception);
    }
  }
  public void importEvent(ActionEvent e) {
    System.out.println("import event from file");
  }

  public void editEvent(ActionEvent e){
    System.out.println("edit selected event");
    EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
    server.connect(selectedEvent.getId());
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
  }

  public void deleteEvent(ActionEvent e){
    if(selectedEvent == null){
      System.out.println("No event selected!");
      return;
    }
    System.out.println("delete selected event with id :");
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
    MainPageCtrl mainPageCtrl = new MainPageCtrl(server);
    mainCtrl.initialize(stage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());
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
  public void stop () {
    server.stop2();
  }
  public Pair<Controller, Parent> getPair() {
    return FXML.load(Controller.class, "client", "scenes", "adminPage.fxml");
  }
  public String getTitle(){
    return "Admin Page";
  }
}

