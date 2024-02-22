package client.scenes;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageCtrl {

@FXML
private TextField createInput;
@FXML
private TextField joinInput;
private Stage stage;
private Scene scene;
private Parent root;
  public void createEvent(ActionEvent e) {
    System.out.println("Crete event window");
    System.out.println(createInput.getText());
    /*    Creating a new event
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/event.fxml"));
    root = loader.load();
    EventCtrl eC = new EventCtrl();
    eC.passInput(createInput.getText());
    stage = (Stage)((Node) e.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
     */
  }
  public void joinEvent(ActionEvent e) {
    System.out.println("Join event window");
    System.out.println(joinInput.getText());

    /*    Joining an event
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/event.fxml"));
    root = loader.load();
    EventCtrl eC = new EventCtrl();
    eC.passInput(joinInput.getText());
    stage = (Stage)((Node) e.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
     */
  }
  public void openAdmin(ActionEvent e) throws IOException {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginAdmin.fxml"));
    root = loader.load();
    stage = (Stage)((Node) e.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();

  }

}
