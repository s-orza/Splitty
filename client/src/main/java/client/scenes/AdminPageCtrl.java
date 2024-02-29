package client.scenes;

import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static com.google.inject.Guice.createInjector;

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
  private Label showEvent;

  private EventHelper selectedEvent;
  //Imports used to swap scenes
  private static final Injector INJECTOR = createInjector(new MyModule());
  private static final MyFXML FXML = new MyFXML(INJECTOR);
  private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);
  private Stage stage;
  ObservableList<EventHelper> contents = FXCollections.observableArrayList(
          new EventHelper("NewYears", new Date(2023, 12, 31), new Date(2024, 01, 01)),
          new EventHelper("Christmas", new Date(2023, 12, 20), new Date(2023, 12, 25) )
  );

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
    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    mainCtrl.initialize(stage, MainPageCtrl.getPair());
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
  public static Pair<Controller, Parent> getPair() {
    return FXML.load(Controller.class, "client", "scenes", "adminPage.fxml");
  }
}

