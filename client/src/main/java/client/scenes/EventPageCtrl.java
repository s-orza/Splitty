package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


import static client.scenes.MainPageCtrl.currentLocale;
import static com.google.inject.Guice.createInjector;

public class EventPageCtrl implements Controller{
    ServerUtils server;

    private SequentialTransition seqTransition;
    @Inject
    public EventPageCtrl(ServerUtils server) {
        this.server = server;
    }

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
    TableColumn<Expense, List<Participant>> participantColumn2;

    @FXML
    TableColumn<Expense, String> typeColumn;

    @FXML
    Button addParticipant;

    @FXML
    Button removeExpense;

    @FXML
    Button addExpense;

    @FXML
    Button editEventName;

    @FXML
    Button viewDebts;

    @FXML
    Button viewStatistics;

    @FXML
    Label eventCode;

    @FXML
    Label eventName;


    @FXML
    Button flagButton;

    @FXML
    Button cancelButton;

    @FXML
    ComboBox comboBox;

    private ResourceBundle resourceBundle;

    //Imports used to swap scenes
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    private Stage stage;

    /**
     * This property is just here to simulate data from database
     */
    private ObservableList<Expense> expenseData;
            /*
            new ExpenseTest("Ivan", "Drinks", "12-12-2023", 7.9),
            new ExpenseTest("Olav", "More Drinks", "23-10-2023", 45),
            new ExpenseTest("David", "Tickets for Event", "13-12-2023", 764),
            new ExpenseTest("Oliwer", "Bribe for policemen", "31-12-2023", 7.1 ),
            new ExpenseTest("Shahar", "Just a gift", "14-12-2023", 34.98),
            new ExpenseTest("Serban", "More more drinks", "15-12-2023", 200 )

             */
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

    /**
     * just the initialize method
     */
    @FXML
    public void initialize() {

        //TODO
        // a method that fetches the data for the participants
        // and saves it into participantsData


        System.out.println("Event Page initialize method");

        System.out.println("in init");

        //initializes flags and loads from database
        initializePage();
    }


