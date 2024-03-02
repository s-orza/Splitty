package client.scenes;

import commons.Event;
import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

import static com.google.inject.Guice.createInjector;

public class CreateEventCtrl implements Controller{
    //Imports used to swap scenes
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);


    //Gets the different FXML page components
    @FXML
    private Button CreateEventButton;
    @FXML
    private TextField textField;

    private String eventName;
    private Stage stage;


    //method to go to the eventPage once you create a new event with eventName as the title of the new event.
    // It also adds a new event to the data base
    public void create(ActionEvent e){
        Event newEvent = new Event(textField.getText());
//        ServerUtils.createEvent(newEvent);
        System.out.println("Crete event window");
        System.out.println(newEvent.toString());
        System.out.println(textField.getText());

        eventName = textField.getText();
        System.out.printf(eventName);
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