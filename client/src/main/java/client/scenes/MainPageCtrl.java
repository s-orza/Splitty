package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;


import commons.MailStructure;
import commons.Tag;
import commons.TagId;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainPageCtrl implements Controller, Initializable {

  @FXML
  private Button email;
  @FXML
  private TextField createInput;
  @FXML
  private TextField joinInput;
  @FXML
  private ListView<String> recentList;
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
  @FXML
  private AnchorPane backGround;

  private Event selectedEv;
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

  public void sendMail(){
    System.out.println(mainCtrl.getConfig().getEmail());
    mainCtrl.refresh();
    if(mainCtrl.getConfig().getEmail() == null ||
    !mainCtrl.getConfig().getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
      email.setStyle("-fx-opacity: 0.5;");
      mainCtrl.popup("Inccorect email specified. You can modify it in AppConfig", "Warning", "Ok");
      return;
    }
    email.setStyle("-fx-opacity: 1;");
    if( server.sendMail(mainCtrl.getConfig().getEmail(),
            new MailStructure("Test mail", "It works!"))){
      mainCtrl.popup("Email sent succesfully", "Succes", "OK");
    }
    else{
      mainCtrl.popup("Email failed", "Warning", "OK");
    }
  }
  public void createEvent(ActionEvent e){
    if (createInput.getText().equals("")){
      mainCtrl.popup("Name can't be empty!", "Waring!", "Ok");
      return;
    }
    Event newEvent = new Event(createInput.getText());
    for(Event event : server.getEvents()) {
      if (event.getName().equals(newEvent.getName())) {
        mainCtrl.popup("Event already exists!", "Warning!", "OK");
        return;
      }
    }
    server.createEvent(newEvent);
    newEvent = server.getEvents().getLast();
    EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
    server.connect(newEvent.getEventId());
    mainCtrl.addRecent(server.getCurrentId());
    //adding the 4 tags that always need to be

    if(!server.checkIfTagExists("other", server.getCurrentId()))
      server.addTag(new Tag(new TagId("other",server.getCurrentId()),"#e0e0e0"));

    if(!server.checkIfTagExists("food", server.getCurrentId()))
      server.addTag(new Tag(new TagId("food",server.getCurrentId()),"#00ff00"));

    if(!server.checkIfTagExists("entrance fees", server.getCurrentId()))
      server.addTag(new Tag(new TagId("entrance fees",server.getCurrentId()),"#0000ff"));

    if(!server.checkIfTagExists("travel", server.getCurrentId()))
      server.addTag(new Tag(new TagId("travel",server.getCurrentId()),"#ff0000"));

    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
  }


  public void joinEvent(ActionEvent event) {
    try {
      //avoid connecting if there are problems
      if(joinInput.getText()==null || joinInput.getText().isEmpty())
      {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        mainCtrl.popup(resourceBundle.getString("eventNotFound"),"Error","Ok");
        return;
      }
      long id=Long.parseLong(joinInput.getText());
      if(server.getEvent(id)==null)
      {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        mainCtrl.popup(resourceBundle.getString("eventNotFound"),"Error","Ok");
        return;
      }
      server.connect(id);
    }catch (Exception e){
      e.printStackTrace();
      return;
    }
    mainCtrl.addRecent(server.getCurrentId());
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
    mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
  }

  public void setLang(Locale currentLocale) {
    this.currentLocale = currentLocale;
  }

  public void openAdmin(ActionEvent e){

    try{
      stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
      LoginAdminCtrl loginAdminCtrl = new LoginAdminCtrl(server);
      mainCtrl.initialize(stage, loginAdminCtrl.getPair(), loginAdminCtrl.getTitle());
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    backgroundImage();
    keyShortCuts();
    if(mainCtrl.getConfig().getEmail() == null ||
            !mainCtrl.getConfig().getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
      email.setStyle("-fx-opacity: 0.5;");
    }
    ArrayList<Event> contents = new ArrayList<>();
    ArrayList<Long> ids = new ArrayList<>();
    ArrayList<Long> oldIds = new ArrayList<>();
    server.getEvents().forEach(e -> ids.add(e.getEventId()));
    for(Long id : mainCtrl.getRecents()){
      if (ids.contains(id)) {
        contents.add(server.getEvent(id));
      }
      else{
        oldIds.add(id);
      }
    }
    for(Long id : oldIds){
      mainCtrl.removeRecent(id);
    }
    contents.sort(new EventActivitySort());
    for (Event e : contents){
      recentList.getItems().add(e.getEventId() + " - " + e.getName());
    }
    server.registerForMessages("/topic/events", Long.class, e -> {
      for (int i = 0; i<contents.size(); i++) {
        if (contents.get(i).getEventId()==e) {
          recentList.getItems().remove(i);
          break;
        }
      }
    });


    recentList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      selectedEv = server.getEvent(
              Integer.parseInt(
                      recentList.getSelectionModel().getSelectedItem()
                              .split(" - ")[0]));

      EventPageCtrl eventCtrl = new EventPageCtrl(server);
      try {
        String input = String.valueOf(eventCtrl.findEventId(selectedEv.getName()));
        joinInput.setText(input);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    //add flags to near the text
    comboBox.setCellFactory(param -> new TextFlagCell());



    if(currentLocale.getLanguage().equals("en")){
      putFlag("enFlag.png");
      ObservableList<String> comboBoxItems =
              FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("English");
    }else
    if(currentLocale.getLanguage().equals("nl")){
      putFlag("nlFlag.png");
      ObservableList<String> comboBoxItems =
              FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("Dutch");
    }else
    if(currentLocale.getLanguage().equals("de")){
      putFlag("deFlag.png");
      ObservableList<String> comboBoxItems =
              FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("German");
    }else
    if(currentLocale.getLanguage().equals("es")){
      putFlag("esFlag.png");
      ObservableList<String> comboBoxItems =
              FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
      comboBox.setItems(comboBoxItems);
      comboBox.setPromptText("Spanish");
    }

    toggleLanguage();
    prepareAnimation();

    comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
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

    addLanguageButton.setOnMouseClicked(e -> {
      stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
      LanguageTemplateCtrl languageTemplateCtrl = new LanguageTemplateCtrl(server);
      mainCtrl.initialize(stage, languageTemplateCtrl.getPair(), languageTemplateCtrl.getTitle());
    });
    //to accept only numbers.
    joinInput.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d*(\\d*)?")) {
        joinInput.setText(oldValue);
      }
    });
  }
  protected static class TextFlagCell extends ListCell<String> {
    private HBox container;
    private Label textLabel;
    private ImageView flag;
    public TextFlagCell() {
      container=new HBox();
      textLabel=new Label();
      //default flag.
      Image editIcon = new Image(getClass().getResourceAsStream("/enFlag.png"));
      flag=new ImageView(editIcon);
      flag.setFitWidth(19);
      flag.setFitHeight(14);
      container.getChildren().addAll(textLabel,flag);
      container.setHgrow(textLabel, Priority.ALWAYS);
      container.setAlignment(Pos.CENTER_LEFT);
      container.setSpacing(4);
    }
    @Override
    protected void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);

      if (empty || item == null) {
        setText(null);
        setGraphic(null);
      } else
      {
        textLabel.setText(item);
        try {
          Image editIcon;
          switch (item) {
            case "Dutch":
              editIcon = new Image(getClass().getResourceAsStream("/nlFlag.png"));
              break;
            case "German":
              editIcon = new Image(getClass().getResourceAsStream("/deFlag.png"));
              break;
            case "Spanish":
              editIcon = new Image(getClass().getResourceAsStream("/esFlag.png"));
              break;
            default:
              editIcon = new Image(getClass().getResourceAsStream("/enFlag.png"));
              break;
          }
          flag = new ImageView(editIcon);
          flag.setFitWidth(19);
          flag.setFitHeight(14);
          container.getChildren().clear();
          container.getChildren().addAll(textLabel, flag);
          container.setHgrow(textLabel, Priority.ALWAYS);
          container.setAlignment(Pos.CENTER_LEFT);
          container.setSpacing(8);
        }catch (Exception e){}
        setGraphic(container);
      }
      }
  }
  private void keyShortCuts() {
    recentList.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER||event.getCode() == KeyCode.RIGHT) {
        joinInput.requestFocus();
      }
      if (event.getCode() == KeyCode.LEFT||event.getCode() == KeyCode.UP) {
        addLanguageButton.requestFocus();
      }
    });
    addLanguageButton.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER||event.getCode() == KeyCode.RIGHT) {
        recentList.requestFocus();
      }
      if (event.getCode() == KeyCode.LEFT) {
        comboBox.requestFocus();
      }
    });

    comboBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.DOWN) {
        addLanguageButton.requestFocus();
        event.consume();
      }
      if (event.getCode() == KeyCode.ENTER) comboBox.show();
    });

    recentList.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
        joinButton.fire();
      }
    });
    joinInput.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER||event.getCode() == KeyCode.RIGHT) {
        joinButton.requestFocus();
      }
      if (event.getCode() == KeyCode.LEFT) {
        recentList.requestFocus();
      }
      if (event.getCode() == KeyCode.DOWN) {
        createInput.requestFocus();
      }
    });
    joinButton.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.RIGHT) {
        createInput.requestFocus();
      }
      if (event.getCode() == KeyCode.ENTER) {
        joinButton.fire();
      }

      if (event.getCode() == KeyCode.LEFT) {
        joinInput.requestFocus();
      }
    });
    createInput.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER||event.getCode() == KeyCode.RIGHT) {
        createButton.requestFocus();
      }
      if (event.getCode() == KeyCode.LEFT) {
        joinButton.requestFocus();
      }
      if (event.getCode() == KeyCode.UP)joinInput.requestFocus();
      if (event.getCode() == KeyCode.DOWN) {
        adminButton.requestFocus();
      }
    });
    createButton.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.DOWN) {
        adminButton.requestFocus();
      }
      if (event.getCode() == KeyCode.LEFT) {
        createInput.requestFocus();
      }
      if (event.getCode() == KeyCode.ENTER) {
        createButton.fire();
      }
    });
    adminButton.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.LEFT) {
        createButton.requestFocus();
      }
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

  /**
   * this is a very bad attempt to do a live language switching which appears to be impossible
   */
  public void toggleLanguage(){
    // Custom control to force reloading of the resource bundle
    ResourceBundle.Control control = new ResourceBundle.Control() {
      @Override
      public long getTimeToLive(String baseName, Locale locale) {
        return ResourceBundle.Control.TTL_DONT_CACHE;
      }

      @Override
      public boolean needsReload(String baseName, Locale locale,
                                 String format, ClassLoader loader,
                                 ResourceBundle bundle, long loadTime) {
        return true;
      }
    };
    ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale, control);
    ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());

      createNewEventLabel.setText(resourceBundle.getString("createNewEventText"));
      joinEventLabel.setText(resourceBundle.getString("joinEventText"));
      joinButton.setText(resourceBundle.getString("joinText"));
      adminButton.setText(resourceBundle.getString("adminText"));
      recentEventsLabel.setText(resourceBundle.getString("recentEventsText"));
      createButton.setText(resourceBundle.getString("createText"));
      addLanguageButton.setText(resourceBundle.getString("addLanguageText"));
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
    shrink.setToY(0.0);
    shrink.setInterpolator(Interpolator.EASE_BOTH);

    ScaleTransition restore = new ScaleTransition(Duration.millis(150), flagButton);
    restore.setToY(1);
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
