package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;

public class CreateEventCtrl implements Controller{

    //Gets the different FXML page components
    @FXML
    private Button CreateEventButton;
    @FXML
    private TextField textField;

    private String eventName;
    private Stage stage;
    ServerUtils server;
    @Inject
    public CreateEventCtrl(ServerUtils server) {
        this.server = server;
    }

    //method to go to the eventPage once you create a new event with eventName as the title of the new event.
    // It also adds a new event to the data base
    public void create(ActionEvent e){
        Event newEvent = new Event(textField.getText());
        for(Event event : server.getEvents()) {
            if (event.getName().equals(newEvent.getName())) {
                System.out.println("Event already exists!");
                return;
            }
        }
        server.createEvent(newEvent);
        newEvent = server.getEvents().getLast();
        System.out.println(newEvent.getEventId() + "id");
        System.out.println("Crete event window");
        System.out.println(textField.getText());
        System.out.println(server.getEvent(newEvent.getEventId()));
        EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
        server.connect(newEvent.getEventId());
        System.out.println(server.getCurrentId() + "ID cur");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
    }

    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "CreateEvent.fxml");
    }
    public String getTitle(){
        return "Create Event";
    }

}