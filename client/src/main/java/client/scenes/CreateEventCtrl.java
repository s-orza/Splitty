package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.google.inject.Guice.createInjector;

public class CreateEventCtrl implements Controller, Initializable {
    //Imports used to swap scenes
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    //Gets the different FXML page components
    @FXML
    private Button CreateEventButton;
    @FXML
    private TextField textField;
    @FXML
    private Label createEventLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label joinCodeLabel;

    private Locale currentLocale = MainPageCtrl.getCurrentLocale();
    private String eventName;
    private Stage stage;
    ServerUtils server;
    @Inject
    public CreateEventCtrl(ServerUtils server) {
        this.server = server;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources){
        System.out.println("we should be seeing this");
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        joinCodeLabel.setText(resourceBundle.getString("joinEventText"));
        titleLabel.setText(resourceBundle.getString("createNewEventText"));
        createEventLabel.setText(resourceBundle.getString("createNewEventLabel"));
    }
    //method to go to the eventPage once you create a new event with eventName as the title of the new event.
    // It also adds a new event to the data base
    public void create(ActionEvent e){
        Event newEvent = new Event(textField.getText());
        System.out.println("Crete event window");
        System.out.println(textField.getText());
        System.out.println(server.getEvents());
        server.createEvent(newEvent);
        EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
        eventPageCtrl.connectEvent(newEvent);
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, EventPageCtrl.getPair(), EventPageCtrl.getTitle());
    }

    public static Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "CreateEvent.fxml");
    }
    public static String getTitle(){
        return "Create Event";
    }

}