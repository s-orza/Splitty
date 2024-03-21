package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.google.inject.Injector;
import commons.Event;


import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;



import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.google.inject.Guice.createInjector;


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

  private EventHelper selectedEv;
  //Imports used to swap scenes
  private Stage stage;
  private static final Injector INJECTOR = createInjector(new MyModule());
  private static final MyFXML FXML = new MyFXML(INJECTOR);

  private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);
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

  public void createEvent(ActionEvent e) {
    System.out.println("Crete event window");
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, CreateEventCtrl.getPair(), CreateEventCtrl.getTitle());
  }

  public void joinEvent(ActionEvent event) {
    System.out.println("Join event window");
    System.out.println(joinInput.getText());
    EventPageCtrl eventCtrl = new EventPageCtrl(server);
    try {
      eventCtrl.connectEvent(server.getEvent(Long.parseLong(joinInput.getText())));
    }catch (Exception e){
      System.out.println(e);
      return;
    }
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, EventPageCtrl.getPair(), EventPageCtrl.getTitle());
  }

  public void openAdmin(ActionEvent e){
    try{
      System.out.println("opening admin");
      stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
      mainCtrl.initialize(stage, LoginAdminCtrl.getPair(), LoginAdminCtrl.getTitle());
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
      ObservableList<String> comboBoxItems = FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("English");
    }
    if(currentLocale.getLanguage().equals("nl")){
      putFlag("nlFlag.png");
      ObservableList<String> comboBoxItems = FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("Dutch");
    }
    if(currentLocale.getLanguage().equals("de")){
      putFlag("deFlag.png");
      ObservableList<String> comboBoxItems = FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("German");
    }
    if(currentLocale.getLanguage().equals("es")){
      putFlag("esFlag.png");
      ObservableList<String> comboBoxItems = FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("Spanish");
    }
    toggleLanguage();
    prepareAnimation();

    comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
      System.out.println("Selected item: " + newValue);
      if(newValue.equals("English")) changeFlag("en");
      if(newValue.equals("Dutch")) changeFlag("nl");
      if(newValue.equals("Spanish")) changeFlag("es");
      if(newValue.equals("German")) changeFlag("de");
      toggleLanguage();
    });

    flagButton.setOnMouseClicked(event -> {
//      changeFlag();
//      toggleLanguage();
      comboBox.show();
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
  public static Pair<Controller, Parent> getPair() {
    return FXML.load(Controller.class, "client", "scenes", "mainPage.fxml");
  }
  public static String getTitle(){
    return "Main Page";
  }
}
