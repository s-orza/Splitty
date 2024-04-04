package client.scenes;

import java.util.ResourceBundle;
import client.utils.ServerUtils;
import commons.Participant;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

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
        initializeVariables();
        toggleLanguage();
        resetElements();
        // check if we are editing or adding a participant
        if (server.getParticipantIdToModify() != -1) {
            // if we are editing a participant
            participantToBeModified = server.getParticipantToBeModified();
            reloadParticipant();
        }
        else {
            // if we are adding a participant
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
        checkFieldsCondition();

        Participant newParticipant = new Participant(name.getText(), email.getText(), iban.getText(), bic.getText());
        try {
            String destination = "/app/participant/event/" + server.getCurrentId();
            server.sendParticipant(destination, newParticipant);
            close(event);
        } catch (WebApplicationException e) {
            System.out.println("Error inserting participant into the database: " + e.getMessage());
        }
    }

    @FXML
    void updateParticipant(ActionEvent event) {
        if(!checkFieldsCondition())
            return;
        //reload again the participant to be sure that it is the newest participant
        participantToBeModified=server.getParticipantToBeModified();
        if(participantToBeModified==null)
        {
            //this can happen if somebody else deleted this participant while you were editing it. In this case
            //let's send a message to the user to inform him and to abort editing.
            VBox layout = new VBox(10);
            Label label = new Label("Somebody deleted this participant while you were editing it. \n" +
                    "Return to the event page:(");
            Button okButton = new Button("Ok");


            // Set up the stage
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Warning");


            okButton.setOnAction(e -> {
                popupStage.close();
                close(event);
            });

            // Set up the layout
            layout.getChildren().addAll(label, okButton);
            layout.setAlignment(Pos.CENTER);
            // Set the scene and show the stage
            Scene scene = new Scene(layout, 450, 150);
            popupStage.setScene(scene);
            popupStage.showAndWait();
            return;
        }
        //get participant to be modified
        Participant participant = server.getParticipant(participantToBeModified.getParticipantID());
        //modify the participant and save it in the database
        server.updateParticipant(participantToBeModified.getParticipantID(), participant);
        participantToBeModified=null;
        server.setParticipantToBeModified(-1);

        close(event);
    }

    /**
     * Checks for any empty field.
     * @return Any string for an empty field or an empty string if no errors were found
     */
    private boolean checkFieldsCondition() {
        if (name.getText().isEmpty()) {
            mainCtrl.popup(name.getText() + " field is Empty!", "Warning", "Ok");
            return false;
        }
        if (email.getText().isEmpty()) {
            mainCtrl.popup(email.getText() + " field is Empty!", "Warning", "Ok");
            return false;
        }
        if (iban.getText().isEmpty()) {
            mainCtrl.popup(iban.getText() + " field is Empty!", "Warning", "Ok");
            return false;
        }
        if (bic.getText().isEmpty()) {
            mainCtrl.popup(bic.getText() + " field is Empty!", "Warning", "Ok");
            return false;
        }
        //TODO
        // Check validity of email, iban and bic
        return true;
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

