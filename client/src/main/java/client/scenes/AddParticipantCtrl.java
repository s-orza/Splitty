package client.scenes;

import client.utils.ServerUtils;
import commons.Participant;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AddParticipantCtrl {
    private ServerUtils server;
    private MainCtrl mainCtrl;
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField name;
    @FXML
    private TextField email;
    @FXML
    private TextField iban;
    @FXML
    private TextField bic;

    @Inject
    public AddParticipantCtrl(ServerUtils server, MainCtrl mainCtrl){
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * The following piece of code is to simulate a database interaction by adding
     * a participant to an expense with a list of participants
     */

    private ObservableList<Participant> participantList = FXCollections.observableArrayList(
            new Participant("David", "example1@email1.com", "IBAN1", "BIC1"),
            new Participant("Rick", "neverGonna@email2.com", "IBAN2", "BIC2"),
            new Participant("Astley", "GiveYouUp@email3.com", "IBAN3", "BIC3"),
            new Participant("Rando", "example2@email3.com", "IBAN4", "BIC4"),
            new Participant("Shahar", "example3@email2.com", "IBAN5", "BIC5"),
            new Participant("Alex", "example4@email3.com", "IBAN6", "BIC6")
    );

    @FXML
    public void initialize(){

        // TODO
        // a method that
        System.out.println("Initializing AddParticipantCtrl...");
    }

    /**
     * Will return a participant if all the fields have been filled
     */
    @FXML
    void addParticipantToEvent(MouseEvent event){
        if (name.getText().isEmpty()){
            System.out.println("Name field is empty");
            return;
        }
        if (email.getText().isEmpty()){
            System.out.println("Email field is empty");
            return;
        }
        if (iban.getText().isEmpty()){
            System.out.println("IBAN field is empty");
            return;
        }
        if (bic.getText().isEmpty()){
            System.out.println("BIC field is empty");
            return;
        }
        Participant participant = new Participant(name.getText(), email.getText(), iban.getText(), bic.getText());
        if (!participantList.contains(participant)) {
            participantList.add(participant);
            System.out.println("Participant " + participantList.getLast() + " was added");
        }
        else {
            //TODO
            // implement a method that displays this message in a pop-up window
            System.out.println("Participant is already in the list");
        }
    }

    @FXML
    void cancelEvent(){
        System.out.println("Participant adding process canceled");
    }


}