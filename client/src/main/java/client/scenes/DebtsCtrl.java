package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;

import commons.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private TableColumn<Debt, String> settleCol;

    @FXML
    private Button cancelButton;

    //Imports used to swap scenes
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    private Stage stage;
    private ServerUtils server;
    private String test;
    private static DebtManager currentDebtManager;

    @Inject
    public DebtsCtrl(ServerUtils server) {
        this.server = server;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DebtsCtrl initializing");

        refresh();


        // initialize close button
        cancelButton.setOnAction(this::cancelHandler);

        dummyTest();

        System.out.println("DebtsCtrl finished initializing");
    }

    private void dummyTest(){

        // set cell factories for columns, receive: (debt)
//        debtorCol.setCellValueFactory(d -> new SimpleStringProperty(server.getParticipant( d.getValue().getDebtor() ).getName()));
//        creditorCol.setCellValueFactory(d -> new SimpleStringProperty(server.getParticipant( d.getValue().getCreditor() ).getName()));
//        amountCol.setCellValueFactory(d -> new SimpleStringProperty(Double.toString( d.getValue().getAmount() )));
//        settleCol.setCellValueFactory(d -> new SimpleStringProperty(Double.toString( d.getValue().getAmount() )));

        debtorCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(Long.toString(d.getValue().getDebtor())));
        creditorCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(Long.toString(d.getValue().getCreditor())));
        amountCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(Double.toString( d.getValue().getAmount() )));
        settleCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(Double.toString( d.getValue().getAmount() )));

        // add participants
        Participant anna = new Participant("Anna", "e", "1", "2");
        Participant elsa = new Participant("Elsa", "e", "1", "2");
        Participant olaf = new Participant("olaf", "e", "1", "2");

        anna.setParticipantID(1);
        elsa.setParticipantID(2);
        elsa.setParticipantID(3);

        // server.addParticipant is not working
//        server.addParticipant(anna);
//        server.addParticipant(elsa);
//        server.addParticipant(olaf);

        // add debts
        Debt d1 = new Debt(10.00, "EUR", anna, elsa);
        Debt d2 = new Debt(69.00, "EUR", anna, olaf);
        Debt d3 = new Debt(5.00, "EUR", olaf, elsa);

        //put into observable array
        ObservableList<Debt> list = FXCollections.observableArrayList(d1,d2,d3);
        debtTable.setItems(list);
    }

    public void connectDebtManager(DebtManager dm){
        currentDebtManager = dm;
        System.out.println("Connecting to " + currentDebtManager);
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
