package client.scenes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class AdminPageCtrl implements Initializable {

  @FXML
  private TableView<EventHelper> table;

  @FXML
  private TableColumn<EventHelper, Date> tableActivity;

  @FXML
  private TableColumn<EventHelper, Date> tableDate;

  @FXML
  private TableColumn<EventHelper, String> tableTitle;
  @FXML
  private Label showEvent;

  private EventHelper selectedEvent;
  ObservableList<EventHelper> contents = FXCollections.observableArrayList(
          new EventHelper("NewYears", new Date(2023, 12, 31), new Date(2024, 01, 01)),
          new EventHelper("Christmas", new Date(2023, 12, 20), new Date(2023, 12, 25) )
  );

  private Stage stage;
  private Scene scene;
  private Parent root;
  public void exportEvent(ActionEvent e) {
    System.out.println("export event to file");
  }
  public void importEvent(ActionEvent e) {
    System.out.println("import event from file");
  }

  public void editEvent(ActionEvent e){
    System.out.println("edit selected event");
  }

  public void deleteEvent(ActionEvent e){
    System.out.println("delete selected event");
    contents.remove(selectedEvent);
    table.setItems(contents);
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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tableTitle.setCellValueFactory(new PropertyValueFactory<EventHelper, String>("title"));
    tableActivity.setCellValueFactory(new PropertyValueFactory<EventHelper, Date>("lastActivity"));
    tableDate.setCellValueFactory(new PropertyValueFactory<EventHelper, Date>("creationDate"));
    table.setItems(contents);
    table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      selectedEvent = table.getSelectionModel().getSelectedItem();
      showEvent.setText(selectedEvent.getTitle());
    });
  }
}
