package client.scenes;


//import com.sun.javafx.application.ParametersImpl;

import client.utils.ServerUtils;


import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;

import commons.ExpenseTest;
import commons.Participant;

import commons.ParticipantTest;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.google.inject.Guice.createInjector;


public class EventPageCtrl implements Controller{


    @FXML
    TableView participantsTable;

    ServerUtils server;

    @Inject
    public EventPageCtrl(ServerUtils server) {
        this.server = server;
    }

    @FXML
    TableColumn<ParticipantTest, String> participantsColumn;

    @FXML
    TableView<ExpenseTest> expensesTable;

    @FXML
    TableColumn<ExpenseTest, String> authorColumn;

    @FXML
    TableColumn<ExpenseTest, String> descriptionColumn;

    @FXML
    TableColumn<ExpenseTest, String> dateColumn;

    @FXML
    TableColumn<ExpenseTest, Double> amountColumn;

    @FXML
    TableColumn<ExpenseTest, Integer> idColumn;

    @FXML
    Button addParticipant;

    @FXML
    Button removeExpense;

    @FXML
    Button addExpense;

    @FXML
    Button editEventName;

    @FXML
    Label eventName;
    //Imports used to swap scenes
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    private Stage stage;

    /**
     * This property is just here to simulate data from database
     */
    private ObservableList<ExpenseTest> expenseData = FXCollections.observableArrayList(
            new ExpenseTest("Ivan", "Drinks", "12-12-2023", 7.9),
            new ExpenseTest("Olav", "More Drinks", "23-10-2023", 45),
            new ExpenseTest("David", "Tickets for Event", "13-12-2023", 764),
            new ExpenseTest("Oliwer", "Bribe for policemen", "31-12-2023", 7.1 ),
            new ExpenseTest("Shahar", "Just a gift", "14-12-2023", 34.98),
            new ExpenseTest("Serban", "More more drinks", "15-12-2023", 200 )
            );
    /**
     * again this will be removed and will stay just for having something in the tables
     */
    private ObservableList<ParticipantTest> participantsData =
            FXCollections.observableArrayList(
                    new ParticipantTest("Ivan"),
                    new ParticipantTest("David"),
                    new ParticipantTest("Serban"),
                    new ParticipantTest("Shahar"),
                    new ParticipantTest("Olav"),
                    new ParticipantTest("Oliwer")
            );

    /**
     * just the initialize method
     */
    @FXML
    public void initialize() {

        //TODO
        // a method that fetches the data for the expenses and participants
        // and saves it into participantsData, expenseData

        System.out.println("in init");
        renderExpenseColumns(expenseData);
        renderParticipants(participantsData);

        // just initializes some properties needed for the elements
        addParticipant.setOnAction(e->addParticipantHandler());
        addExpense.setOnAction(e->addExpenseHandler(e));
        removeExpense.setOnAction(e->removeExpenseHandler());
        expensesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        editEventName.setOnAction(e->{
            editEventNameHandler();
        });

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

            //TODO
            // We need to add database logic to change the name in the database as well.
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
        mainCtrl.initialize(stage, AddExpenseCtrl.getPair(), AddExpenseCtrl.getTitle());
    }





    /**
     * this method adds the data about Participants into the Participants table
     * Currently uses mock data from a dummy class, but in the future will get its model from
     * a method that interacts with a database
     * @param participantsData ObservableList which includes the new data to be added in the table
     */
    private void renderParticipants(ObservableList<ParticipantTest> participantsData) {
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
    private void renderExpenseColumns(ObservableList<ExpenseTest> model){
        try{
            authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Description"));
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("Date"));
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

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
        Label label = new Label("Are you sure you want to remove the selected expenses?");
        Button cancelButton = new Button("Cancel");

        Button removeButton = new Button("Remove");

        // Set up the stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Remove Expenses");

        // This removes the entries from the file if pressed
        removeButton.setOnAction(e -> {
            popupStage.close();

            ObservableList<ExpenseTest> selectedItems = expensesTable.getSelectionModel().getSelectedItems();
            List<ExpenseTest> itemsToRemove = new ArrayList<>(selectedItems);
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
    private void removeExpensesFromDatabase(List<ExpenseTest> toRemove){
        //todo
        // this method will remove the expenses from the database
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
    public void addParticipantHandler() {
        try {
            System.out.println("This will lead to another page to add participant");
            Participant a = server.getParticipant(67152);
            System.out.println(a);

//            System.out.println("about to execute participantEvent");
//            server.addParticipantEvent(new ParticipantEventDTO(67152, 54352));
        } catch (Exception e) {
            System.out.println(e);
        }

        //todo
        // go to the add participant page
    }



    //getter for swapping scenes
    public static Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "EventPage.fxml");
    }
    public static String getTitle(){
        return "Event Page";
    }

}


