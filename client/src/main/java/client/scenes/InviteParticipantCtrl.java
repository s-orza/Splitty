package client.scenes;

import client.utils.ServerUtils;
import commons.MailStructure;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

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

  private Stage stage;
  ServerUtils server;
  @Inject
  public InviteParticipantCtrl(ServerUtils server) {
    this.server = server;
  }
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    backgroundImage();
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
      server.addParticipantEvent(new Participant(name.getText(), email.getText(), "", ""), server.getCurrentId());
    }
    else{
      mainCtrl.popup("Email failed", "Warning", "OK");
    }
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
    return FXML.load(Controller.class, "client", "scenes", "InviteParticipant.fxml");
  }

  @Override
  public String getTitle() {
    return "Invite participant page";
  }

}
