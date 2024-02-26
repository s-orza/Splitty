package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CreateEventCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    //Gets the different FXML page components
    @FXML
    private Button CreateEventButton;
    @FXML
    private TextField textField;

    private String eventName;

    @Inject
    public CreateEventCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    //method to go to the eventPage once you create a new event with eventName as the title of the new event
    public void create(ActionEvent e){
//        mainCtrl.showEventPage();
        eventName = textField.getText();
        System.out.printf(eventName);
    }
}