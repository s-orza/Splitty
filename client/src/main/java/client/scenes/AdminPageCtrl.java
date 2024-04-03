package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.Password;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import javafx.scene.control.*;
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
import java.time.LocalDate;
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
  private Button generatePassButton;

  @FXML
  private TextField passLengthField;

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

    server.registerForMessages("/topic/events", Long.class, e -> {
      for (int i = 0; i<contents.size(); i++) {
        if (contents.get(i).getId()==e) {
          contents.remove(i);
          break;
        }
      }
      table.setItems(contents);
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

        mainCtrl.popup("Exported succesfully to: \n" + filePath + fileName, "Success");
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
                mainCtrl.popup("Event with that name already exists!", "Warning!");
                return;
              }
            }
            Event createdEvent = new Event(newEvent.getName());
            createdEvent.setCreationDate(newEvent.getCreationDate());
            server.createEvent(createdEvent);
            long id = server.getEvents().getLast().getEventId();
            List<Participant> participants = newEvent.getParticipants();
            for(Participant p : participants){
              server.addParticipantEvent(p, id);
            }
            List<Tag> tags = newEvent.getTags();
            for(Tag t : tags){
              if(server.checkIfTagExists(t.getId().getName(), id))
              {
                System.out.println("Already in the database!");
              }
              else {
                server.addTag(new Tag(new TagId(t.getId().getName(),id),t.getColor()));
              }
            }

            List<Expense> expenses = newEvent.getExpenses();
            for(Expense e : expenses){
              server.addExpenseToEvent(id, new Expense(
                      e.getAuthor(),
                      e.getContent(),
                      e.getMoney(),
                      e.getCurrency(),
                      e.getDate(),
                      e.getParticipants(),
                      e.getType()));
            }

            List<Debt> debts = newEvent.getDebts();
            //here I used LocalDate.now() as the date because the date doesn't matter as every debt is a new debt and
            //we do not update anything
            for(Debt d : debts){
              server.addDebtToEvent(id, new Debt(d.getAmount(), d.getCurrency(), d.getDebtor(),
                      d.getCreditor()), LocalDate.now()+"");
            }

          }
          catch (Exception e){
            e.printStackTrace();
          }
        }
        else{
          mainCtrl.popup("Wrong file format! Please select a .json file", "Warning");
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
//        server.deleteEvent(selectedEvent.getId());
        server.sendEvent("/app/events", selectedEvent.getId());
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

    // add listener for pass length
    passLengthField.clear();
    passLengthField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("-?\\d?")) {
        passLengthField.setText(oldValue);
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

  public void generatePassword() throws Exception {
    if(passLengthField.getText().isEmpty()){
      server.deletePass(server.getPass().getPassID());
      server.addPassword(new Password());
      return;
    }
    int length = Integer.parseInt(passLengthField.getText());

    server.deletePass(server.getPass().getPassID());
    server.addPassword(new Password(length));
  }
}

