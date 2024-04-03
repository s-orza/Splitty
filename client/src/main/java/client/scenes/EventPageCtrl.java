package client.scenes;

import client.utils.ServerUtils;
import commons.*;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import javax.inject.Inject;
import java.util.*;
import static client.scenes.MainPageCtrl.currentLocale;

public class EventPageCtrl implements Controller{
    ServerUtils server;

    private SequentialTransition seqTransition;
    @Inject
    public EventPageCtrl(ServerUtils server) {
        this.server = server;
    }

    @FXML
    Button invite;
    @FXML
    TableView<Participant> participantsTable;

    @FXML
    TableColumn<Participant, String> participantsColumn;

    @FXML
    TableView<Expense> expensesTable;

    @FXML
    TableColumn<Expense, String> authorColumn;

    @FXML
    TableColumn<Expense, String> descriptionColumn;

    @FXML
    TableColumn<Expense, Double> amountColumn;

    @FXML
    TableColumn<Expense, String> currencyColumn;

    @FXML
    TableColumn<Expense, String> dateColumn;

    @FXML
    TableColumn<Expense, List<Participant>> participantsColumn2;

    @FXML
    TableColumn<Expense, String> typeColumn;
    @FXML
    private ComboBox<String> searchByComboBox;
    @FXML
    private ToggleButton fromxButton;
    private String fromxButtonText;

    @FXML
    private ToggleButton includingxButton;
    private String includingxButtonText;
    private String allText;
    @FXML
    Button addParticipant;

    @FXML
    Button removeExpense;

    @FXML
    Button addExpense;

    @FXML
    Button editEventName;
    @FXML
    private Button editExpense;
    @FXML
    Button editParticipant;

    @FXML
    Button viewDebts;

    @FXML
    Button viewStatistics;

    @FXML
    Label eventCode;

    @FXML
    Label eventName;
    @FXML
    Button removeParticipant;
    @FXML
    Button flagButton;

    @FXML
    Button cancelButton;

    @FXML
    ComboBox comboBox;
    @FXML
    AnchorPane backGround;
    @FXML
    Button undoButton;
    //here we map every index from the selection comboBox to the id of its participant
    //we need this for searching by author X /including X
    private Map<Integer,Long> indexesToIds;

    private ResourceBundle resourceBundle;

    private Stage stage;

    /**
     * This property is just here to simulate data from database
     */
    private ObservableList<Expense> expenseData;
    /**
     * again this will be removed and will stay just for having something in the tables
     */
    private ObservableList<Participant> participantsData;
                    /*
                    new ParticipantTest("Ivan"),
                    new ParticipantTest("David"),
                    new ParticipantTest("Serban"),
                    new ParticipantTest("Shahar"),
                    new ParticipantTest("Olav"),
                    new ParticipantTest("Oliwer")
                     */
    private ObservableList<String> participantsToSelectFrom;
    private int selectionMod;//0->all, 1->by author,2->including
    private long personId;
    /**
     * just the initialize method
     */
    @FXML
    public void initialize() {
        initializePage();
    }



