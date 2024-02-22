package client.scenes;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPageCtrl {

  private Stage stage;
  private Scene scene;
  private Parent root;
  public void exportEvent(ActionEvent e) {
    System.out.println("export event to file");
  }
  public void importEvent(ActionEvent e) {
    System.out.println("import event from file");
  }
  public void close(ActionEvent e) throws IOException {
    System.out.println("close window");
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainPage.fxml"));
    root = loader.load();
    stage = (Stage)((Node) e.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

}
