package client.scenes;

import client.utils.ServerUtils;
import commons.MailStructure;
import commons.Participant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class InviteParticipantCtrl implements Controller, Initializable {

  @FXML
  private TableView<Participant> participants;

  @FXML
  private TableColumn<Participant, String> name;

  @FXML
  private TableColumn<Participant, String> email;

  @FXML
  private Button invite;

  @FXML
  private Button cancel;

  private Participant selectedParticipant;

  private Stage stage;
  ServerUtils server;
  ObservableList<Participant> contents;
  @Inject
  public InviteParticipantCtrl(ServerUtils server) {
    this.server = server;
  }
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //There was a bug. Before it was server.getAllParticipantsFromDatabase() but this returns a null.
    List <Participant> list =server.getParticipantsOfEvent(server.getCurrentId());
    contents =  FXCollections.observableArrayList(
            list.stream().filter(e -> e.getEmail().matches(
                    "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$") &&
            !server.getParticipantsOfEvent(server.getCurrentId()).contains(e)).toList());
    name.setCellValueFactory(new PropertyValueFactory<Participant, String>("name"));
    email.setCellValueFactory(new PropertyValueFactory<Participant, String>("email"));
    participants.setItems(contents);
    participants.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      selectedParticipant = participants.getSelectionModel().getSelectedItem();
    });
  }

  public void close(ActionEvent e){
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
    mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
  }
  public void invite(ActionEvent e){
    if(server.sendMail(selectedParticipant.email,
            new MailStructure("Please join my event!",
                    "I invite you to join my event at " +
                            server.getServerUrl() +
                            ".\n Use this code to join: " +
                            server.getCurrentId()))){
      mainCtrl.popup("Email sent succesfully", "Succes", "OK");
      server.addParticipantEvent(selectedParticipant, server.getCurrentId());
      contents.remove(selectedParticipant);
      participants.setItems(contents);
    }
    else{
      mainCtrl.popup("Email failed", "Warning", "OK");
    }
  }
  public Pair<Controller, Parent> getPair() {
    return FXML.load(Controller.class, "client", "scenes", "InviteParticipant.fxml");
  }

  @Override
  public String getTitle() {
    return "Invite participant page";
  }

}
