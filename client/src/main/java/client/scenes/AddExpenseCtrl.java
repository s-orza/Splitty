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
import java.util.ResourceBundle;

import static client.scenes.MainPageCtrl.currentLocale;
import static com.google.inject.Guice.createInjector;

public class AddExpenseCtrl implements Controller{
    //Imports used to swap scenes
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);
    private final ServerUtils server;
    private Stage stage;

    @FXML
    private Text addEditExpenseText;

    @FXML
    private Text whoPaidText;

    @FXML
    private Text forWhatText;

    @FXML
    private Text howMuchText;

    @FXML
    private Text whenText;

    @FXML
    private Text expenseTypeText;

    @FXML
    private Text howToSplitText;

    @FXML
    private Text cantText;

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
    private ListView<Pair2> namesList;//names showed on screen from which we select
    @FXML
    private Text warningText;
    private List<Integer> selectedNamesList=new ArrayList<>();
    private ObservableList<Pair2> names = FXCollections.observableArrayList(
            new Pair2("Serban",0), new Pair2("David",1),
            new Pair2("Olav",2), new Pair2("Alex",3));
    private List<String> expenseTypesAvailable=new ArrayList<>();
    private List<String> tagsAvailable;
    private List<Participant> participantsObjectList;
    private Expense expenseToBeModified=null;

    ResourceBundle resourceBundle;
    @Inject
    public AddExpenseCtrl(ServerUtils server) {
        this.server = server;
    }
    @FXML
    public void initialize() {
        //load resources
        loadFromDatabase();
        toggleLanguage();
        //it contains the positions of the selected participants (the position in participantObjectList
        selectedNamesList = new ArrayList<>();

        //reset
        resetElements();
    }

    private void toggleLanguage() {
        try{
            resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
            addEditExpenseText.setText(resourceBundle.getString("addEditExpenseText"));
            whoPaidText.setText(resourceBundle.getString("whoPaidText"));
            forWhatText.setText(resourceBundle.getString("forWhatText"));
            howMuchText.setText(resourceBundle.getString("howMuchText"));
            whenText.setText(resourceBundle.getString("whenText"));
            expenseTypeText.setText(resourceBundle.getString("expenseTypeText"));
            howToSplitText.setText(resourceBundle.getString("howToSplitText"));
            checkBoxAllPeople.setText(resourceBundle.getString("ebeText"));
            checkBoxSomePeople.setText(resourceBundle.getString("obspText"));
            cancelButton.setText(resourceBundle.getString("cancelText"));
            addButton.setText(resourceBundle.getString("addText"));
            cantText.setText(resourceBundle.getString("cantText"));
            whoPaidText.setText(resourceBundle.getString("whoPaidText"));
            whoPaidText.setText(resourceBundle.getString("whoPaidText"));
            authorSelector.setPromptText(resourceBundle.getString("selectPersonText"));
            typeSelector.setPromptText(resourceBundle.getString("selectTypeText"));
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void loadFromDatabase()
    {
        //first we need to create a list with the names of the participants:
        //->here to put code for creating the list
        //server.getAllParticipatns(eventId) or somrthing like that
        participantsObjectList=new ArrayList<>();
        //this "if" will be removed when we can access the participant list
        if(1==2)
        {
            int k=0;
            for(Participant person:participantsObjectList)
            {
                names.add(new Pair2(person.getName(),k));
                k++;
            }
        }
        //initialise the expenseTypesAvailable
        expenseTypesAvailable.clear();
        expenseTypesAvailable.addAll(List.of("EUR", "USD", "RON", "CHF"));
        //initialise the tags
        tagsAvailable=new ArrayList<>();
        if(server.checkIfTagExists("other")==false)
        {
            server.addTag(new Tag("other","#e0e0e0"));
        }
        List<Tag> temp=server.getAllTags();
        System.out.println(temp);
        for(Tag t:temp)
            tagsAvailable.add(t.getName());
    }
    /**
     * This is a function that resets and prepare the scene.
     * You should call this function everytime you open AddExpense page.
     */
    public void resetElements(){
        //reset the scene
        selectedNamesList = new ArrayList<>();
        //initialise the warning text
        warningText.setText("");
        //prepare the possible authors
        authorSelector.getItems().clear();

        authorSelector.setValue(null);
        authorSelector.getItems().addAll(names.stream().map(x->x.element).toList());
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
        //create the list view with indexes+ "names"
        namesList.setItems(names);
        namesList.setCellFactory(param -> new CheckBoxListCell());

        //at the beginning the list is hidden
        namesList.setVisible(false);
        //without these 2 lines, the list will be buggy and you won t be able to select participants
        //in this way, you cannot select the element,only the checkbox
        namesList.setSelectionModel(null);
        namesList.setFocusTraversable(false);

        typeSelector.getItems().clear();

        typeSelector.setValue(null);

        typeSelector.getItems().addAll(tagsAvailable);
        //this only makes sense for the edit part of an expense
        expenseToBeModified=null;
    }
    private class Pair2{
        public String element;
        public int index;

        public Pair2(String element, int index) {
            this.element = element;
            this.index = index;
        }


    }
    private class CheckBoxListCell extends ListCell<Pair2>{
        private CheckBox checkBox;
        public CheckBoxListCell() {
        }
        @Override
        protected void updateItem(Pair2 item, boolean empty) {
            super.updateItem(item,empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                checkBox=new CheckBox(item.element);//we create a checkbox with that name
                checkBox.setOnAction(event -> handleCheckBoxSelectName(checkBox, item.index));
                setGraphic(checkBox);
               // checkBox.setFocusTraversable(true);
            }
        }
    }

    /**
     * This function is to reload/get an expense from even in order to be modified.
     * @param expense the expense which will be modified
     */
    public void reloadExpense(Expense expense)
    {
        expenseToBeModified=expense;
        loadFromDatabase();
        resetElements();
        //reload author
        authorSelector.setValue(expense.getAuthor().getName());
        //reload content
        contentBox.setText(expense.getContent());
        //reload money
        moneyPaid.setText(expense.getMoney()+"");
        //reload money type
        moneyTypeSelector.setValue(expense.getType());
        //reload date
            //magic formula needs to be found
        //reload type
        typeSelector.setValue(expense.getType());
        //reload participants stuff
        //not yet available as we need participants, to be done in future

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
            warningText.setText(resourceBundle.getString("authorWarning"));
            return;
        }
        if(contentBox.getText()==null || contentBox.getText().isEmpty())
        {
            System.out.println("What is the money for?");
            warningText.setText(resourceBundle.getString("forWhatWarning"));
            return;
        }
        if(moneyPaid.getText()==null || moneyPaid.getText().isEmpty())
        {
            System.out.println(moneyPaid.getText());
            warningText.setText(resourceBundle.getString("amountWarning"));
            return;
        }
        if(moneyPaid.getText().contains("-") || moneyPaid.getText().equals("0"))
        {
            warningText.setText(resourceBundle.getString("negativeAmountWarning"));
            return;
        }
        if(date.getValue()==null) {
            System.out.println("You need to select a date!");
            warningText.setText(resourceBundle.getString("dateWarning"));
            return;
        }
        if(typeSelector.getValue()==null || typeSelector.getValue().isEmpty())
        {
            System.out.println("You need to enter the type");
            warningText.setText(resourceBundle.getString("typeWarning"));
            return;
        }
        if(!checkBoxAllPeople.isSelected() && !checkBoxSomePeople.isSelected())
        {
            System.out.println("Select how to split the expense.");
            warningText.setText(resourceBundle.getString("splitWarning"));
            return;
        }
        if(checkBoxSomePeople.isSelected() && selectedNamesList.isEmpty())
        {
            System.out.println("Select the participants.");
            warningText.setText(resourceBundle.getString("selectParticipantsWarning"));
            return;
        }
        //the expense can be considered valid now
        warningText.setText("");
        int year=date.getValue().getYear();
        String content=contentBox.getText();
        double money=Double.parseDouble(moneyPaid.getText());
        String dateString=date.getValue().getDayOfMonth()+","+
                date.getValue().getMonthValue()+","+
                date.getValue().getYear();
        //the expense
        Participant pa=new Participant("a","b","c","d");
        pa.setParticipantID(3152);
        //this will be the final author
        if(1==2) {
            Participant authorP = participantsObjectList.get(authorSelector.getSelectionModel().getSelectedIndex());
        }
        //I still need to adjust this list
        List<Participant> list=new ArrayList<>();
        //if we selected all participants
        if(checkBoxAllPeople.isSelected())
        {
            if(1==2)
            for(Participant p:participantsObjectList)
                list.add(p);
        }
        else
            //then, if some participants are selected
        //REMOVE THIS IF WHEN WE HAVE REAL PARTICIPANTS
        if(1==2)
        {
            for(Integer p:selectedNamesList)
            {
                list.add(participantsObjectList.get(p));
            }
        }
        else
            list.add(pa);
        Expense expense=new Expense(pa,content,money,moneyTypeSelector.getValue(),
                dateString,list,typeSelector.getValue());
        System.out.println(expense);
        //the id is the id of the current event, we need to change
        long id= EventPageCtrl.getCurrentEvent().getEventId();
        //if we just add an expense, this will be null
        if(expenseToBeModified==null)
            server.addExpenseToEvent(id,expense);
        else
        {
            //modify the expense and save it to tha database
            expenseToBeModified.setAuthor(expense.getAuthor());
            expenseToBeModified.setContent(expense.getContent());
            expenseToBeModified.setMoney(expense.getMoney());
            expenseToBeModified.setCurrency(expense.getCurrency());
            expenseToBeModified.setDate(expense.getDate());
            expenseToBeModified.setParticipants(expense.getParticipants());
            expenseToBeModified.setType(expense.getType());
            //save it
            server.updateExpense(expenseToBeModified.getExpenseId(),expenseToBeModified);
        }
        //System.out.println(server.getExpenseById(1));
        resetElements();
        //go back to event page
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, EventPageCtrl.getPair(), EventPageCtrl.getTitle());

    }
    /**
     * this function will be called when you press the cancel Button.
     * @param e an event
     */
    @FXML
    void cancelAddExpense(MouseEvent e) {
        resetElements();
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
        typeSelector.setPromptText(resourceBundle.getString("selectTypeText"));
        typeSelector.setValue(null);

        newTypeTextField.setText("");
    }
    void handleSelectAuthor(ActionEvent event)
    {
        System.out.println(authorSelector.getValue());
        //the position->good to get the author object
        System.out.println(authorSelector.getSelectionModel().getSelectedIndex());
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
    void handleCheckBoxSelectName(CheckBox checkBox,Integer index){
        String name=checkBox.getText();
        System.out.println(index+", "+name);
        if(checkBox.isSelected()){
            selectedNamesList.add(index);
        }
        else
            selectedNamesList.remove(index);
        System.out.println(selectedNamesList);
    }
    public static Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "AddExpense.fxml");
    }
    public static String getTitle(){
        return "Add Expense";
    }
}
