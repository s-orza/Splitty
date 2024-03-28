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

    @FXML
    private Label titleText;
    @FXML
    private Label joinCodeText;
    @FXML
    private Label cancelText;
    @FXML
    private Label eventNameText;
    @FXML
    private Label eventCodeText;
    @FXML
    private Label addExpenseText;

    @FXML
    private TextField titleInput;

    @FXML
    private TextField joinCodeInput;

    @FXML
    private TextField cancelInput;

    @FXML
    private TextField eventNameInput;

    @FXML
    private TextField eventCodeInput;

    @FXML
    private TextField addExpenseInput;
    @FXML
    private Label removeExpenseText;
    @FXML
    private Label authorText;
    @FXML
    private Label descriptionText;
    @FXML
    private Label amountText;
    @FXML
    private Label currencyText;
    @FXML
    private Label participantsText;

    @FXML
    private TextField removeExpenseInput;

    @FXML
    private TextField authorInput;

    @FXML
    private TextField descriptionInput;

    @FXML
    private TextField amountInput;

    @FXML
    private TextField currencyInput;

    @FXML
    private TextField participantsInput;
    @FXML
    private Label dateText;
    @FXML
    private Label typeText;
    @FXML
    private Label addParticipantText;
    @FXML
    private Label editEventNameText;
    @FXML
    private Label viewDebtsText;
    @FXML
    private Label passwordText;

    @FXML
    private TextField dateInput;

    @FXML
    private TextField typeInput;

    @FXML
    private TextField addParticipantInput;

    @FXML
    private TextField editEventNameInput;

    @FXML
    private TextField viewDebtsInput;

    @FXML
    private TextField passwordInput;
    @FXML
    private Label loginText;
    @FXML
    private Label addEditExpenseText;
    @FXML
    private Label whoPaidText;
    @FXML
    private Label forWhatText;
    @FXML
    private Label howMuchText;
    @FXML
    private Label whenText;

    @FXML
    private TextField loginInput;

    @FXML
    private TextField addEditExpenseInput;

    @FXML
    private TextField whoPaidInput;

    @FXML
    private TextField forWhatInput;

    @FXML
    private TextField howMuchInput;

    @FXML
    private TextField whenInput;
    @FXML
    private Label expenseTypeText;
    @FXML
    private Label howToSplitText;
    @FXML
    private Label ebeText;
    @FXML
    private Label obspText;
    @FXML
    private Label selectPersonText;
    @FXML
    private Label cantText;

    @FXML
    private TextField expenseTypeInput;

    @FXML
    private TextField howToSplitInput;

    @FXML
    private TextField ebeInput;

    @FXML
    private TextField obspInput;

    @FXML
    private TextField selectPersonInput;

    @FXML
    private TextField cantInput;
    @FXML
    private Label addText;
    @FXML
    private Label authorWarning;
    @FXML
    private Label forWhatWarning;
    @FXML
    private Label amountWarning;
    @FXML
    private Label negativeAmountWarning;
    @FXML
    private Label dateWarning;

    @FXML
    private TextField addInput;

    @FXML
    private TextField authorWarningInput;

    @FXML
    private TextField forWhatWarningInput;

    @FXML
    private TextField amountWarningInput;

    @FXML
    private TextField negativeAmountWarningInput;

    @FXML
    private TextField dateWarningInput;


    @FXML
    private Label typeWarning;
    @FXML
    private Label splitWarning;
    @FXML
    private Label selectParticipantsWarning;
    @FXML
    private Label removeExpenseQuestionText;
    @FXML
    private Label removeText;
    @FXML
    private Label removeExpenseTitle;
    @FXML
    private Label selectTypeText;
    @FXML
    private TextField typeWarningInput;

    @FXML
    private TextField splitWarningInput;

    @FXML
    private TextField selectParticipantsWarningInput;

    @FXML
    private TextField removeExpenseQuestionInput;

    @FXML
    private TextField removeInput;

    @FXML
    private TextField removeExpenseTitleInput;

    @FXML
    private TextField selectTypeInput;


    private void setUpLanguage(){
        resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        overridesLanguageText.setText(resourceBundle.getString("overridesLanguageText"));
        whatTheLanguageCalledText.setText(resourceBundle.getString("whatTheLanguageCalledText"));
        pleaseFillInText.setText(resourceBundle.getString("pleaseFillInText"));
//        joinEventText.setText(resourceBundle.getString("joinEventText"));
//        joinText.setText(resourceBundle.getString("joinText"));
//        recentEventsText.setText(resourceBundle.getString("recentEventsText"));
//        createText.setText(resourceBundle.getString("createText"));
//        adminText.setText(resourceBundle.getString("adminText"));
//        createNewEventText.setText(resourceBundle.getString("createNewEventText"));
//        adminText.setText(resourceBundle.getString("adminText"));

        joinEventText.setText(resourceBundle.getString("joinEventText"));
        createNewEventText.setText(resourceBundle.getString("createNewEventText"));
        createText.setText(resourceBundle.getString("createText"));
        joinText.setText(resourceBundle.getString("joinText"));
        adminText.setText(resourceBundle.getString("adminText"));
        recentEventsText.setText(resourceBundle.getString("recentEventsText"));
        titleText.setText(resourceBundle.getString("titleText"));
        joinCodeText.setText(resourceBundle.getString("joinCodeText"));
        cancelText.setText(resourceBundle.getString("cancelText"));
        eventNameText.setText(resourceBundle.getString("eventNameText"));
        eventCodeText.setText(resourceBundle.getString("eventCodeText"));
        addExpenseText.setText(resourceBundle.getString("addExpenseText"));
        removeExpenseText.setText(resourceBundle.getString("removeExpenseText"));
        authorText.setText(resourceBundle.getString("authorText"));
        descriptionText.setText(resourceBundle.getString("descriptionText"));
        amountText.setText(resourceBundle.getString("amountText"));
        currencyText.setText(resourceBundle.getString("currencyText"));
        participantsText.setText(resourceBundle.getString("participantsText"));
        dateText.setText(resourceBundle.getString("dateText"));
        typeText.setText(resourceBundle.getString("typeText"));
        addParticipantText.setText(resourceBundle.getString("addParticipantText"));
        editEventNameText.setText(resourceBundle.getString("editEventNameText"));
        viewDebtsText.setText(resourceBundle.getString("viewDebtsText"));
        passwordText.setText(resourceBundle.getString("passwordText"));
        loginText.setText(resourceBundle.getString("loginText"));
        addEditExpenseText.setText(resourceBundle.getString("addEditExpenseText"));
        whoPaidText.setText(resourceBundle.getString("whoPaidText"));
        forWhatText.setText(resourceBundle.getString("forWhatText"));
        howMuchText.setText(resourceBundle.getString("howMuchText"));
        whenText.setText(resourceBundle.getString("whenText"));
        expenseTypeText.setText(resourceBundle.getString("expenseTypeText"));
        howToSplitText.setText(resourceBundle.getString("howToSplitText"));
        ebeText.setText(resourceBundle.getString("ebeText"));
        obspText.setText(resourceBundle.getString("obspText"));
        selectPersonText.setText(resourceBundle.getString("selectPersonText"));
        cantText.setText(resourceBundle.getString("cantText"));
        addText.setText(resourceBundle.getString("addText"));
        authorWarning.setText(resourceBundle.getString("authorWarning"));
        forWhatWarning.setText(resourceBundle.getString("forWhatWarning"));
        amountWarning.setText(resourceBundle.getString("amountWarning"));
        negativeAmountWarning.setText(resourceBundle.getString("negativeAmountWarning"));
        dateWarning.setText(resourceBundle.getString("dateWarning"));
        typeWarning.setText(resourceBundle.getString("typeWarning"));
        splitWarning.setText(resourceBundle.getString("splitWarning"));
        selectParticipantsWarning.setText(resourceBundle.getString("selectParticipantsWarning"));
        removeExpenseQuestionText.setText(resourceBundle.getString("removeExpenseQuestionText"));
        removeText.setText(resourceBundle.getString("removeText"));
        removeExpenseTitle.setText(resourceBundle.getString("removeExpenseTitle"));
        selectTypeText.setText(resourceBundle.getString("selectTypeText"));



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

                // Set properties based on user input
                props.setProperty("joinEventText", joinEventInput.getText());
                props.setProperty("createNewEventText", createNewEventInput.getText());
                props.setProperty("createText", createInput.getText());
                props.setProperty("joinText", joinInput.getText());
                props.setProperty("adminText", adminInput.getText());
                props.setProperty("recentEventsText", recentEventsInput.getText());
                props.setProperty("titleText", titleInput.getText());
                props.setProperty("joinCodeText", joinCodeInput.getText());
                props.setProperty("cancelText", cancelInput.getText());
                props.setProperty("eventNameText", eventNameInput.getText());
                props.setProperty("eventCodeText", eventCodeInput.getText());
                props.setProperty("addExpenseText", addExpenseInput.getText());
                props.setProperty("removeExpenseText", removeExpenseInput.getText());
                props.setProperty("authorText", authorInput.getText());
                props.setProperty("descriptionText", descriptionInput.getText());
                props.setProperty("amountText", amountInput.getText());
                props.setProperty("currencyText", currencyInput.getText());
                props.setProperty("participantsText", participantsInput.getText());
                props.setProperty("dateText", dateInput.getText());
                props.setProperty("typeText", typeInput.getText());
                props.setProperty("addParticipantText", addParticipantInput.getText());
                props.setProperty("editEventNameText", editEventNameInput.getText());
                props.setProperty("viewDebtsText", viewDebtsInput.getText());
                props.setProperty("passwordText", passwordInput.getText());
                props.setProperty("loginText", loginInput.getText());
                props.setProperty("addEditExpenseText", addEditExpenseInput.getText());
                props.setProperty("whoPaidText", whoPaidInput.getText());
                props.setProperty("forWhatText", forWhatInput.getText());
                props.setProperty("howMuchText", howMuchInput.getText());
                props.setProperty("whenText", whenInput.getText());
                props.setProperty("expenseTypeText", expenseTypeInput.getText());
                props.setProperty("howToSplitText", howToSplitInput.getText());
                props.setProperty("ebeText", ebeInput.getText());
                props.setProperty("obspText", obspInput.getText());
                props.setProperty("selectPersonText", selectPersonInput.getText());
                props.setProperty("cantText", cantInput.getText());
                props.setProperty("addText", addInput.getText());
                props.setProperty("authorWarning", authorWarningInput.getText());
                props.setProperty("forWhatWarning", forWhatWarningInput.getText());
                props.setProperty("amountWarning", amountWarningInput.getText());
                props.setProperty("negativeAmountWarning", negativeAmountWarningInput.getText());
                props.setProperty("dateWarning", dateWarningInput.getText());
                props.setProperty("typeWarning", typeWarningInput.getText());
                props.setProperty("splitWarning", splitWarningInput.getText());
                props.setProperty("selectParticipantsWarning", selectParticipantsWarningInput.getText());
                props.setProperty("removeExpenseQuestionText", removeExpenseQuestionInput.getText());
                props.setProperty("removeText", removeInput.getText());
                props.setProperty("removeExpenseTitle", removeExpenseTitleInput.getText());
                props.setProperty("selectTypeText", selectTypeInput.getText());




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
