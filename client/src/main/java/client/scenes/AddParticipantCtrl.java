package client.scenes;

import static com.google.inject.Guice.createInjector;

import java.util.ResourceBundle;
import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Participant;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.text.Text;

import static client.scenes.MainPageCtrl.currentLocale;

public class AddParticipantCtrl implements Controller{
    private ServerUtils server;
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField name;
    @FXML
    private TextField email;
    @FXML
    private TextField iban;
    @FXML
    private TextField bic;
    private static Alert errorAlert;
    private Participant participantToBeModified;
    ResourceBundle resourceBundle;
    
    @Inject
    public AddParticipantCtrl(ServerUtils server){
        this.server = server;
    }

    @FXML
    public void initialize(){
        System.out.println(server.getParticipantIdToModify());
        initializeVariables();
        toggleLanguage();
        resetElements();
        // check if we are editing or adding a participant
        if (server.getParticipantIdToModify() != -1) {
            // if we are editing a participant
            System.out.println("Entered with participant code: " + server.getParticipantToBeModified());
            participantToBeModified = server.getParticipantToBeModified();
            reloadParticipant();
        }
        else {
            // if we are adding a participant
            System.out.println(server.getParticipantIdToModify());
            addButton.setVisible(true);
            saveButton.setVisible(false);
        }
    }

    private void initializeVariables() {
        name.setText("");
        email.setText("");
        iban.setText("");
        bic.setText("");
    }

    private void toggleLanguage(){
        try{
            resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
            name.setText(resourceBundle.getString("nameText"));
            addButton.setText(resourceBundle.getString("addText"));
            saveButton.setText(resourceBundle.getString("saveText"));
            cancelButton.setText(resourceBundle.getString("cancelText"));
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }

    /**
     * Will return a participant if all the fields have been filled up and no rules were broken
     */
    @FXML
    void addParticipant(ActionEvent event) {
        checkAnyFieldIsEmpty();

        Participant newParticipant = new Participant(name.getText(), email.getText(), iban.getText(), bic.getText());
        try {
            //TODO
            // make the eventID be specific to each event
            String destination = "/app/participant/event/" + String.valueOf(server.getCurrentId());
            server.sendParticipant(destination, newParticipant);
            close(event);
        } catch (WebApplicationException e) {
            System.out.println("Error inserting participant into the database: " + e.getMessage());
        }
    }

    @FXML
    void updateParticipant(ActionEvent event) {

    }

    /**
     * Checks for any empty field.
     * @return Any string for an empty field or an empty string if no errors were found
     */
    private void checkAnyFieldIsEmpty() {
        if (name.getText().isEmpty()) {
            mainCtrl.popup(name.getText() + " field is Empty!", "Warning", "Ok");
            return;
        }
        if (email.getText().isEmpty()) {
            mainCtrl.popup(email.getText() + " field is Empty!", "Warning", "Ok");
            return;
        }
        if (iban.getText().isEmpty()) {
            mainCtrl.popup(iban.getText() + " field is Empty!", "Warning", "Ok");
            return;
        }
        if (bic.getText().isEmpty()) {
            mainCtrl.popup(bic.getText() + " field is Empty!", "Warning", "Ok");
        }
    }

    public void resetElements(){
        name.clear();
        email.clear();
        iban.clear();
        bic.clear();
    }

    @FXML
    public void close(ActionEvent e){
        System.out.println("Closing Addparticipants window...");
        server.setParticipantToBeModified(-1);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
        mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
    }

    /**
     * This method loads the participant that will be edited
     */
    private void reloadParticipant() {
        // swap buttons
        saveButton.setVisible(true);
        addButton.setVisible(false);
        //initialize fields
        name.setText(participantToBeModified.getName());
        email.setText(participantToBeModified.getEmail());
        iban.setText(participantToBeModified.getIban());
        bic.setText(participantToBeModified.getBic());
    }


    public Pair<Controller, Parent> getPair(){
        return FXML.load(Controller.class, "client", "scenes", "AddParticipant.fxml");
    }
    public String getTitle(){
        return "Add Participant";
    }
}

