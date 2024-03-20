package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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
        if (textField.getText().equals("")){
            popup("Name can't be empty!");
            return;
        }
        Event newEvent = new Event(textField.getText());
        for(Event event : server.getEvents()) {
            if (event.getName().equals(newEvent.getName())) {
                popup("Event already exists!");
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

    private void popup(String text){
        VBox layout = new VBox(10);
        Label label = new Label(text);
        Button cancelButton = new Button("Cancel");

        // Set up the stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Warning!");

        cancelButton.setOnAction(e -> {
            popupStage.close();
        });

        // Set up the layout
        layout.getChildren().addAll(label, cancelButton);
        layout.setAlignment(Pos.CENTER);

        // Set the scene and show the stage
        Scene scene = new Scene(layout, 370, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    public void close(ActionEvent e){
        System.out.println("close window");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        MainPageCtrl mainPageCtrl = new MainPageCtrl(server);
        mainCtrl.initialize(stage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());
    }

    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "CreateEvent.fxml");
    }
    public String getTitle(){
        return "Create Event";
    }

}