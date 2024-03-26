package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
//import javafx.fxml.Initializable;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import static client.scenes.MainPageCtrl.currentLocale;

public class LanguageTemplateCtrl implements Controller, Initializable {
    private ServerUtils server;

    private Stage stage;

    ResourceBundle resourceBundle;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private Label overridesLanguageText;

    @FXML
    private Label whatTheLanguageCalledText;

    @FXML
    private Label pleaseFillInText;

    @FXML
    private Label joinEventText;

    @FXML
    private Label joinText;

    @FXML
    private Label recentEventsText;

    @FXML
    private Label createText;

    @FXML
    private Label createNewEventText;

    @FXML
    private Label adminText;

    @FXML
    private TextField joinEventInput;

    @FXML
    private TextField joinInput;

    @FXML
    private TextField recentEventsInput;

    @FXML
    private TextField createInput;

    @FXML
    private TextField createNewEventInput;

    @FXML
    private TextField adminInput;


    private void setUpLanguage(){
        resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        overridesLanguageText.setText(resourceBundle.getString("overridesLanguageText"));
        whatTheLanguageCalledText.setText(resourceBundle.getString("whatTheLanguageCalledText"));
        pleaseFillInText.setText(resourceBundle.getString("pleaseFillInText"));
        joinEventText.setText(resourceBundle.getString("joinEventText"));
        joinText.setText(resourceBundle.getString("joinText"));
        recentEventsText.setText(resourceBundle.getString("recentEventsText"));
        createText.setText(resourceBundle.getString("createText"));
        adminText.setText(resourceBundle.getString("adminText"));
        createNewEventText.setText(resourceBundle.getString("createNewEventText"));
        adminText.setText(resourceBundle.getString("adminText"));
        adminText.setText(resourceBundle.getString("adminText"));
        adminText.setText(resourceBundle.getString("adminText"));
        adminText.setText(resourceBundle.getString("adminText"));
        adminText.setText(resourceBundle.getString("adminText"));
        adminText.setText(resourceBundle.getString("adminText"));
        adminText.setText(resourceBundle.getString("adminText"));
        adminText.setText(resourceBundle.getString("adminText"));
        adminText.setText(resourceBundle.getString("adminText"));
        adminText.setText(resourceBundle.getString("adminText"));

    }
    @Inject
    public LanguageTemplateCtrl(ServerUtils server) {
        this.server = server;
    }

    private void eventListenerInit(){
        cancelButton.setOnMouseClicked(e->{
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            MainPageCtrl mainPageCtrl = new MainPageCtrl(server);
            mainCtrl.initialize(stage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());
        });

        saveButton.setOnMouseClicked(e->{
            String path =
                    "C:\\dsk\\OOPP\\GroupProject\\oopp-team-33\\client\\src\\main\\resources\\messages_xx.properties";

            Properties props = new Properties();

            try {
                // Load the properties file
                FileInputStream in = new FileInputStream(path);
                props.load(in);
                in.close();

                // Change the value of a key
                props.setProperty("joinEventText", joinEventInput.getText());
                props.setProperty("joinText", joinInput.getText());
                props.setProperty("recentEventsText", recentEventsInput.getText());
                props.setProperty("createText", createInput.getText());
                props.setProperty("createNewEventText", createNewEventInput.getText());
                props.setProperty("adminText", adminInput.getText());


                // Save changes back to the properties file
                FileOutputStream out = new FileOutputStream(path);
                props.store(out, "Updated properties");
                out.close();

                System.out.println("Properties file updated successfully.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("in template");
        setUpLanguage();
        eventListenerInit();
    }
    @Override
    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "LanguageTemplate.fxml");
    }

    @Override
    public String getTitle() {
        return "Language Template";
    }


}
