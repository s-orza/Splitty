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
import javafx.scene.control.Alert.AlertType;
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
    private static Injector injector;
    private static MyFXML FXML;
    private static MainCtrl mainCtrl;
    private static Alert warningAlert;
    private static Alert errorAlert;
    
    @Inject
    public AddParticipantCtrl(ServerUtils server){
        this.server = server;
        initialize();
    }

    @FXML
    public void initialize(){
        System.out.println("Initializing AddParticipantCtrl...");
        // Nothing needs to be initialized from the database so nothing will be done about that
        // Initializing everything that might be needed for this controller
        injector = createInjector(new MyModule());
        FXML = new MyFXML(injector);
        mainCtrl = injector.getInstance(MainCtrl.class);

        // initializing warning Text for whether an error is encountered and alerts for any case
        warningText = new Text();
    }

    /**
     * Will return a participant if all the fields have been filled up and no rules were broken
     */
    @FXML
    void addParticipantToEvent(ActionEvent event) {
        String checkerString = checkAnyFieldIsEmpty();
        if(!checkerString.isEmpty()) {
            displayError(checkerString);
            return;
        }

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

    /**
     * Checks for any empty field.
     * @return Any string for an empty field or an empty string if no errors were found
     */
    private String checkAnyFieldIsEmpty() {
        if (name.getText().isEmpty()) {
            System.out.println("Name field is empty warning displayed");
            warningText.setText("Name field is empty");
            return "Name";
        }
        if (email.getText().isEmpty()) {
            System.out.println("Email field is empty warning displayed");
            warningText.setText("Email field is empty");
            return "Email";
        }
        if (iban.getText().isEmpty()) {
            System.out.println("IBAN field is empty warning displayed");
            warningText.setText("IBAN field is empty");
            return "IBAN";
        }
        if (bic.getText().isEmpty()) {
            System.out.println("BIC field is empty warning displayed");
            warningText.setText("BIC field is empty");
            return "BIC";
        }
        return "";
    }

    public void displayError(String cause){
        errorAlert = new Alert(AlertType.ERROR);
        switch(cause){
            case "Name" -> {
                errorAlert.setContentText("Name field cannot be empty!");
            }
            case "Email" -> {
                errorAlert.setContentText("Email field cannot be empty!");
            }
            case "IBAN" -> {
                errorAlert.setContentText("IBAN field cannot be empty!");
            }
            case "BIC" -> {
                errorAlert.setContentText("BIC field cannot be empty!");
            }
            default -> {
                errorAlert.setContentText("Unrecognized field... An Unknown error occurred");
            }
        }
        errorAlert.show();
    }

    @FXML
    public void close(ActionEvent e){
        System.out.println("Closing Addparticipants window...");
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

