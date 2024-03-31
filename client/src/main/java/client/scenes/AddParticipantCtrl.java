package client.scenes;

import static com.google.inject.Guice.createInjector;

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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.text.Text;

public class AddParticipantCtrl implements Controller{
    private ServerUtils server;
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
    @FXML
    private Text warningText;
    private static Injector INJECTOR;
    private static MyFXML FXML;
    private static MainCtrl mainCtrl;
    private static Alert warning;
    
    @Inject
    public AddParticipantCtrl(ServerUtils server){
        this.server = server;
        initialize();
    }

    @FXML
    public void initialize(){
        System.out.println("Initializing AddParticipantCtrl...");

        // Initializing everything related to the
        INJECTOR = createInjector(new MyModule());
        FXML = new MyFXML(INJECTOR);
        mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        warningText = new Text();
    }

    /**
     * Will return a participant if all the fields have been filled
     */
    @FXML
    void addParticipantToEvent(ActionEvent event) {
        if(checkAnyFieldIsEmpty(event))
            return;

        Participant newParticipant = new Participant(name.getText(), email.getText(), iban.getText(), bic.getText());
        try {
            server.addParticipantEvent(newParticipant, server.getCurrentId());
            close(event);
        } catch (WebApplicationException e) {
            System.out.println("Error inserting participant into the database: " + e.getMessage());
        }
    }

    private boolean checkAnyFieldIsEmpty(ActionEvent event) {
        if (name.getText().isEmpty()) {
            System.out.println("Name field is empty warning displayed");
            warningText.setText("Name field is empty");
            displayError(event, "Name");
            return true;
        }
        if (email.getText().isEmpty()) {
            System.out.println("Email field is empty warning displayed");
            warningText.setText("Email field is empty");
            displayError(event, "Email");
            return true;
        }
        if (iban.getText().isEmpty()) {
            System.out.println("IBAN field is empty warning displayed");
            warningText.setText("IBAN field is empty");
            displayError(event, "IBAN");
            return true;
        }
        if (bic.getText().isEmpty()) {
            System.out.println("BIC field is empty warning displayed");
            warningText.setText("BIC field is empty");
            displayError(event, "BIC");
            return true;
        }
        return false;
    }

    public void displayError(ActionEvent event, String cause){
        switch(cause){
            case "Name" -> {
                System.out.println("Name field is empty!");
            }
            case "Email" -> {
                System.out.println("Email field is empty!");
            }
            case "IBAN" -> {
                System.out.println("IBAN field is empty!");
            }
            case "BIC" -> {
                System.out.println("BIC field is empty!");
            }
            default -> {
                System.out.println("Unrecognized field...");
            }
        }
    }

    @FXML
    public void close(ActionEvent e){
        System.out.println("close window");
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
        mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
    }


    public Pair<Controller, Parent> getPair(){
        return FXML.load(Controller.class, "client", "scenes", "AddParticipant.fxml");
    }
    public String getTitle(){
        return "Add Participant";
    }
}

