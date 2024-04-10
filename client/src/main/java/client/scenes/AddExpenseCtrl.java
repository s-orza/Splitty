package client.scenes;

import client.utils.ServerUtils;
import commons.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import com.google.inject.Inject;
import javafx.scene.text.Text;


import javax.swing.*;
import java.time.LocalDate;
import java.util.*;


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
    private Button saveButton;
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
    @FXML
    private AnchorPane backGround;
    @FXML
    private Button undoButton;
    //in this list are the persons that have their checkbox checked.
    private List<Integer> selectedNamesList=new ArrayList<>();
    private Stack<Map<String, String>> undoStack;
    private ObservableList<Pair2> names;// = FXCollections.observableArrayList(
            //new Pair2("Serban",0), new Pair2("David",1),
            //new Pair2("Olav",2), new Pair2("Alex",3));
    private List<String> expenseTypesAvailable=new ArrayList<>();
    private List<String> tagsAvailable;
    private List<Participant> participantsObjectList;
    private Expense expenseToBeModified;
    private final String[] tempContent = {""};
    private final String[] tempMoneyPaid = {""};
    private final String[] tempAuthor = {"-1"};
    private final String[] tempMoneyType = {"-1"};
    private final String[]  tempType = {"-1"};
    private final String[]  tempDate = {"-1"};
    private final String[] tempAllPeople = {"false"};
    private final String[] tempSomePeople= {"false"};

    ResourceBundle resourceBundle;
    @Inject
    public AddExpenseCtrl(ServerUtils server) {
        this.server = server;
    }
    @FXML
    public void initialize() {
        System.out.println("initializeeee");
        backgroundImage();
        keyShortCuts();
        //load resources
        loadFromDatabase();
        toggleLanguage();
        //undoFunction();
        //it contains the positions of the selected participants (the position in participantObjectList
        selectedNamesList = new ArrayList<>();

        //reset fields
        resetElements();
        //in case we are in the edit page
        if(server.getExIdToModify()!=-1)
        {
            expenseToBeModified=server.getExpenseToBeModified();
            reloadExpense();
        }
        //in add page
        else
        {
            addButton.setVisible(true);
            saveButton.setVisible(false);
        }
    }

    private void undoFunction() {
        undoStack = new Stack();
        contentBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!tempContent[0].equals(contentBox.getText())) {
                    Map<String, String> nameMap = new HashMap<>();
                    nameMap.put("contentBox", tempContent[0]);
                    undoStack.push(nameMap);
                    System.out.println("stack is " + undoStack.toString());
                    tempContent[0] = contentBox.getText();
                }
            }
        });
        moneyPaid.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!tempMoneyPaid[0].equals(moneyPaid.getText())) {
                    Map<String, String> nameMap = new HashMap<>();
                    nameMap.put("moneyPaid", tempMoneyPaid[0]);
                    undoStack.push(nameMap);
                    System.out.println("stack is " + undoStack.toString());
                    tempMoneyPaid[0] = moneyPaid.getText();
                }
            }
        });
        authorSelector.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if (!tempAuthor[0].equals(String.valueOf(authorSelector.getSelectionModel().getSelectedIndex()))) {
                    Map<String, String> nameMap = new HashMap<>();
                    nameMap.put("authorSelector",tempAuthor[0]);
                    undoStack.push(nameMap);
                    System.out.println("stack is " + undoStack.toString());
                    tempAuthor[0] = String.valueOf(authorSelector.getSelectionModel().getSelectedIndex());
                }

            }
        });
        moneyTypeSelector.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if (!tempMoneyType[0].equals(String.valueOf(moneyTypeSelector.getSelectionModel()
                        .getSelectedIndex()))) {
                    Map<String, String> nameMap = new HashMap<>();
                    nameMap.put("moneyTypeSelector",tempMoneyType[0]);
                    undoStack.push(nameMap);
                    System.out.println("stack is " + undoStack.toString());
                    tempMoneyType[0] = String.valueOf(moneyTypeSelector.getSelectionModel().getSelectedIndex());
                }

            }
        });
        typeSelector.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if (!tempType[0].equals(String.valueOf(typeSelector.getSelectionModel().getSelectedIndex()))) {
                    Map<String, String> nameMap = new HashMap<>();
                    nameMap.put("typeSelector",tempType[0]);
                    undoStack.push(nameMap);
                    System.out.println("stack is " + undoStack.toString());
                    tempType[0] = String.valueOf(typeSelector.getSelectionModel().getSelectedIndex());
                }

            }
        });
        date.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the new value is different from the old value
            if (newValue!=null)
                if ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) {
                if (!tempDate[0].equals(newValue.toString())) {

                    Map<String, String> nameMap = new HashMap<>();
                    nameMap.put("date", tempDate[0]);
                    undoStack.push(nameMap);
                    System.out.println("stack is " + undoStack.toString());
                    tempDate[0] = String.valueOf(newValue.toString());
                }
            }
        });
        checkBoxAllPeople.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the new value is different from the old value
            if (newValue!=null)
            if (!oldValue.equals(newValue)) {
                if (!tempAllPeople[0].equals(String.valueOf(checkBoxAllPeople.isSelected()))) {
                    Map<String, String> nameMap = new HashMap<>();
                    nameMap.put("checkBoxAllPeople",tempAllPeople[0]);
                    undoStack.push(nameMap);
                    System.out.println("stack is " + undoStack.toString());
                    tempAllPeople[0] = String.valueOf(checkBoxAllPeople.isSelected());
                }
            }
        });
        checkBoxSomePeople.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the new value is different from the old value
            if (newValue!=null)
            if (!oldValue.equals(newValue)) {
                if (!tempSomePeople[0].equals(String.valueOf(checkBoxSomePeople.isSelected()))) {
                    Map<String, String> nameMap = new HashMap<>();
                    nameMap.put("checkBoxSomePeople",tempSomePeople[0]);
                    undoStack.push(nameMap);
                    System.out.println("stack is " + undoStack.toString());
                    tempSomePeople[0] = String.valueOf(checkBoxSomePeople.isSelected());
                }
            }
        });

    }


    //shortcuts that when specific key is pressed while focusing on a part of the page, the right thing happens
    private void keyShortCuts() {
        authorSelector.requestFocus();

        backGround.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Scene scene = (backGround.getScene());
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        cancelButton.fire();
                    }
                });
            }
        });

        authorSelector.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) authorSelector.show();
            if (event.getCode() == KeyCode.RIGHT || event.getCode()==KeyCode.DOWN) {
                contentBox.requestFocus();
                event.consume();
            }
        });
        cancelButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) cancelButton.fire();
        });
        saveButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) saveButton.fire();
        });
        addButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addButton.fire();
                event.consume();
            }
        });
        contentBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER|| event.getCode()==KeyCode.DOWN) moneyPaid.requestFocus();
            if (event.getCode() == KeyCode.RIGHT) newTypeTextField.requestFocus();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) authorSelector.requestFocus();
        });
        moneyPaid.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.ENTER||
                    event.getCode()==KeyCode.DOWN) moneyTypeSelector.requestFocus();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) contentBox.requestFocus();
        });
        moneyTypeSelector.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode()==KeyCode.DOWN){
                date.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.ENTER) moneyTypeSelector.show();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) {
                moneyPaid.requestFocus();
                event.consume();
            }
        });
        date.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode()==KeyCode.DOWN) {
                typeSelector.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.ENTER) {
                date.show();
            }
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) {
                moneyTypeSelector.requestFocus();
                event.consume();
            }
        });
        newTypeTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode()==KeyCode.DOWN) colorPicker.requestFocus();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) contentBox.requestFocus();
        });
        colorPicker.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode()==KeyCode.DOWN) {
                addTypeButton.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.ENTER) colorPicker.show();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) {
                newTypeTextField.requestFocus();
                event.consume();
            }
        });
        addTypeButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode()==KeyCode.DOWN) contentBox.requestFocus();
            if (event.getCode() == KeyCode.ENTER) createTag(null);
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) newTypeTextField.requestFocus();
        });
        typeSelector.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode()==KeyCode.DOWN) {
                checkBoxAllPeople.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.ENTER) typeSelector.show();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) {
                date.requestFocus();
                event.consume();
            }
        });
        checkBoxAllPeople.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode()==KeyCode.DOWN) checkBoxSomePeople.requestFocus();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) typeSelector.requestFocus();
        });
        checkBoxSomePeople.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) namesList.requestFocus();
            if (event.getCode() == KeyCode.RIGHT || event.getCode()==KeyCode.DOWN) saveButton.requestFocus();
            if (event.getCode() == KeyCode.LEFT||event.getCode()==KeyCode.UP) checkBoxAllPeople.requestFocus();
        });

        undoButton.setOnMouseClicked(event -> {
            if (!undoStack.empty()) {
                System.out.println("Stack is " + undoStack.toString());
                Map result = undoStack.pop();
                System.out.println("popped was " + result.toString());
                String key = (String) result.keySet().iterator().next();
                switch (key) {
                    case "contentBox":
                        contentBox.setText((String) result.get(key));
                        tempContent[0] = "";
                        break;
                    case "moneyPaid":
                        moneyPaid.setText((String) result.get(key));
                        tempMoneyPaid[0] = "";
                        break;
                    case "authorSelector":
                        if (Integer.parseInt((String)result.get(key))==-1) {
                            authorSelector.getSelectionModel().clearSelection();
                        } else {
                            authorSelector.getSelectionModel().select(Integer.parseInt((String)result.get(key)));
                        }
                        tempAuthor[0]= "-1";
                        break;
                    case "moneyTypeSelector":
                        if (Integer.parseInt((String)result.get(key))==-1) {
                            moneyTypeSelector.getSelectionModel().clearSelection();
                        } else {
                            moneyTypeSelector.getSelectionModel().select(Integer.parseInt((String)result.get(key)));
                        }
                        tempMoneyType[0]= "-1";
                        break;
                    case "typeSelector":
                        if (Integer.parseInt((String)result.get(key))==-1) {
                            typeSelector.getSelectionModel().clearSelection();
                        } else {
                            typeSelector.getSelectionModel().select(Integer.parseInt((String)result.get(key)));
                        }
                        tempType[0]= "-1";
                        break;
                    case "date":
                        if (((String)result.get(key)).equals("-1")) {
                            date.setValue(null);
                        } else {
                            String dateString = (String)result.get(key);
                            LocalDate localDate = LocalDate.parse(dateString);
                            date.setValue(localDate);
                            undoStack.pop();
                        }
                        tempType[0]= "-1";
                        break;
                    case "checkBoxAllPeople":
                        checkBoxAllPeople.setSelected(Boolean.parseBoolean((String)result.get(key)));
                        tempAllPeople[0] = "false";
                        break;
                    case "checkBoxSomePeople":
                        checkBoxSomePeople.setSelected(Boolean.parseBoolean((String)result.get(key)));
                        tempSomePeople[0] = "false";
                        if (!Boolean.parseBoolean((String)result.get(key))) {
                            namesList.setVisible(false);
                        } else {
                            namesList.setVisible(true);
                        }
                        break;
                    default:
                        break;
                }
            }

        });
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
            e.printStackTrace();
        }
    }

    public void loadFromDatabase()
    {
        //first we need to create a list with the names of the participants:
        participantsObjectList=server.getParticipantsOfEvent(server.getCurrentId());
        System.out.println(server.getExIdToModify());
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
        moneyTypeSelector.setValue(MainCtrl.getCurrency());
        //moneyTypeSelector.setOnAction(this::handleCurrencySelection);
        ///date
        date.setValue(null);
        //reset the checkBoxes
        checkBoxSomePeople.setSelected(false);
        checkBoxAllPeople.setSelected(false);
        //create the list view with indexes+ "names"
        namesList.setItems(names);
        namesList.setCellFactory(param -> new CheckBoxListCell(false));

        //at the beginning the list is hidden
        namesList.setVisible(false);
        //without these 2 lines, the list will be buggy, and you won t be able to select participants
        //in this way, you cannot select the element,only the checkbox
        namesList.setSelectionModel(null);
        namesList.setFocusTraversable(false);

        typeSelector.getItems().clear();

        typeSelector.setValue(null);

        typeSelector.getItems().addAll(tagsAvailable);
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
        private boolean isOnEdit;
        public CheckBoxListCell(boolean isOnEdit) {
            this.isOnEdit=isOnEdit;
        }
        @Override
        protected void updateItem(Pair2 item, boolean empty) {
            super.updateItem(item,empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                checkBox=new CheckBox(item.element);//we create a checkbox with that name
                checkBox.setOnAction(event -> handleCheckBoxSelectName(checkBox, item.index));
                //if we are on the edit page we need to reload the selected participants
                if(isOnEdit && expenseToBeModified!=null)
                {
                    List<String> participantNames=expenseToBeModified.getParticipants().stream()
                            .map(x->x.getName()).toList();
                    //if the name is in the list
                    if(participantNames.contains(item.element))
                    {
                        checkBox.setSelected(true);
                        handleCheckBoxSelectName(checkBox,item.index);
                    }
                }
                else
                    checkBox.setSelected(false);
                setGraphic(checkBox);
               // checkBox.setFocusTraversable(true);
            }
        }
    }
    /**
     * This function is to reload/get an expense from even in order to be modified.
     */
    public void reloadExpense()
    {
        addButton.setVisible(false);
        saveButton.setVisible(true);
        //reload author
        authorSelector.setValue(expenseToBeModified.getAuthor().getName());
        //reload content
        contentBox.setText(expenseToBeModified.getContent());
        //reload money
        moneyPaid.setText(expenseToBeModified.getMoney()+"");
        //reload money type
        moneyTypeSelector.setValue(expenseToBeModified.getCurrency());
        //reload date
        try{
            LocalDate time = LocalDate.parse(expenseToBeModified.getDate());

           date.setValue(time);
        }catch (Exception e)
        {
            System.out.println("The date has a problem");
        }
        //reload type
        typeSelector.setValue(expenseToBeModified.getType());
        //reload participants stuff
        //if the expense has all the participants
        if(expenseToBeModified.getParticipants().size()==names.size())
        {
            checkBoxAllPeople.setSelected(true);
            //we only need to mark this as selected because the save/add function will take this into
            //consideration.
        }
        else
        {
            checkBoxSomePeople.setSelected(true);
            namesList.setVisible(true);

            selectedNamesList = new ArrayList<>();
            namesList.setCellFactory(null);
            namesList.setItems(null);
            namesList.setItems(names);
            namesList.setCellFactory(param -> new CheckBoxListCell(true));
        }

    }

    /**
     * this function will be called when you press the add Button.
     * @param event an event
     */
    @FXML
    void addExpenseToTheEvent(ActionEvent event) {
        //we need to verify if the expense is valid.
        if(!inputIsCorrect())
            return;
        //the expense can be considered valid now
        warningText.setText("");

        Expense expense=takeExpenseFromFields();

        System.out.println(expense);

        //server.addExpenseToEvent(server.getCurrentId(),expense);

        String destination = "/app/expenses/tag/" + String.valueOf(server.getCurrentId());
        server.sendExpense(destination,expense);

        createDebtsFromExpense(expense);

        resetElements();
        server.setExpenseToBeModified(-1);


        showPopup();


        //go back to the event page
        EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
    }
    private void showPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Created Expense Successfully");
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 350, 20);
        popupStage.setScene(scene);
        popupStage.show();
    }
    private Expense takeExpenseFromFields()
    {
        String content=contentBox.getText();
        double money=Double.parseDouble(moneyPaid.getText());
        String dateString=date.getValue().getYear()+"-";
        //if we need to add a "0"
        if(date.getValue().getMonthValue()<10)
            dateString=dateString+"0";
        dateString=dateString+date.getValue().getMonthValue()+"-";
        //if we need to add a "0"
        if(date.getValue().getDayOfMonth()<10)
            dateString=dateString+"0";
        dateString=dateString+date.getValue().getDayOfMonth();

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
                dateString,list,typeSelector.getValue());
            return expense;
    }
    private void createDebtsFromExpense(Expense expense)
    {
        //we know there is at least one participant.
        if(expense.getParticipants().isEmpty()) //(just in case)
            return; //but we would never arrive here
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
                    //System.out.println(p.getName() +" gives "+othersNeedsToGive+" to "
                     //       +expense.getAuthor().getName());
                    //we need the date because in case there is already a debt between these 2 persons we need to
                    //update and maybe to convert money
                    server.addDebtToEvent(server.getCurrentId(),new Debt(othersNeedsToGive,
                            expense.getCurrency(),p.getParticipantID(),expense.getAuthor().
                            getParticipantID()),expense.getDate());
                }
            }
        }
        else
        {
            //the author need to receive all the money
            authorNeedsToReceive=expense.getMoney();
            for(Participant p:expense.getParticipants())
            {
                //update debs from p to author

                //we need the date because in case there is already a debt between these 2 persons we need to
                //update and maybe to convert money
                server.addDebtToEvent(server.getCurrentId(),new Debt(othersNeedsToGive,
                        expense.getCurrency(),p.getParticipantID(),expense.getAuthor().
                        getParticipantID()),expense.getDate());
            }

        }
    }

    @FXML
    void saveEditExpense(ActionEvent event)
    {
        if(!inputIsCorrect())
            return;
        //reload again the expense to be sure that it is the last version of the expense
        //possible future bug solved
        expenseToBeModified=server.getExpenseToBeModified();
        if(expenseToBeModified==null)
        {
            //this can happen if somebody else deleted this expense while you were editing it. In this case
            //let's send a message to the user to inform him and to abort editing.
            VBox layout = new VBox(10);
            Label label = new Label("Somebody deleted this expense while you were editing it. \n" +
                    "Return to the event page:(");
            Button okButton = new Button("Ok");

            // Set up the stage
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Warning");


            okButton.setOnAction(e -> {
                popupStage.close();
                //you still need to do this because the server utils object is only on your app
                server.setExpenseToBeModified(-1);
                EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
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
        server.setExpenseToBeModified(-1);
        Expense expense=takeExpenseFromFields();
        //reset the debts
        server.resetDebtsFromExpense(server.getCurrentId(),expenseToBeModified.getExpenseId());
        //creates new debts
        createDebtsFromExpense(expense);
        //modify the expense and save it to tha database
        server.updateExpense(expenseToBeModified.getExpenseId(),expense);
        expenseToBeModified=null;
        server.setExpenseToBeModified(-1);

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
            warningText.setText(resourceBundle.getString("authorWarning"));
            return false;
        }
        if(contentBox.getText()==null || contentBox.getText().isEmpty())
        {
            warningText.setText(resourceBundle.getString("forWhatWarning"));
            return false;
        }
        if(moneyPaid.getText()==null || moneyPaid.getText().isEmpty())
        {
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
            warningText.setText(resourceBundle.getString("dateWarning"));
            return false;
        }
        if(typeSelector.getValue()==null || typeSelector.getValue().isEmpty())
        {
            warningText.setText(resourceBundle.getString("typeWarning"));
            return false;
        }
        if(!checkBoxAllPeople.isSelected() && !checkBoxSomePeople.isSelected())
        {
            warningText.setText(resourceBundle.getString("splitWarning"));
            return false;
        }
        if(checkBoxSomePeople.isSelected() && selectedNamesList.isEmpty())
        {
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
    void cancelAddExpense(ActionEvent e) {
        resetElements();
        server.setExpenseToBeModified(-1);
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
        server.sendTag("/app/expenses", new Tag(new TagId(tagName,server.getCurrentId()),"#"+color.substring(2,8)));
    //        server.addTag();
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
        //the position->good to get the author object
        //System.out.println(authorSelector.getSelectionModel().getSelectedIndex());
    }


    /**
     * This is a basic handler that checks when you check the box for
     * selecting all people
     * @param event an event
     */
    @FXML
    void handleCheckBoxAllPeople(ActionEvent event) {
        if(checkBoxAllPeople.isSelected()) {
            //in case the user already selected the "some people" option
            checkBoxSomePeople.setSelected(false);
            //hide the list because we don't need to select
            namesList.setVisible(false);
        }
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
            //in case the user already selected the "everyone" option
            checkBoxAllPeople.setSelected(false);
        }
        else {
            namesList.setVisible(false);
        }
    }
    /**
     * This handles when you check a checkBox of a person in the view list for selecting
     * @param checkBox the checkbox of a person who is selected/unselected
     */
    void handleCheckBoxSelectName(CheckBox checkBox,Integer index){
        String name=checkBox.getText();
        //System.out.println(index+", "+name);
        if(checkBox.isSelected()){
            //if this is not already there (this is just to make sure everything is ok!
            //double safe measurement!
            if(!selectedNamesList.contains(index))
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
