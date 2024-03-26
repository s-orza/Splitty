package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;


import javafx.animation.*;
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

import javafx.scene.image.Image;

import javafx.scene.layout.*;
import javafx.scene.text.Text;

import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;


public class MainPageCtrl implements Controller, Initializable {

  @FXML
  private TextField createInput;
  @FXML
  private TextField joinInput;
  @FXML
  private ListView<EventHelper> recentList;
  @FXML
  private Button flagButton;
  @FXML
  private Text createNewEventLabel;
  @FXML
  private Text joinEventLabel;
  @FXML
  private Button joinButton;
  @FXML
  private Button createButton;
  @FXML
  private Text recentEventsLabel;
  @FXML
  private Button adminButton;
  @FXML
  private ComboBox comboBox;
  @FXML
  private Button addLanguageButton;

  private EventHelper selectedEv;
  //Imports used to swap scenes
  private Stage stage;
  private ServerUtils server;

  protected static Locale currentLocale = new Locale("en", "US");

  public static Locale getCurrentLocale() {
    return currentLocale;
  }

  private int counter = 0;
  private TranslateTransition  smoothShake;
  private SequentialTransition seqTransition;

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

    try{
      System.out.println("opening admin");
      stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
      LoginAdminCtrl loginAdminCtrl = new LoginAdminCtrl(server);
      mainCtrl.initialize(stage, loginAdminCtrl.getPair(), loginAdminCtrl.getTitle());
    }catch (Exception ex){
      System.out.println(ex);
    }
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




    if(currentLocale.getLanguage().equals("en")){
      putFlag("enFlag.png");
      ObservableList<String> comboBoxItems =
              FXCollections.observableArrayList("English", "Dutch", "German", "Spanish", "Extra");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("English");
    }
    if(currentLocale.getLanguage().equals("nl")){
      putFlag("nlFlag.png");
      ObservableList<String> comboBoxItems =
              FXCollections.observableArrayList("English", "Dutch", "German", "Spanish", "Extra");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("Dutch");
    }
    if(currentLocale.getLanguage().equals("de")){
      putFlag("deFlag.png");
      ObservableList<String> comboBoxItems =
              FXCollections.observableArrayList("English", "Dutch", "German", "Spanish", "Extra");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("German");
    }
    if(currentLocale.getLanguage().equals("es")){
      putFlag("esFlag.png");
      ObservableList<String> comboBoxItems =
              FXCollections.observableArrayList("English", "Dutch", "German", "Spanish", "Extra");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("Spanish");
    }
    if(currentLocale.getLanguage().equals("xx")){
      putFlag("xxFlag.png");
      ObservableList<String> comboBoxItems =
              FXCollections.observableArrayList("English", "Dutch", "German", "Spanish", "Extra");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("xx");
    }
    toggleLanguage();
    prepareAnimation();

    comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
      System.out.println("Selected item: " + newValue);
      if(newValue.equals("English")) changeFlag("en");
      if(newValue.equals("Dutch")) changeFlag("nl");
      if(newValue.equals("Spanish")) changeFlag("es");
      if(newValue.equals("German")) changeFlag("de");
      if(newValue.equals("Extra")) changeFlag("xx");
      toggleLanguage();
    });

    flagButton.setOnMouseClicked(event -> {
//      changeFlag();
//      toggleLanguage();
      comboBox.show();
    });

    addLanguageButton.setOnMouseClicked(e -> {
      stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
      LanguageTemplateCtrl languageTemplateCtrl = new LanguageTemplateCtrl(server);
      mainCtrl.initialize(stage, languageTemplateCtrl.getPair(), languageTemplateCtrl.getTitle());
    });
  }
  public void changeFlag(String toChange){
    seqTransition.play();
    if(toChange.equals("es")){
      currentLocale = new Locale("es", "ES");
      // pause for a bit so that the flag shrinks and then changes it
      PauseTransition pause = new PauseTransition(Duration.millis(150));
      // This executes changeFlag after the pause
      pause.setOnFinished(e -> putFlag("esFlag.png"));
      pause.play();
    }
    else if(toChange.equals("nl")){
      currentLocale = new Locale("nl", "NL");
      // pause for a bit so that the flag shrinks and then changes it
      PauseTransition pause = new PauseTransition(Duration.millis(150));
      // This executes changeFlag after the pause
      pause.setOnFinished(e -> putFlag("nlFlag.png"));
      pause.play();
    }
    else if(toChange.equals("de")){
      currentLocale = new Locale("de", "DE");
      // pause for a bit so that the flag shrinks and then changes it
      PauseTransition pause = new PauseTransition(Duration.millis(150));
      // This executes changeFlag after the pause
      pause.setOnFinished(e -> putFlag("deFlag.png"));
      pause.play();
    }
    else if(toChange.equals("xx")){
      currentLocale = new Locale("xx", "XX");
      // pause for a bit so that the flag shrinks and then changes it
      PauseTransition pause = new PauseTransition(Duration.millis(150));
      // This executes changeFlag after the pause
      pause.setOnFinished(e -> putFlag("xxFlag.png"));
      pause.play();
    }
    else{
      currentLocale = new Locale("en", "US");
      PauseTransition pause = new PauseTransition(Duration.millis(150));
      pause.setOnFinished(e -> putFlag("enFlag.png"));
      pause.play();
    }
  }
  public void toggleLanguage(){
      System.out.println("image pressed " + counter++);
      ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
      createNewEventLabel.setText(resourceBundle.getString("createNewEventText"));
      joinEventLabel.setText(resourceBundle.getString("joinEventText"));
      joinButton.setText(resourceBundle.getString("joinText"));
      adminButton.setText(resourceBundle.getString("adminText"));
      recentEventsLabel.setText(resourceBundle.getString("recentEventsText"));
      createButton.setText(resourceBundle.getString("createText"));
  }

  private void putFlag(String path){
    Image image = new Image(path);
    BackgroundSize backgroundSize =
            new BackgroundSize(100, 100, true, true, true, false);
    BackgroundImage backgroundImage = new BackgroundImage(image,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
            backgroundSize);

    Background background = new Background(backgroundImage);

    flagButton.setBackground(background);
  }

  public void prepareAnimation(){
    // Shrink transition
    ScaleTransition shrink = new ScaleTransition(Duration.millis(150), flagButton);
    shrink.setToY(0.0); // Shrink to disappear on the Y axis
    shrink.setInterpolator(Interpolator.EASE_BOTH);

    ScaleTransition restore = new ScaleTransition(Duration.millis(150), flagButton);
    restore.setToY(1); // Restore to original size on the Y axis
    restore.setInterpolator(Interpolator.EASE_BOTH);

    seqTransition = new SequentialTransition(shrink, restore);

    flagButton.setOnMouseClicked(event -> seqTransition.play());
  }
  public Pair<Controller, Parent> getPair() {

    return FXML.load(Controller.class, "client", "scenes", "mainPage.fxml");
  }
  public String getTitle(){
    return "Main Page";
  }
}
