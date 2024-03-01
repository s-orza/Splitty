package client.scenes;

import client.utils.ServerUtils;
import commons.Participant;
import com.google.inject.Inject;
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
        System.out.println(participant);
    }

    @FXML
    void cancelEvent()
}
