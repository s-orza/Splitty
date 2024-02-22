package client.scenes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
public class AddExpenseCtrl {
    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private CheckBox checkBoxAllPeople;

    @FXML
    private CheckBox checkBoxSomePeople;

    @FXML
    private DatePicker date;

    @FXML
    private TextField moneyPaid;

    @FXML
    private ComboBox<?> moneyTypeSelector;

    @FXML
    private ComboBox<?> typeSelector;
    @FXML
    private ListView<String> namesList;
    private ObservableList<String> names = FXCollections.observableArrayList(
            "Serban","David","Olav","Alex");

    @FXML
    public void initialize() {
        namesList.setItems(names);
        namesList.setCellFactory(param -> new CheckBoxListCell());
    }
    private class CheckBoxListCell extends ListCell<String>{
        private CheckBox checkBox;
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item,empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                checkBox = new CheckBox(item);//we create a checkbox with that name
                setGraphic(checkBox);
            }
        }
    }

    @FXML
    void addExpenseToTheEvent(MouseEvent event) {
        if(date.getValue()==null)
        {
            return;
        }
        int year=date.getValue().getYear();
        System.out.println(year);
    }

    @FXML
    void cancelAddExpense(MouseEvent event) {

    }

    @FXML
    void handleCheckBoxAllPeople(ActionEvent event) {
        if(checkBoxAllPeople.isSelected())
        {
            System.out.println("Everyone is selected!");
        }
        else
        {
            System.out.println("Only some people are selected!");
            //show the list of people available
        }
    }

    @FXML
    void handleCheckBoxSomePeople(ActionEvent event) {
        if(checkBoxAllPeople.isSelected())
        {
            System.out.println("Everyone is selected!");
        }
        else
        {
            System.out.println("Only some people are selected!");
            //show the list of people available
        }

    }
}
