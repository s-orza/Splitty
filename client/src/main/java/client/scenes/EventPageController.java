package client.scenes;


import com.sun.javafx.application.ParametersImpl;
import commons.ExpenseTest;
import commons.ParticipantTest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EventPageController{

    @FXML
    public TableView participantsTable;

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
    /**
     * this was added just to fill the tables with some exemplary values
     * the real connectivity to a database will be done later
     */
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
    private ObservableList<ParticipantTest> partiipantsData =
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
        // Initialize your columns here
        //TODO
        // a method that fetches the data for the expenses from the database
        // and transforms it into an observable list.
        System.out.println("in init");
        renderExpenseColumns(expenseData);
        renderParticipants(partiipantsData);

        // just initiallizes some properties needed for the elements
        addParticipant.setOnAction(e->addParticipantHandler());
        addExpense.setOnAction(e->addExpenseHandler());
        removeExpense.setOnAction(e->removeExpenseHandler());
        expensesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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


    private void removeExpenseHandler(){
        

        ObservableList<ExpenseTest> selectedItems = expensesTable.getSelectionModel().getSelectedItems();

        // Make a copy of the selected items to avoid ConcurrentModificationException
        List<ExpenseTest> itemsToRemove = new ArrayList<>(selectedItems);

        // Assuming ExpenseTestModels is the ObservableList used for the table's items
        expenseData.removeAll(itemsToRemove);
    }


    public void addParticipantHandler(){
        System.out.println("This will lead to another page to add participant");
    }

    public void addExpenseHandler(){
        System.out.println("This will lead to another page to add expense");
    }

    private void renderParticipants(ObservableList<ParticipantTest> model){
        try{
            participantsColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            participantsTable.setItems(model);
        }catch(Exception e){
            System.out.println(e);
        }

    }
}