    //set event page title and event code
    private void initializePage() {
        //load from database:
        expenseData = FXCollections.observableArrayList(server.getAllExpensesOfEvent(server.getCurrentId()));
        server.registerForUpdatesExpenses(server.getCurrentId(), e -> {
            expenseData.add(e);
        });


        List<Participant> participantList=server.getParticipantsOfEvent(server.getCurrentId());
        participantsData = FXCollections.observableArrayList();
        for(Participant p:participantList)
            participantsData.add(p);

        renderExpenseColumns(expenseData);
        renderParticipants(participantsData);

        if(currentLocale.getLanguage().equals("en")){
            putFlag("enFlag.png");
            ObservableList<String> comboBoxItems =
                    FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
            comboBox.setItems(comboBoxItems);
            comboBox.setPromptText("English");
        }
        if(currentLocale.getLanguage().equals("nl")){
            putFlag("nlFlag.png");
            ObservableList<String> comboBoxItems =
                    FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
            comboBox.setItems(comboBoxItems);
            comboBox.setPromptText("Dutch");
        }
        if(currentLocale.getLanguage().equals("de")){
            putFlag("deFlag.png");
            ObservableList<String> comboBoxItems =
                    FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
            comboBox.setItems(comboBoxItems);
            comboBox.setPromptText("German");
        }
        if(currentLocale.getLanguage().equals("es")){
            putFlag("esFlag.png");
            ObservableList<String> comboBoxItems =
                    FXCollections.observableArrayList("English", "Dutch", "German", "Spanish");
            comboBox.setItems(comboBoxItems);
            comboBox.setPromptText("Spanish");
        }
        toggleLanguage();
        prepareAnimation();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Selected item: " + newValue);
            if(newValue.equals("English")) changeFlag("en");
            if(newValue.equals("Dutch")) changeFlag("nl");
            if(newValue.equals("Spanish")) changeFlag("es");
            if(newValue.equals("German")) changeFlag("de");
            toggleLanguage();
        });

        flagButton.setOnMouseClicked(event -> {
//      changeFlag();
//      toggleLanguage();
            comboBox.show();
        });

        eventName.setText(server.getEvent(server.getCurrentId()).getName());
        eventCode.setText("Event Code: " + server.getEvent(server.getCurrentId()).getEventId());



        // System.out.println(server.getEvent(server.getCurrentId()));
        // just initializes some properties needed for the elements
        addParticipant.setOnAction(e->addParticipantHandler(e));
        addExpense.setOnAction(e->addExpenseHandler(e));
        removeExpense.setOnAction(e->removeExpenseHandler());
        expensesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        editEventName.setOnAction(e->{
            editEventNameHandler();
        });
        viewDebts.setOnAction(e->viewDebtsHandler(e));
        viewStatistics.setOnAction(e->viewStatisticsHandler(e));
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
           eventName.setText(resourceBundle.getString("eventNameText"));
           System.out.println(1);
           eventCode.setText(resourceBundle.getString("eventCodeText"));
           System.out.println(2);
           addExpense.setText(resourceBundle.getString("addExpenseText"));
           System.out.println(3);
           removeExpense.setText(resourceBundle.getString("removeExpenseText"));
           System.out.println(4);
           authorColumn.setText(resourceBundle.getString("authorText"));
           System.out.println(5);
           descriptionColumn.setText(resourceBundle.getString("descriptionText"));
           System.out.println(6);
           amountColumn.setText(resourceBundle.getString("amountText"));
           System.out.println(7);
           currencyColumn.setText(resourceBundle.getString("currencyText"));
           System.out.println(8);
           dateColumn.setText(resourceBundle.getString("dateText"));
           System.out.println(1);
           participantsColumn.setText(resourceBundle.getString("participantsText"));
           System.out.println(1);
           typeColumn.setText(resourceBundle.getString("typeText"));
           System.out.println(1);
           addParticipant.setText(resourceBundle.getString("addParticipantText"));
           System.out.println(1);
           editEventName.setText(resourceBundle.getString("editEventNameText"));
           System.out.println(1);
           participantsTable.getColumns().get(0).setText(resourceBundle.getString("participantsText"));
           System.out.println(1);
           viewDebts.setText(resourceBundle.getString("viewDebtsText"));
           cancelButton.setText(resourceBundle.getString("cancelText"));
       }catch (Exception e){
           System.out.println(e);
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
    ////////////////////////////////////////

//    public void connectEvent(Event event){
//        currentEvent = event;
//        System.out.println("Connecting to " + currentEvent);
//
//        System.out.println(server.getCurrentId());
//        eventName.setText(server.getEvent(server.getCurrentId()).getName());
//        eventCode.setText("Event Code: " + server.getCurrentId());
//
//    }

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
            server.changeEventName(server.getCurrentId(), newName.getText());
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

    private void addExpenseHandler(ActionEvent e) {
        System.out.println("This will lead to another page to add expense");
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
            System.out.println(e);
        }
    }

    /**
     * initializes the columns of the expense table from the database
     * @param model this is the observable list that should be created with
     *              the data from the database
     */
    private void renderExpenseColumns(ObservableList<Expense> model){
        try{
            authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("money"));
            currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            participantColumn2.setCellValueFactory(new PropertyValueFactory<>("participants"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("Type"));

            expensesTable.setItems(model);
        }catch(Exception e){
            System.out.println(e);
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
            expenseData.removeAll(itemsToRemove);

            removeExpensesFromDatabase(itemsToRemove);
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
    }

    /**
     * this method will remove the provided list of expenses from the database
     * @param toRemove List of expenses to remove
     */
    private void removeExpensesFromDatabase(List<Expense> toRemove){
        for (Expense x: toRemove) {
            server.deleteExpenseFromEvent(server.getCurrentId(), x.getExpenseId());
        }
        // this method will remove the expenses from the database
    }
    public void close(ActionEvent e){
        System.out.println("close window");
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
        try {

            //server.deleteParticipantEvent(52752,92757);
//            server.getParticipantsOfEvent(52752);
//            System.out.println(server.getEventsOfParticipant(92755));

//            var la = server.getEventsOfParticipant(92755);
//            for(Event a : la){
//                System.out.println(a);
//            }
            var la = server.getParticipantsOfEvent(server.getCurrentId());
            for(Participant a : la){
                System.out.println(a);
            }

//            System.out.println("about to execute participantEvent");
//            server.addParticipantEvent(new ParticipantEventDTO(67152, 54352));
        } catch (Exception e) {
            System.out.println(e);
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        AddParticipantCtrl addParticipantCtrl = new AddParticipantCtrl(server);
        mainCtrl.initialize(stage, addParticipantCtrl.getPair(), addParticipantCtrl.getTitle());
    }


    /**
     * Method to switch to debts page
     */
    public void viewDebtsHandler(ActionEvent event) {
        System.out.println("switching to debts");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        DebtsCtrl debtsCtrl = new DebtsCtrl(server);
        mainCtrl.initialize(stage, debtsCtrl.getPair(), debtsCtrl.getTitle());
    }
    public void viewStatisticsHandler(ActionEvent event) {
        System.out.println("switching to Statistics");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        StatisticsCtrl statisticsCtrl = new StatisticsCtrl(server);
        mainCtrl.initialize(stage, statisticsCtrl.getPair(), statisticsCtrl.getTitle());
    }

    public void stop () {
        server.stop();
    }

    //getter for swapping scenes
    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "EventPage.fxml");
    }
    public String getTitle(){
        return "Event Page";
    }

}



