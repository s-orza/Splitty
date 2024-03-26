package client.scenes;

import client.utils.ServerUtils;
import commons.*;
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

public class AddExpenseCtrl implements Controller{

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
    private ObservableList<Pair2> names;// = FXCollections.observableArrayList(
            //new Pair2("Serban",0), new Pair2("David",1),
            //new Pair2("Olav",2), new Pair2("Alex",3));
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
        participantsObjectList=server.getParticipantsOfEvent(server.getCurrentId());
        names=FXCollections.observableArrayList();
        int k=0;
        for(Participant person:participantsObjectList)
        {
            names.add(new Pair2(person.getName(),k));
            k++;
        }
        //initialise the expenseTypesAvailable
        expenseTypesAvailable.clear();
        expenseTypesAvailable.addAll(List.of("EUR", "USD", "RON", "CHF"));
        //initialise the tags
        tagsAvailable=new ArrayList<>();
        long eventId= server.getCurrentId();
        //adding the 4 tags that always need to be
        if(!server.checkIfTagExists("other", eventId))
            server.addTag(new Tag(new TagId("other",eventId),"#e0e0e0"));

        if(!server.checkIfTagExists("food", eventId))
            server.addTag(new Tag(new TagId("food",eventId),"#00ff00"));

        if(!server.checkIfTagExists("entrance fees", eventId))
            server.addTag(new Tag(new TagId("entrance fees",eventId),"#0000ff"));

        if(!server.checkIfTagExists("travel", eventId))
            server.addTag(new Tag(new TagId("travel",eventId),"#ff0000"));

        List<Tag> temp=server.getAllTagsFromEvent(eventId);
        System.out.println(temp);
        for(Tag t:temp)
            tagsAvailable.add(t.getId().getName());
    }
    /**
     * This is a function that resets and prepare the scene.
     * This resets the elements on screen with what we have from the database
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
        //without these 2 lines, the list will be buggy, and you won t be able to select participants
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
        moneyTypeSelector.setValue(expense.getType().getName());
        //reload date
            //magic formula needs to be found
        //reload type
        typeSelector.setValue(expense.getType().getName());
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
        if(!inputIsCorrect())
            return;
        //the expense can be considered valid now
        warningText.setText("");
        String content=contentBox.getText();
        double money=Double.parseDouble(moneyPaid.getText());
        String dateString=date.getValue().getDayOfMonth()+","+
                date.getValue().getMonthValue()+","+
                date.getValue().getYear();
        //the expense
        //this will be the final author

        Participant authorP = participantsObjectList.get(authorSelector.getSelectionModel().getSelectedIndex());

        List<Participant> list=new ArrayList<>();
        //if we selected all participants
        if(checkBoxAllPeople.isSelected())
        {
            for(Participant p:participantsObjectList)
                list.add(p);
        }
        else
            //then, if some participants are selected
            for(Integer p:selectedNamesList)
            {
                list.add(participantsObjectList.get(p));
            }
        Expense expense=new Expense(authorP,content,money,moneyTypeSelector.getValue(),
                dateString,list, server.getTagByIdOfEvent(typeSelector.getValue(), server.getCurrentId()).getId());
        System.out.println(expense);
        System.out.println("Adding to event id" + server.getCurrentId());
        long id= server.getCurrentId();
        //if we just add an expense, this will be null
        if(expenseToBeModified==null)
        {
            server.addExpenseToEvent(id,expense);
            //add debts
            //we know there is at least one participant.
            double split=expense.getMoney()/expense.getParticipants().size();
            System.out.println("The selected persons need to pay: "+split);
            double authorNeedsToReceive=0;
            double othersNeedsToGive=split;
            Event currentEvent=server.getEvent(server.getCurrentId());
            //if the author is included
            if(expense.getParticipants().contains(expense.getAuthor()))
            {
                authorNeedsToReceive=expense.getMoney()-split;
                for(Participant p:expense.getParticipants())
                {
                    //update debs from p to author
                    if(p.getParticipantID()!=expense.getAuthor().getParticipantID())
                    {
                        System.out.println(p.getName() +" gives "+othersNeedsToGive+" to "
                                +expense.getAuthor().getName());
                        server.addDebtToEvent(server.getCurrentId(),new Debt(othersNeedsToGive,
                                expense.getCurrency(),p.getParticipantID(),expense.getAuthor().getParticipantID()));
                    }
                }
            }
            else
            {
                //the author need to receive all the money
                authorNeedsToReceive=expense.getMoney();
                System.out.println("ev: "+currentEvent);
                for(Participant p:expense.getParticipants())
                {
                    //update debs from p to author
                    System.out.println(p.getName() +" gives "+othersNeedsToGive+" to "
                            +expense.getAuthor().getName());

                    server.addDebtToEvent(server.getCurrentId(),new Debt(othersNeedsToGive,
                            expense.getCurrency(),p.getParticipantID(),expense.getAuthor().getParticipantID()));
                }

            }

        }
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
        //go back to the event page
        EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());

    }

    /**
     * This function verifies if the input is correct.
     * @return true if the input is correct
     */
    private boolean inputIsCorrect()
    {
        if(authorSelector.getValue()==null || authorSelector.getValue().isEmpty())
        {
            System.out.println("The author cannot be empty.");
            warningText.setText(resourceBundle.getString("authorWarning"));
            return false;
        }
        if(contentBox.getText()==null || contentBox.getText().isEmpty())
        {
            System.out.println("What is the money for?");
            warningText.setText(resourceBundle.getString("forWhatWarning"));
            return false;
        }
        if(moneyPaid.getText()==null || moneyPaid.getText().isEmpty())
        {
            System.out.println(moneyPaid.getText());
            warningText.setText(resourceBundle.getString("amountWarning"));
            return false;
        }
        if(moneyPaid.getText().contains("-") || moneyPaid.getText().equals("0"))
        {
            warningText.setText(resourceBundle.getString("negativeAmountWarning"));
            return false;
        }
        if(Double.parseDouble(moneyPaid.getText())==0.0)
        {
            warningText.setText(resourceBundle.getString("negativeAmountWarning"));
            return false;
        }
        if(date.getValue()==null) {
            System.out.println("You need to select a date!");
            warningText.setText(resourceBundle.getString("dateWarning"));
            return false;
        }
        if(typeSelector.getValue()==null || typeSelector.getValue().isEmpty())
        {
            System.out.println("You need to enter the type");
            warningText.setText(resourceBundle.getString("typeWarning"));
            return false;
        }
        if(!checkBoxAllPeople.isSelected() && !checkBoxSomePeople.isSelected())
        {
            System.out.println("Select how to split the expense.");
            warningText.setText(resourceBundle.getString("splitWarning"));
            return false;
        }
        if(checkBoxSomePeople.isSelected() && selectedNamesList.isEmpty())
        {
            System.out.println("Select the participants.");
            warningText.setText(resourceBundle.getString("selectParticipantsWarning"));
            return false;
        }
        return true;
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
        EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
        mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
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
        if(server.checkIfTagExists(tagName,server.getCurrentId()))
        {
            System.out.println("Already in the database!");
            return;
        }
        String color=colorPicker.getValue().toString();
        server.addTag(new Tag(new TagId(tagName,server.getCurrentId()),"#"+color.substring(2,8)));
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
    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "AddExpense.fxml");
    }
    public String getTitle(){
        return "Add Expense";
    }
}
