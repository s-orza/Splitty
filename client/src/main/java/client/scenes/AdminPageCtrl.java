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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
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

  @FXML
  private AnchorPane backGround;
  @FXML
  private Button exit;
  @FXML
  private Button exportButton;
  @FXML
  private Button editButton;

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
    ObjectMapper mapper = new ObjectMapper();
    StringWriter writer = new StringWriter();
    try{
        mapper.writeValue(writer, event);
        String json  = writer.toString();
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

        mainCtrl.popup("Exported succesfully to: \n" + filePath + fileName, "Success", "OK");
      }
    catch(Exception exception){
      exception.printStackTrace();
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
          try{
            Event newEvent = mapper.readValue(selectedFile, Event.class);
            for(Event event : server.getEvents()) {
              if (event.getName().equals(newEvent.getName())) {
                mainCtrl.popup("Event with that name already exists!", "Warning!", "OK");
                return;
              }
            }
            Event createdEvent = new Event(newEvent.getName());
            createdEvent.setCreationDate(newEvent.getCreationDate());
            server.createEvent(createdEvent);
            long id = server.getEvents().getLast().getEventId();
            mainCtrl.addRecent(id);
            List<Participant> participants = newEvent.getParticipants();
            for(Participant p : participants){
              server.addParticipantEvent(p, id);
            }
            List<Tag> tags = newEvent.getTags();
            for(Tag t : tags){
              if(!server.checkIfTagExists(t.getId().getName(), id))
              {
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
            for(Debt d : debts){
              server.addDebtToEvent(id, new Debt(d.getAmount(), d.getCurrency(), d.getDebtor(), d.getCreditor()));
            }

          }
          catch (Exception e){
            e.printStackTrace();
          }
        }
        else{
          mainCtrl.popup("Wrong file format! Please select a .json file", "Warning", "Ok");
          return;
        }
    }
    else {
        return;
    }
  }

  public void editEvent(ActionEvent e){
    EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
    server.connect(selectedEvent.getId());
    mainCtrl.addRecent(selectedEvent.getId());
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
  }

  public void deleteEvent(ActionEvent e){
    if(selectedEvent == null){
      mainCtrl.popup("Please select an event", "Warning", "Okgit ");
      return;
    }

    VBox layout = new VBox(10);
    Label label = new Label("This is irreversible, Are you sure you want to remove \n the selected event?");
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
        exception.printStackTrace();
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

    showPopup();
  }
  private void showPopup() {
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle("Deleted Event Successfully");
    VBox layout = new VBox(10);
    Scene scene = new Scene(layout, 350, 20);
    popupStage.setScene(scene);
    popupStage.show();
  }
  public void close(ActionEvent e) throws IOException {
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    MainPageCtrl mainPageCtrl = new MainPageCtrl(server);
    mainCtrl.initialize(stage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    backgroundImage();
    keyShortCuts();
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

  private void keyShortCuts() {
    exit.requestFocus();

    table.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.RIGHT) editButton.requestFocus();
      if (event.getCode() == KeyCode.LEFT) exportButton.requestFocus();
    });

    passLengthField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.LEFT) generatePassButton.requestFocus();
      if (event.getCode() == KeyCode.ENTER) generatePassButton.requestFocus();
      if (event.getCode() == KeyCode.RIGHT) editButton.requestFocus();
    });

  }


  private void backgroundImage() {
    Image image = new Image("Background_Photo.jpg");
    BackgroundSize backgroundSize =
            new BackgroundSize(720, 450, true, true, true, false);
    BackgroundImage backgroundImage = new BackgroundImage(image,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
            backgroundSize);
    Background background = new Background(backgroundImage);
    backGround.setBackground(background);
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

