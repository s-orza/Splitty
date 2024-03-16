package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;

import commons.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Pair;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.inject.Inject;

import java.net.URL;
import java.util.ResourceBundle;

import static com.google.inject.Guice.createInjector;

public class DebtsCtrl implements Controller, Initializable {


    @FXML
    private TableView<Debt> debtTable;

    @FXML
    private TableColumn<Debt, String> debtorCol;

    @FXML
    private TableColumn<Debt, String> creditorCol;

    @FXML
    private TableColumn<Debt, String> amountCol;

    @FXML
    private Button cancelButton;

    //Imports used to swap scenes
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    private Stage stage;
    private ServerUtils server;
    static DebtManager currentDebtManager;

    // currently constructor injection doesn't work, anywhere
    // including in EventPageCtrl
    @Inject
    public DebtsCtrl(ServerUtils server, long id) {
        this.server = server;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DebtsCtrl initializing");

        refresh();

        // initialize close button
        cancelButton.setOnAction(this::cancelHandler);

        // set cell factories for columns, receive: (debt)
        debtorCol.setCellValueFactory(d -> new SimpleStringProperty(server.getParticipant( d.getValue().getDebtor() ).getName()));
        creditorCol.setCellValueFactory(d -> new SimpleStringProperty(server.getParticipant( d.getValue().getCreditor() ).getName()));
        creditorCol.setCellValueFactory(d -> new SimpleStringProperty(Double.toString( d.getValue().getAmount() )));

        System.out.println("DebtsCtrl finished initializing");
    }

    private void cancelHandler(ActionEvent e){
        System.out.println("closed DebtsCtrl");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, EventPageCtrl.getPair(), EventPageCtrl.getTitle());
    }

    public void refresh() {
        System.out.println("refreshed");
//        // refreshes the Debt Manager and resets the items in the table
//        var dm = server.getDebtManager();
//        currentDebtManager = dm;
//        data = dm.getDebts();
//        debtTable.setItems(data);

    }

    public static Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "Debts.fxml");
    }
    public static String getTitle(){
        return "Debts Page";
    }
}
