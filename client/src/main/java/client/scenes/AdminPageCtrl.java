package client.scenes;

import client.utils.ServerUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@JsonIgnoreProperties(ignoreUnknown= true)
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
    event.setTags(tags);
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
  public void importEvent(ActionEvent actionEvent) {
    FileChooser fc = new FileChooser();
    String filePath = new File("").getAbsolutePath().replace("\\", "/");
    filePath += ("/EventsBackup/");
    fc.setInitialDirectory(new File(filePath));
    File selectedFile = fc.showOpenDialog(null);
    if(selectedFile !=null){
        if(selectedFile.getName().contains(".json") ){
          ObjectMapper mapper = new ObjectMapper();
          try {
            System.out.println("This file" + new String(Files.readAllBytes(Paths.get(selectedFile.toURI()))));
            Event newEvent = mapper.readValue(selectedFile, Event.class);
            System.out.println(newEvent);
            for(Event event : server.getEvents()) {
              if (event.getName().equals(newEvent.getName())) {
                mainCtrl.popup("Event with that name already exists!");
                return;
              }
            }
            server.createEvent(new Event(newEvent.getName()));
            long id = server.getEvents().getLast().getEventId();
            List<Participant> participants = newEvent.getParticipants();
            for(Participant p : participants){
              server.addParticipantEvent(p, id);
            }
            List<Expense> expenses = newEvent.getExpenses();
            for(Expense e : expenses){
              server.addExpenseToEvent(id, e);
            }
            /*
            List<Debt> debts = newEvent.getDebts();
            for(Debt d : debts){
              server.addDebtToEvent(id, d);
            }
             */





          }
          catch (Exception e){
            e.printStackTrace();
          }
        }
        else{
          popup("Wrong file format! Please select a .json file");
          return;
        }
    }
    else {
        return;
    }
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

  public Pair<Controller, Parent> getPair() {
    return FXML.load(Controller.class, "client", "scenes", "adminPage.fxml");
  }
  public String getTitle(){
    return "Admin Page";
  }
}

