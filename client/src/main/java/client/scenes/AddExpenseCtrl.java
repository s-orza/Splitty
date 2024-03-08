package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import com.google.inject.Inject;
import javafx.scene.text.Text;


import java.util.ArrayList;
import java.util.List;

import static com.google.inject.Guice.createInjector;

public class AddExpenseCtrl implements Controller{
    //Imports used to swap scenes
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);
    private final ServerUtils server;
    private Stage stage;

    @FXML
    private Button addButton;
    @FXML
    private Button addTypeButton;
    @FXML
    private TextField newTypeTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField contentBox;

    @FXML
    private CheckBox checkBoxAllPeople;

    @FXML
    private CheckBox checkBoxSomePeople;

    @FXML
    private DatePicker date;
    @FXML
    private ColorPicker colorPicker;

    @FXML
    private TextField moneyPaid;

    @FXML
    private ComboBox<String> moneyTypeSelector;

    @FXML
    private ComboBox<String> authorSelector;

    @FXML
    private ComboBox<String> typeSelector;
    @FXML
    private ListView<String> namesList;//names showed on screen from which we select
    @FXML
    private Text warningText;
    private List<String> selectedNamesList=new ArrayList<>();
    private ObservableList<String> names = FXCollections.observableArrayList(
            "Serban","David","Olav","Alex");
    private List<String> expenseTypesAvailable=new ArrayList<>();
    private List<String> tagsAvailable;
    @Inject
    public AddExpenseCtrl(ServerUtils server) {
        this.server = server;
    }
    @FXML
    public void initialize() {
        //first we need to create a list with the names of the participants:
        //->here to put code for creating the list:
        //names=...
        selectedNamesList = new ArrayList<>();
        //initialise the expenseTypesAvailable
        expenseTypesAvailable.clear();
        expenseTypesAvailable.addAll(List.of("EUR", "USD", "RON", "CHF"));
        //initialise the warning text
        warningText.setText("");

        //initialise the tags
        tagsAvailable=new ArrayList<>();
        // tagsAvailable= ->import from database all the tags<-
            tagsAvailable.add("other");


        //reset and load
        resetElements();
    }
    /**
     * This is a function that resets and prepare the scene.
     * You should call this function everytime you open AddExpense page.
     */
    public void resetElements(){
        //reset the scene
        //initialise the warning text
        warningText.setText("");
        //prepare the possible authors
        authorSelector.getItems().clear();
        authorSelector.setPromptText("-Select person");
        authorSelector.setValue(null);
        authorSelector.getItems().addAll(names);
        authorSelector.setOnAction(this::handleSelectAuthor);

        //content of the expense
        contentBox.setText("");
        moneyPaid.clear();
        moneyPaid.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d*(\\.\\d*)?")) {
                moneyPaid.setText(oldValue);
            }
        });
        moneyPaid.setText("");

        //for handling money type
        moneyTypeSelector.getItems().clear();
        moneyTypeSelector.getItems().addAll(expenseTypesAvailable);
        moneyTypeSelector.setValue("EUR");//setting EUR as the default value
        moneyTypeSelector.setOnAction(this::handleCurrencySelection);
        ///date
        date.setValue(null);
        //reset the checkBoxes
        checkBoxSomePeople.setSelected(false);
        checkBoxAllPeople.setSelected(false);
        //create the list view with "names"
        namesList.setItems(names);
        namesList.setCellFactory(param -> new CheckBoxListCell());
        //at the beginning the list is hidden
        namesList.setVisible(false);


        typeSelector.getItems().clear();
        typeSelector.setPromptText("-Select type-");
        typeSelector.setValue(null);
        typeSelector.getItems().addAll(tagsAvailable);
    }
    private class CheckBoxListCell extends ListCell<String>{
        private CheckBox checkBox;
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item,empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                checkBox=new CheckBox(item);//we create a checkbox with that name
                checkBox.setOnAction(event -> handleCheckBoxSelectName(checkBox));
                setGraphic(checkBox);
            }
        }
    }

    /**
     * this function will be called when you press the add Button.
     * @param event an event
     */
    @FXML
    void addExpenseToTheEvent(MouseEvent event) {
        //we need to verify if the expense is valid.
        if(authorSelector.getValue()==null || authorSelector.getValue().isEmpty())
        {
            System.out.println("The author cannot be empty.");
            warningText.setText("The author cannot be empty.");
            return;
        }
        if(contentBox.getText()==null || contentBox.getText().isEmpty())
        {
            System.out.println("What is the money for?");
            warningText.setText("What is the money for?");
            return;
        }
        if(moneyPaid.getText()==null || moneyPaid.getText().isEmpty())
        {
            System.out.println(moneyPaid.getText());
            warningText.setText("The amount of money must be specified.");
            return;
        }
        if(date.getValue()==null) {
            System.out.println("You need to select a date!");
            warningText.setText("You need to select a date!");
            return;
        }
        if(typeSelector.getValue()==null || typeSelector.getValue().isEmpty())
        {
            System.out.println("You need to enter the type");
            warningText.setText("Enter the type please.");
            return;
        }
        if(!checkBoxAllPeople.isSelected() && !checkBoxSomePeople.isSelected())
        {
            System.out.println("Select how to split the expense.");
            warningText.setText("Select how to split the expense.");
            return;
        }
        if(checkBoxSomePeople.isSelected() && selectedNamesList.isEmpty())
        {
            System.out.println("Select the participants.");
            warningText.setText("Select the participants.");
            return;
        }
        //the expense can be considered valid now
        warningText.setText("");
        int year=date.getValue().getYear();
        String author=authorSelector.getValue();
        String content=contentBox.getText();
        int money=Integer.parseInt(moneyPaid.getText());
        String dateString=date.getValue().getDayOfMonth()+","+
                date.getValue().getMonthValue()+","+
                date.getValue().getYear();
        //the expense
        Participant pa=new Participant("a","b","c","d");
        pa.setParticipantID(3152);
        //I still need to adjust this list
        List<Participant> list=new ArrayList<>();
        list.add(pa);
        Expense expense=new Expense(pa,content,money,moneyTypeSelector.getValue(),
                dateString,null,typeSelector.getValue());
        System.out.println(expense);
        //the id is the id of the current event, we need to change
        long id=2;//??;
        server.addExpenseToEvent(id,expense);
        //System.out.println(server.getExpenseById(1));
        resetElements();
    }
    /**
     * this function will be called when you press the cancel Button.
     * @param e an event
     */
    @FXML
    void cancelAddExpense(MouseEvent e) {
        System.out.println("Expense canceled");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, EventPageCtrl.getPair(), "Event Page");
    }
    @FXML
    void createTag(MouseEvent event) {
        String tagName=newTypeTextField.getText();
        if(tagName==null || tagName.isEmpty())
        {
            System.out.println("Write something in the tag");
            return;
        }
        tagName=tagName.trim();
        if(server.checkIfTagExists(tagName))
        {
            System.out.println("Already in the database!");
            return;
        }
        String color=colorPicker.getValue().toString();
        server.addTag(new Tag(tagName,"#"+color.substring(2,8)));
        System.out.println("tag added");
        tagsAvailable.add(tagName);

        //reset tags from the screen
        typeSelector.getItems().clear();
        typeSelector.getItems().addAll(tagsAvailable);
        typeSelector.setPromptText("-Select type-");
        typeSelector.setValue(null);

        newTypeTextField.setText("");
    }
    void handleSelectAuthor(ActionEvent event)
    {
        System.out.println(authorSelector.getValue());
    }


    /**
     * This is a basic handler that checks when you check the box for
     * selecting all people
     * @param event an event
     */
    @FXML
    void handleCheckBoxAllPeople(ActionEvent event) {
        if(checkBoxAllPeople.isSelected()) {
            System.out.println("Everyone is selected!");
            //in case the user already selected the "some people" option
            checkBoxSomePeople.setSelected(false);
            //hide the list because we don't need to select
            namesList.setVisible(false);
        }
        else {
            System.out.println("everyone options is NOT selected!");
            //show the list of people available
        }
    }
    /**
     * This is a basic handler that checks when you change the currency type
     * @param event an event
     */
    @FXML
    private void handleCurrencySelection(ActionEvent event) {
        String selectedMoneyType=moneyTypeSelector.getValue();
        System.out.println(selectedMoneyType);
    }
    /**
     * This is a basic handler that checks when you check the box for
     * only selecting some of the people
     * @param event an event
     */
    @FXML
    void handleCheckBoxSomePeople(ActionEvent event) {
        if(checkBoxSomePeople.isSelected()) {
            namesList.setVisible(true);
            System.out.println("some people will be selected!");
            //in case the user already selected the "everyone" option
            checkBoxAllPeople.setSelected(false);
        }
        else {
            namesList.setVisible(false);
            System.out.println("Only some people option is NOT selected!");
            //show the list of people available
        }
    }

    /**
     * This handles when you check a checkBox of a person in the view list for selecting
     * @param checkBox the checkbox of a person who is selected/unselected
     */
    void handleCheckBoxSelectName(CheckBox checkBox){
        String name=checkBox.getText();
        if(checkBox.isSelected()){
            selectedNamesList.add(name);
        }
        else
            selectedNamesList.remove(name);
        System.out.println(selectedNamesList);
    }
    public static Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "AddExpense.fxml");
    }
    public static String getTitle(){
        return "Add Expense";
    }
}
