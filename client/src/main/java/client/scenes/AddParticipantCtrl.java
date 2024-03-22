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
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    @Inject
    public AddParticipantCtrl(ServerUtils server){
        this.server = server;
    }

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
    void addParticipantToEvent(ActionEvent event) {
        if (name.getText().isEmpty()) {
            System.out.println("Name field is empty warning displayed");
            warningText.setText("Name field is empty");
            return;
        }
        if (email.getText().isEmpty()) {
            System.out.println("Email field is empty warning displayed");
            warningText.setText("Email field is empty");
            return;
        }
        if (iban.getText().isEmpty()) {
            System.out.println("IBAN field is empty warning displayed");
            warningText.setText("IBAN field is empty");
            return;
        }
        if (bic.getText().isEmpty()) {
            System.out.println("BIC field is empty warning displayed");
            warningText.setText("BIC field is empty");
            return;
        }
        Participant newParticipant = new Participant(name.getText(), email.getText(), iban.getText(), bic.getText());
        try {
            server.addParticipant(newParticipant);
        } catch (WebApplicationException e) {
            System.out.println("Error inserting participant into the database: " + e.getMessage());
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