    //set event page title and event code
    private void initializePage() {
        backgroundImage();
        keyShortCuts();
        selectionMod=0;
        personId=0;
        //load from database:
        expenseData = FXCollections.observableArrayList(server.getAllExpensesOfEvent(server.getCurrentId()));
        //if you deselect an option for searching, it will still be considering that you use that seerch option
        //until you change to another one.
        server.registerForUpdatesExpenses(server.getCurrentId(), e -> {
            System.out.println(selectionMod);
            //This is for live updating the expense table
            switch (selectionMod)
            {
                //if we want to see all expenses
                case 0:
                    ObservableList<Expense> list=
                            FXCollections.observableArrayList(server.getAllExpensesOfEvent(server.getCurrentId()));
                    renderExpenseColumns(list);
                break;
                case 1:
                    //in this case we are 100% the search by author is selected
                    //if the person is an author
                    if(e.getAuthor().getParticipantID()==personId)
                    {
                        Expense convertedEx=new Expense(e.getAuthor(),e.getContent(),
                                server.convertCurrency(e.getDate(),e.getCurrency(),MainCtrl.getCurrency(),e.getMoney()),
                                MainCtrl.getCurrency(),e.getDate(),e.getParticipants(),e.getType());
                        //DON'T FORGET THE ID
                        convertedEx.setExpenseId(e.getExpenseId());
                        //to show only the first 2 decimals
                        convertedEx.setMoney(Double.parseDouble(String.format("%.2f", convertedEx.getMoney())));
                        expensesTable.getItems().add(convertedEx);
                    }
                    break;
                case 2:
                    //in this case we are 100% the search by included person is selected
                    //if the person is included
                    List<Long> pList=e.getParticipants().stream().map(x->x.getParticipantID()).toList();
                    if(pList.contains(personId) || e.getAuthor().getParticipantID()==personId)
                    {
                        Expense convertedEx=new Expense(e.getAuthor(),e.getContent(),
                                server.convertCurrency(e.getDate(),e.getCurrency(),MainCtrl.getCurrency(),e.getMoney()),
                                MainCtrl.getCurrency(),e.getDate(),e.getParticipants(),e.getType());
                        //DON'T FORGET THE ID
                        convertedEx.setExpenseId(e.getExpenseId());
                        //to show only the first 2 decimals
                        convertedEx.setMoney(Double.parseDouble(String.format("%.2f", convertedEx.getMoney())));
                        expensesTable.getItems().add(convertedEx);
                    }
                    break;
                default:
                    break;
            }
        });

        String destination = "/topic/expenses/" + String.valueOf(server.getCurrentId());
        server.registerForMessages(destination, Expense.class, e -> {
            //remove from table live
            switch (selectionMod)
            {
                //if we want to see all expenses
                case 0:
                    //reload them
                    ObservableList<Expense> list=
                            FXCollections.observableArrayList(server.getAllExpensesOfEvent(server.getCurrentId()));
                    renderExpenseColumns(list);
                    break;
                case 1:
                    //in this case we are 100% the search by author is selected
                    //if the person is an author
                    if(e.getAuthor().getParticipantID()==personId)
                    {
                        Expense convertedEx=new Expense(e.getAuthor(),e.getContent(),
                                server.convertCurrency(e.getDate(),e.getCurrency(),MainCtrl.getCurrency(),e.getMoney()),
                                MainCtrl.getCurrency(),e.getDate(),e.getParticipants(),e.getType());
                        //DON'T FORGET THE ID
                        convertedEx.setExpenseId(e.getExpenseId());
                        //to show only the first 2 decimals
                        convertedEx.setMoney(Double.parseDouble(String.format("%.2f", convertedEx.getMoney())));
                        expensesTable.getItems().remove(convertedEx);
                    }
                    break;
                case 2:
                    //in this case we are 100% the search by included person is selected
                    //if the person is included
                    List<Long> pList=e.getParticipants().stream().map(x->x.getParticipantID()).toList();
                    if(pList.contains(personId) || e.getAuthor().getParticipantID()==personId)
                    {
                        Expense convertedEx=new Expense(e.getAuthor(),e.getContent(),
                                server.convertCurrency(e.getDate(),e.getCurrency(),MainCtrl.getCurrency(),e.getMoney()),
                                MainCtrl.getCurrency(),e.getDate(),e.getParticipants(),e.getType());
                        //DON'T FORGET THE ID
                        convertedEx.setExpenseId(e.getExpenseId());
                        //to show only the first 2 decimals
                        convertedEx.setMoney(Double.parseDouble(String.format("%.2f", convertedEx.getMoney())));
                        expensesTable.getItems().remove(convertedEx);
                    }
                    break;
                default:
                    break;
            }
        });

        //we need this to get the id of the selected person
        indexesToIds=new HashMap<>();
        List<Participant> participantList=server.getParticipantsOfEvent(server.getCurrentId());
        participantsData = FXCollections.observableArrayList();
        participantsToSelectFrom=FXCollections.observableArrayList();
        int k=0;
        for(Participant p:participantList)
        {
            participantsData.add(p);
            participantsToSelectFrom.add(p.getName());
            //map the position in the selection combo box to ids
            indexesToIds.put(k,p.getParticipantID());
            k++;
        }

        server.registerForMessages("/topic/participant/event/"+
                String.valueOf(server.getCurrentId()), Participant.class, t -> {
            participantsData.add(t);
        });


        searchByComboBox.setItems(participantsToSelectFrom);
        fromxButton.setText("From ?");
        includingxButton.setText("Including ?");

        renderExpenseColumns(expenseData);
        renderParticipants(participantsData);

        server.registerForMessages("/topic/events/name/" + String.valueOf(server.getCurrentId()), String.class, t -> {
            eventName.setText(t);
        });

        if(currentLocale.getLanguage().equals("en")){
            putFlag("enFlag.png");
            ObservableList<String> comboBoxItems =
                    FXCollections.observableArrayList("English", "Dutch", "German", "Spanish", "Extra");
            comboBox.setItems(comboBoxItems);
            comboBox.setPromptText("English");
        }
        if(currentLocale.getLanguage().equals("nl")){
            putFlag("nlFlag.png");
            ObservableList<String> comboBoxItems =
                    FXCollections.observableArrayList("English", "Dutch", "German", "Spanish", "Extra");
            comboBox.setItems(comboBoxItems);
            comboBox.setPromptText("Dutch");
        }
        if(currentLocale.getLanguage().equals("de")){
            putFlag("deFlag.png");
            ObservableList<String> comboBoxItems =
                    FXCollections.observableArrayList("English", "Dutch", "German", "Spanish", "Extra");
            comboBox.setItems(comboBoxItems);
            comboBox.setPromptText("German");
        }
        if(currentLocale.getLanguage().equals("es")){
            putFlag("esFlag.png");
            ObservableList<String> comboBoxItems =
                    FXCollections.observableArrayList("English", "Dutch", "German", "Spanish", "Extra");
            comboBox.setItems(comboBoxItems);
            comboBox.setPromptText("Spanish");
        }
        if(currentLocale.getLanguage().equals("xx")){
            putFlag("xxFlag.png");
            ObservableList<String> comboBoxItems =
                    FXCollections.observableArrayList("English", "Dutch", "German", "Spanish", "Extra");
            comboBox.setItems(comboBoxItems);
            comboBox.setPromptText("Extra");
        }
        toggleLanguage();
        prepareAnimation();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("English")) changeFlag("en");
            if(newValue.equals("Dutch")) changeFlag("nl");
            if(newValue.equals("Spanish")) changeFlag("es");
            if(newValue.equals("German")) changeFlag("de");
            if(newValue.equals("Extra")) changeFlag("xx");
            toggleLanguage();
        });

        flagButton.setOnMouseClicked(event -> {
//      changeFlag();
//      toggleLanguage();
            comboBox.show();
        });

        eventName.setText(server.getEvent(server.getCurrentId()).getName());
        eventCode.setText("Event Code: " + server.getEvent(server.getCurrentId()).getEventId());

        // just initializes some properties needed for the elements
        addParticipant.setOnAction(e->addParticipantHandler(e));
        addExpense.setOnAction(e->addExpenseHandler(e));
        removeExpense.setOnAction(e->removeExpenseHandler());
        removeParticipant.setOnAction(e->removeParticipantHandler(e));
        editExpense.setOnAction(e->editExpenseHandler(e));
        editParticipant.setOnAction(e -> editParticipantHandler(e));
        expensesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        editEventName.setOnAction(e->{
            editEventNameHandler();
        });
        viewDebts.setOnAction(e->viewDebtsHandler(e));
        viewStatistics.setOnAction(e->viewStatisticsHandler(e));
    }

    private void keyShortCuts() {
        cancelButton.requestFocus();

        editEventName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) comboBox.requestFocus();
            if (event.getCode() == KeyCode.ENTER) editEventName.fire();
        });
        fromxButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) cancelButton.requestFocus();
            if (event.getCode() == KeyCode.LEFT)  cancelButton.requestFocus();
        });
        includingxButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) cancelButton.requestFocus();
            if (event.getCode() == KeyCode.LEFT)  cancelButton.requestFocus();
        });

        comboBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) cancelButton.requestFocus();
            if (event.getCode() == KeyCode.ENTER) comboBox.show();
            if (event.getCode() == KeyCode.LEFT) editEventName.requestFocus();
        });
        cancelButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) viewStatistics.requestFocus();
            if (event.getCode() == KeyCode.ENTER) cancelButton.fire();
            if (event.getCode() == KeyCode.LEFT) comboBox.requestFocus();
        });
        viewStatistics.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) viewDebts.requestFocus();
            if (event.getCode() == KeyCode.ENTER) viewStatistics.fire();
            if (event.getCode() == KeyCode.LEFT) cancelButton.requestFocus();
        });
        viewDebts.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) addParticipant.requestFocus();
            if (event.getCode() == KeyCode.ENTER) viewDebts.fire();
            if (event.getCode() == KeyCode.LEFT) viewStatistics.requestFocus();
        });
        addParticipant.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) addExpense.requestFocus();
            if (event.getCode() == KeyCode.ENTER) addParticipant.fire();
            if (event.getCode() == KeyCode.LEFT) viewDebts.requestFocus();
        });
        addExpense.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) editExpense.requestFocus();
            if (event.getCode() == KeyCode.ENTER) addExpense.fire();
            if (event.getCode() == KeyCode.LEFT) addParticipant.requestFocus();
        });
        editExpense.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) removeExpense.requestFocus();
            if (event.getCode() == KeyCode.ENTER) editExpense.fire();
            if (event.getCode() == KeyCode.LEFT) addExpense.requestFocus();
        });
        removeExpense.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) removeExpense.fire();
            if (event.getCode() == KeyCode.LEFT) editExpense.requestFocus();
        });
    }

    ////////////////////////////////////////
    public void changeFlag(String toChange){
        seqTransition.play();
        if(toChange.equals("es")){
            currentLocale = new Locale("es", "ES");
            // pause for a bit so that the flag shrinks and then changes it
            PauseTransition pause = new PauseTransition(Duration.millis(150));
            // This executes changeFlag after the pause
            pause.setOnFinished(e -> putFlag("esFlag.png"));
            pause.play();
        }
        else if(toChange.equals("nl")){
            currentLocale = new Locale("nl", "NL");
            // pause for a bit so that the flag shrinks and then changes it
            PauseTransition pause = new PauseTransition(Duration.millis(150));
            // This executes changeFlag after the pause
            pause.setOnFinished(e -> putFlag("nlFlag.png"));
            pause.play();
        }
        else if(toChange.equals("de")){
            currentLocale = new Locale("de", "DE");
            // pause for a bit so that the flag shrinks and then changes it
            PauseTransition pause = new PauseTransition(Duration.millis(150));
            // This executes changeFlag after the pause
            pause.setOnFinished(e -> putFlag("deFlag.png"));
            pause.play();
        }
        else if(toChange.equals("xx")){
            currentLocale = new Locale("xx", "XX");
            // pause for a bit so that the flag shrinks and then changes it
            PauseTransition pause = new PauseTransition(Duration.millis(150));
            // This executes changeFlag after the pause
            pause.setOnFinished(e -> putFlag("xxFlag.png"));
            pause.play();
        }
        else{
            currentLocale = new Locale("en", "US");
            PauseTransition pause = new PauseTransition(Duration.millis(150));
            pause.setOnFinished(e -> putFlag("enFlag.png"));
            pause.play();
        }
    }
    public void toggleLanguage(){
        resourceBundle = ResourceBundle.getBundle("messages", currentLocale);

       try{
           eventCode.setText(resourceBundle.getString("eventCodeText"));
           addExpense.setText(resourceBundle.getString("addExpenseText"));
           removeExpense.setText(resourceBundle.getString("removeExpenseText"));
           authorColumn.setText(resourceBundle.getString("authorText"));
           descriptionColumn.setText(resourceBundle.getString("descriptionText"));
           amountColumn.setText(resourceBundle.getString("amountText"));
           currencyColumn.setText(resourceBundle.getString("currencyText"));
           dateColumn.setText(resourceBundle.getString("dateText"));
           participantsColumn2.setText(resourceBundle.getString("participantsText"));
           typeColumn.setText(resourceBundle.getString("typeText"));
           addParticipant.setText(resourceBundle.getString("addParticipantText"));
//           editEventName.setText(resourceBundle.getString("editEventNameText"));
           participantsTable.getColumns().get(0).setText(resourceBundle.getString("participantsText"));

           viewDebts.setText(resourceBundle.getString("viewDebtsText"));
           cancelButton.setText(resourceBundle.getString("cancelText"));
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void putFlag(String path){
        Image image = new Image(path);
        BackgroundSize backgroundSize =
                new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                backgroundSize);

        Background background = new Background(backgroundImage);

        flagButton.setBackground(background);
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

    public void prepareAnimation(){
        // Shrink transition
        ScaleTransition shrink = new ScaleTransition(Duration.millis(150), flagButton);
        shrink.setToY(0.0); // Shrink to disappear on the Y axis
        shrink.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition restore = new ScaleTransition(Duration.millis(150), flagButton);
        restore.setToY(1); // Restore to original size on the Y axis
        restore.setInterpolator(Interpolator.EASE_BOTH);

        seqTransition = new SequentialTransition(shrink, restore);

        flagButton.setOnMouseClicked(event -> seqTransition.play());
    }

    public long findEventId(String name) throws Exception {
        for (Event e : server.getEvents()){
            if(e.getName().equals(name)){
                return e.getEventId();
            }
        }
        throw new Exception("No event with given name exists!");
    }
    /**
     * handles the change of the event name, but only in visual perspective, and no
     * database connectivity
     */
    private void editEventNameHandler() {
        VBox layout = new VBox(10);
        Label label = new Label("What should be the new name of this event?");
        TextField newName = new TextField();

        Button changeButton = new Button("Change");
        Button cancelButton = new Button("Cancel");

        // Set up the stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Change Event Name");


        changeButton.setOnAction(e -> {
            popupStage.close();
            eventName.setText(newName.getText());
//            server.changeEventName(server.getCurrentId(), newName.getText());
            server.sendEventName("/app/events/name/" + String.valueOf(server.getCurrentId()), newName.getText());
        });

        cancelButton.setOnAction(e -> {
            popupStage.close();
        });

        // Set up the layout
        layout.getChildren().addAll(label, newName, cancelButton, changeButton);
        layout.setAlignment(Pos.CENTER);

        // Set the scene and show the stage
        Scene scene = new Scene(layout, 370, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    private void editExpenseHandler(ActionEvent e)
    {
        //here we do not care if the items are converted or not because we only use the ids.
        ObservableList<Expense> selectedItems = expensesTable.getSelectionModel().getSelectedItems();
        if(selectedItems.isEmpty())
        {
            mainCtrl.popup("Please select at least one expense.", "Warning", "Ok");
            //WARNING
            return;
        }
        List<Expense> itemsToEdit = new ArrayList<>(selectedItems);
        if(itemsToEdit.size()>1)
        {
            mainCtrl.popup("Please select only one expense.", "Warning", "Ok");
            //WARNING
            return;
        }
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        AddExpenseCtrl addExpenseCtrl = new AddExpenseCtrl(server);
        server.setExpenseToBeModified(itemsToEdit.get(0).getExpenseId());
        mainCtrl.initialize(stage, addExpenseCtrl.getPair(), "View expense");
    }

    private void editParticipantHandler(ActionEvent e){
        ObservableList<Participant> selectedItems = participantsTable.getSelectionModel().getSelectedItems();
        if(selectedItems.isEmpty())
        {
            mainCtrl.popup("Please select at least one participant.", "Warning", "Ok");
            //WARNING
            return;
        }
        List<Participant> itemsToEdit = new ArrayList<>(selectedItems);
        if(itemsToEdit.size()>1)
        {
            mainCtrl.popup("Please select only one participant.", "Warning", "Ok");
            //WARNING
            return;
        }
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        AddParticipantCtrl addParticipantCtrl = new AddParticipantCtrl(server);
        server.setParticipantToBeModified(itemsToEdit.get(0).getParticipantID());
        mainCtrl.initialize(stage, addParticipantCtrl.getPair(), "View Participant");
    }

    private void addExpenseHandler(ActionEvent e) {
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        AddExpenseCtrl addExpenseCtrl = new AddExpenseCtrl(server);
        mainCtrl.initialize(stage, addExpenseCtrl.getPair(), addExpenseCtrl.getTitle());

    }

    /**
     * this method adds the data about Participants into the Participants table
     * Currently uses mock data from a dummy class, but in the future will get its model from
     * a method that interacts with a database
     * @param participantsData ObservableList which includes the new data to be added in the table
     */
    private void renderParticipants(ObservableList<Participant> participantsData) {
        try{
            participantsColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            participantsTable.setItems(participantsData);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * initializes the columns of the expense table from the database and convert them
     * @param model this is the observable list that should be created with
     *              the data from the database
     */
    private void renderExpenseColumns(ObservableList<Expense> model){
        try{
            ObservableList<Expense> newModel=FXCollections.observableArrayList();
            //to convert money
            List<Expense> expenseList=model.stream().map(x->{
                Expense ex=new Expense(x.getAuthor(),x.getContent(),
                        server.convertCurrency(x.getDate(),x.getCurrency(),MainCtrl.getCurrency(),x.getMoney()),
                        MainCtrl.getCurrency(),x.getDate(),x.getParticipants(),x.getType());
                //DON'T FORGET THE ID
                ex.setExpenseId(x.getExpenseId());
                //to show only the first 2 decimals
                ex.setMoney(Double.parseDouble(String.format("%.2f", ex.getMoney())));
                return ex;
            }).toList();
            newModel.addAll(expenseList);
            //add background color to the tags
            typeColumn.setCellFactory(param ->{
                return new TableCell<Expense,String>(){
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty)
                        {
                            setText(null);
                            setStyle("");
                        }
                        else
                        {
                            setText(item);
                            Tag tag=server.getTagByIdOfEvent(item,server.getCurrentId());
                            if(tag==null)
                                setStyle("");
                            else
                            {
                                //set background color
                                String textForBackgroundColor="-fx-background-color: "+tag.getColor()+";";
                                //set the text white or black (It depends on the contrast with the background)
                                Color c=Color.web(tag.getColor());
                                //calculate the luminance (I searched on the internet and this is the formula)
                                //luminance= 0.2126*Red + 0.7152*Green + 0.0722*Blue
                                double luminance=0.2126*c.getRed() + 0.7152*c.getGreen() + 0.0722*c.getBlue();
                                //System.out.println("luminance is "+luminance);
                                //set the text color to white or black, depending on which one has the greatest contrast
                                if(luminance>0.5)
                                    setStyle(textForBackgroundColor+"-fx-text-fill: black;");
                                else
                                    setStyle(textForBackgroundColor+"-fx-text-fill: white;");
                            }
                        }
                    }
                };
            });
            authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("money"));
            currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            participantsColumn2.setCellValueFactory(new PropertyValueFactory<>("participants"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("Type"));

            expensesTable.setItems(newModel);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    void personWasSelected()
    {
        long id=indexesToIds.get(searchByComboBox.getSelectionModel().getSelectedIndex());
        Participant x;
        x=server.getParticipant(id);
        if(x==null)
            return;
        fromxButton.setText("From "+x.getName());
        includingxButton.setText("Including "+x.getName());
        if(fromxButton.isSelected())
            searchFromX(new ActionEvent());
        else
            if(includingxButton.isSelected())
                searchIncludingX(new ActionEvent());
            else
                //this is useful if we deselect all options.
                searchAll(new ActionEvent());
    }
    @FXML
    void searchAll(ActionEvent event) {
        //show all expenses
        selectionMod=0;
        personId=0;
        expenseData = FXCollections.observableArrayList(server.getAllExpensesOfEvent(server.getCurrentId()));
        renderExpenseColumns(expenseData);
    }

    @FXML
    void searchFromX(ActionEvent event) {
        if(searchByComboBox.getValue()==null)
        {
            mainCtrl.popup("You must select a person", "Warning", "Ok");
            //popUpWarningText("Please select the person!");
            return;
        }
        long id=indexesToIds.get(searchByComboBox.getSelectionModel().getSelectedIndex());
        Participant x;
        x=server.getParticipant(id);
        if(x==null)//there has been a problem
        {
            return;
        }
        //show all expenses from x in this event
        List<Expense> listFromServer=server.getAllExpensesFromXOfEvent(server.getCurrentId(),x.getParticipantID());
        if(listFromServer==null)
            return;
        ObservableList<Expense> expensesFromX=FXCollections.observableArrayList();

        expensesFromX.addAll(listFromServer);
        selectionMod=1;
        personId=id;
        renderExpenseColumns(expensesFromX);

    }

    @FXML
    void searchIncludingX(ActionEvent event) {
        if(searchByComboBox.getValue()==null)
        {
            mainCtrl.popup("You must select the included person!", "Warning", "Ok");
            return;
        }
        long id=indexesToIds.get(searchByComboBox.getSelectionModel().getSelectedIndex());
        Participant x;
        x=server.getParticipant(id);
        if(x==null)//there has been a problem
        {
            return;
        }
        //show all expenses that includes x in this event
        List<Expense> listFromServer=server.getAllExpensesIncludingXOfEvent(server.getCurrentId(),x.getParticipantID());
        if(listFromServer==null)
            return;
        ObservableList<Expense> expensesFromX=FXCollections.observableArrayList();
        selectionMod=2;
        personId=id;
        expensesFromX.addAll(listFromServer);
        renderExpenseColumns(expensesFromX);

    }

    /**
     * This method handles the removal of participants in the database
     */
    public void removeParticipantHandler(ActionEvent event){
        VBox layout = new VBox(10);
        Label label = new Label(resourceBundle.getString("removeParticipantQuestionText"));
        Button cancelButton = new Button(resourceBundle.getString("cancelText"));

        Button removeButton = new Button(resourceBundle.getString("removeText"));

        // setting up the stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(resourceBundle.getString("removeParticipantTitle"));

        // set action for removing
        removeButton.setOnAction(e -> {
            popupStage.close();

            ObservableList<Participant> selectedItems = participantsTable.getSelectionModel().getSelectedItems();
            List<Participant> itemsToRemove = new ArrayList<>(selectedItems);
            participantsData.removeAll(itemsToRemove);

            removeParticipantsFromDatabase(itemsToRemove);
        });

        // set action for cancelling the removal process
        cancelButton.setOnAction(e -> {
            popupStage.close();
        });

        // Set up the layout
        layout.getChildren().addAll(label, cancelButton, removeButton);
        layout.setAlignment(Pos.CENTER);

        // Set the scene and show the stage
        Scene scene = new Scene(layout, 370, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    /**
     * This method will remove the provided list of participant from the database
     * @param toRemove List of participants to remove
     */
    private void removeParticipantsFromDatabase(List<Participant> toRemove){
        for (Participant p: toRemove){
            server.deleteParticipantEvent(p.getParticipantID(), server.getCurrentId());
            server.deleteParticipant(p.getParticipantID());
        }
    }


    /**
     * this method handles the functionality of removing visual entries in the table
     */

    private void removeExpenseHandler(){
        VBox layout = new VBox(10);
        Label label = new Label(resourceBundle.getString("removeExpenseQuestionText"));
        Button cancelButton = new Button(resourceBundle.getString("cancelText"));

        Button removeButton = new Button(resourceBundle.getString("removeText"));

        // Set up the stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(resourceBundle.getString("removeExpenseTitle"));

        // This removes the entries from the file if pressed
        removeButton.setOnAction(e -> {
            popupStage.close();

            ObservableList<Expense> selectedItems = expensesTable.getSelectionModel().getSelectedItems();
            List<Expense> itemsToRemove = new ArrayList<>(selectedItems);
            //items with money uncoverted
            List<Expense> originalItems=new ArrayList<>();
            for(Expense ex:itemsToRemove) {
                if(ex.getExpenseId()==0)
                    System.out.println("ID of expense is null!!!!!!!");
                originalItems.add(server.getExpenseById(ex.getExpenseId()));
            }
            System.out.println(itemsToRemove);
            expenseData.removeAll(itemsToRemove);
            //I did this to be sure I don't create any problems to the websocket
            removeExpensesFromDatabase(originalItems);
        });

        cancelButton.setOnAction(e -> {
            popupStage.close();
        });

        // Set up the layout
        layout.getChildren().addAll(label, cancelButton, removeButton);
        layout.setAlignment(Pos.CENTER);

        // Set the scene and show the stage
        Scene scene = new Scene(layout, 370, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();

        showPopup();
    }

    private void showPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Deleted expense Successfully");
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 350, 20);
        popupStage.setScene(scene);
        popupStage.show();
    }

    /**
     * this method will remove the provided list of expenses from the database
     * @param toRemove List of expenses to remove
     */
    private void removeExpensesFromDatabase(List<Expense> toRemove){
        for (Expense x: toRemove) {
            String destination = "/app/expenses/" + String.valueOf(server.getCurrentId());
            server.sendRemoveExpense(destination,x);
        }
        // this method will remove the expenses from the database
    }
    public void close(ActionEvent e){
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        MainPageCtrl mainPageCtrl = new MainPageCtrl(server);
        mainCtrl.initialize(stage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());
    }


    /**
     * this method will change the name of the event in the databse
     * @param newName String the new name of the event
     */
    private void changeNameInDatabase(String newName){
        //todo
        // this method will change the name of the event in database
    }


    /**
     * method that will lead to a new stage, specifically for adding participants
     */
    public void addParticipantHandler(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        AddParticipantCtrl addParticipantCtrl = new AddParticipantCtrl(server);
        mainCtrl.initialize(stage, addParticipantCtrl.getPair(), addParticipantCtrl.getTitle());
    }


    /**
     * Method to switch to debts page
     */
    public void viewDebtsHandler(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        DebtsCtrl debtsCtrl = new DebtsCtrl(server);
        mainCtrl.initialize(stage, debtsCtrl.getPair(), debtsCtrl.getTitle());
    }
    public void viewStatisticsHandler(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        StatisticsCtrl statisticsCtrl = new StatisticsCtrl(server);
        mainCtrl.initialize(stage, statisticsCtrl.getPair(), statisticsCtrl.getTitle());
    }

    public void inviteParticipant(ActionEvent event){
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        InviteParticipantCtrl inviteParticipantCtrl = new InviteParticipantCtrl(server);
        mainCtrl.initialize(stage, inviteParticipantCtrl.getPair(), inviteParticipantCtrl.getTitle());
    }


    //getter for swapping scenes
    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "EventPage.fxml");
    }
    public String getTitle(){
        return "Event Page";
    }

}



