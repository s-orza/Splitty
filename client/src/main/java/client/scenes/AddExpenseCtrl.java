package client.scenes;

import commons.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseCtrl {
    @FXML
    private Button addButton;

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
    private TextField moneyPaid;

    @FXML
    private ComboBox<String> moneyTypeSelector;

    @FXML
    private ComboBox<String> authorSelector;

    @FXML
    private ComboBox<String> typeSelector;
    @FXML
    private ListView<String> namesList;//names showed on screen from which we select
    private List<String> selectedNamesList=new ArrayList<>();
    private ObservableList<String> names = FXCollections.observableArrayList(
            "Serban","David","Olav","Alex");

    @FXML
    public void initialize() {
        selectedNamesList=new ArrayList<>();
        //create the list view with "names"
        namesList.setItems(names);
        namesList.setCellFactory(param -> new CheckBoxListCell());
        //namesList.getChildren().add(checkBox);   //just an idea for another way of putting names in a list view

        //for handling money type
        moneyTypeSelector.setOnAction(this::handleCurrencySelection);
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
        if(date.getValue()==null) {
            System.out.println("You need to select date!");
            return;
        }
        if(moneyPaid.getText().isEmpty())
        {
            System.out.println("Write the amount of money");
            return;
        }
        int year=date.getValue().getYear();
        String author=authorSelector.getValue();
        String content=contentBox.getText();
        int money=Integer.parseInt(moneyPaid.getText());
        //the expense
        Expense expense=new Expense(author,content,money,moneyTypeSelector.getValue(),
                date.getValue(),selectedNamesList,typeSelector.getValue());
        System.out.println(expense);
    }
    /**
     * this function will be called when you press the cancel Button.
     * @param event an event
     */
    @FXML
    void cancelAddExpense(MouseEvent event) {
        System.out.println("Expense canceled");
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
}
