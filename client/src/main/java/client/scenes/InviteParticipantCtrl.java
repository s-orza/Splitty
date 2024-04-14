package client.scenes;

import client.utils.ServerUtils;
import commons.MailStructure;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static client.scenes.MainPageCtrl.currentLocale;

public class InviteParticipantCtrl implements Controller, Initializable {

  @FXML
  private TextField name;

  @FXML
  private TextField email;

  @FXML
  private Button invite;

  @FXML
  private Button cancel;
  @FXML
  private AnchorPane backGround;

  @FXML
  private Text nameText;

  @FXML
  private Label inviteSelectParticipantsText;
  @FXML
  private CheckBox add;

  ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);

  private Participant selectedParticipant;


  private Stage stage;
  ServerUtils server;
  @Inject
  public InviteParticipantCtrl(ServerUtils server) {
    this.server = server;
  }
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    backgroundImage();
    toggleLanguage();
    keyShortCuts();
  }

  private void keyShortCuts() {
    name.sceneProperty().addListener((observable, oldScene, newScene) -> {
      if (newScene != null) {
        Scene scene = (name.getScene());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
          if (event.getCode() == KeyCode.ESCAPE) {
            cancel.fire();
          }
        });
      }
    });
    name.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.DOWN)  email.requestFocus();
    });
    email.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.DOWN)  invite.requestFocus();
      if (event.getCode() == KeyCode.LEFT||event.getCode() == KeyCode.UP)  name.requestFocus();
    });
    invite.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.RIGHT)  cancel.requestFocus();
      if (event.getCode() == KeyCode.LEFT||event.getCode() == KeyCode.UP)  email.requestFocus();
    });
  }

  private void toggleLanguage(){
    inviteSelectParticipantsText.setText(resourceBundle.getString("inviteSelectParticipantsText"));
    nameText.setText(resourceBundle.getString("nameText"));
    cancel.setText(resourceBundle.getString("cancelText"));
    invite.setText(resourceBundle.getString("inviteText"));
    add.setText(resourceBundle.getString("addToEvent?"));
  }

  public void close(ActionEvent e){
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
    mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
  }
  public void invite(ActionEvent e){
    if(!email.getText().matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")){
      mainCtrl.popup("Incorrect email", "Warning", "OK");
      return;
    }
    if(server.sendMail(email.getText(),
            new MailStructure("Please join my event!",
                    "I invite you to join my event at " +
                            server.getServerUrl() +
                            ".\n Use this code to join: " +
                            server.getCurrentId()))){
      mainCtrl.popup("Email sent succesfully", "Succes", "OK");
      if (add.isSelected()){
        server.addParticipantEvent(new Participant(name.getText(), email.getText(), "", ""), server.getCurrentId());
      }
      name.getText();
    }
    else{
      mainCtrl.popup("Email failed", "Warning", "OK");
    }
  }

  private void backgroundImage() {
    Image image = new Image("Background_Photo.jpg");
    BackgroundSize backgroundSize =
            new BackgroundSize(360, 275, true, true, true, false);
    BackgroundImage backgroundImage = new BackgroundImage(image,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
            backgroundSize);
    Background background = new Background(backgroundImage);
    backGround.setBackground(background);
  }
  public Pair<Controller, Parent> getPair() {
    return FXML.load(Controller.class, "client", "scenes", "InviteParticipant.fxml");
  }

  @Override
  public String getTitle() {
    return resourceBundle.getString("inviteParticipantsTitle");
  }

}
