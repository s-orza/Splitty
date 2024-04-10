package client.scenes;

import java.util.ResourceBundle;
import client.utils.ServerUtils;
import commons.Participant;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.PauseTransition;
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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    @FXML
    private AnchorPane backGround;
    @FXML
    private Label addParticipantText;
    @FXML
    private Label nameText;
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
        backgroundImage();
        // initializing warning Text for whether an error is encountered and alerts for any case
        keyShortCuts();
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
            addParticipantText.setText(resourceBundle.getString("addParticipantText"));
            nameText.setText(resourceBundle.getString("nameText"));
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }

    private void keyShortCuts() {
        name.requestFocus();

        name.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.ENTER||
            event.getCode()==KeyCode.DOWN) email.requestFocus();
        });
        email.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.ENTER||
                    event.getCode()==KeyCode.DOWN) iban.requestFocus();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) name.requestFocus();
        });
        iban.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.ENTER||
                    event.getCode()==KeyCode.DOWN) bic.requestFocus();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) email.requestFocus();
        });
        bic.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.ENTER||
                    event.getCode()==KeyCode.DOWN) addButton.requestFocus();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) iban.requestFocus();
        });
        addButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) addButton.fire();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) bic.requestFocus();
        });
        cancelButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) cancelButton.fire();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) addButton.requestFocus();
        });
    }

    private void backgroundImage() {
        Image image = new Image("Background_Photo.jpg");
        BackgroundSize backgroundSize =
                new BackgroundSize(720, 450, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                backgroundSize);
        Background background = new Background(backgroundImage);
        backGround.setBackground(background);
    }
    /**
     * Will return a participant if all the fields have been filled up and no rules were broken
     */
    @FXML
    void addParticipant(ActionEvent event) {
        checkFieldsCondition();

        // read all text from fields and create a new participant
        Participant newParticipant = takeParticipantFromFields();
        try {
            //TODO
            // make the eventID be specific to each event
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(e -> {
                showPopup();
            });
            delay.play();

            String destination = "/app/participant/event/" + server.getCurrentId();
            server.sendParticipant(destination, newParticipant);
            close(event);
        } catch (WebApplicationException e) {
            System.out.println("Error inserting participant into the database: " + e.getMessage());
        }
    }

    private void showPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Added Participant Successfully");
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 350, 20);
        popupStage.setScene(scene);
        popupStage.show();
    }
    @FXML
    void updateParticipant(ActionEvent event) {
        if(!checkFieldsCondition())
            return;
        // reload again the participant to be sure that it is the newest participant
        // can also prevent a bug where another user has deleted the participant you are already working on
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
        Participant participant = takeParticipantFromFields();
        System.out.println(participant);
        //get participant to be modified
//        participant = server.getParticipant(participantToBeModified.getParticipantID());
        //modify the participant and save it in the database
        System.out.println(participantToBeModified.getParticipantID());
        server.updateParticipant(participantToBeModified.getParticipantID(), participant);
        participantToBeModified=null;
        server.setParticipantToBeModified(-1);

        close(event);
    }

    private Participant takeParticipantFromFields()
    {
        // get and return a new participant from the text fields
        return new Participant(name.getText(), email.getText(), iban.getText(), bic.getText());
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

