package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
public class AddExpenseCtrl {
    @FXML
    private DatePicker date;

    @FXML
    private TextField moneyPaid;

    @FXML
    private ComboBox<?> moneyTypeSelector;

    @FXML
    private ComboBox<?> typeSelector;
}
