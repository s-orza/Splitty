package client.scenes;

import client.utils.ServerUtils;
import commons.Debt;
import commons.Event;
import commons.Participant;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

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
    private TableColumn<Debt, Void> settleCol;

    @FXML
    private Button cancelButton;

    @FXML
    private AnchorPane backGround;

    private Stage stage;
    private ServerUtils server;
    private String test;
    private static Event currentEvent;

    @Inject
    public DebtsCtrl(ServerUtils server) {
        this.server = server;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DebtsCtrl initializing");
        backgroundImage();
        refresh();

        // initialize close button
        cancelButton.setOnAction(this::cancelHandler);

        // render the columns
        renderCols();

        dummyTest();

        System.out.println("DebtsCtrl finished initializing");
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


    private void renderCols(){
//        debtorCol.setCellValueFactory(
//        d -> new SimpleStringProperty(server.getParticipant( d.getValue().getDebtor() ).getName()));
//
//        creditorCol.setCellValueFactory(
//        d -> new SimpleStringProperty(server.getParticipant( d.getValue().getCreditor() ).getName()));
//
//        amountCol.setCellValueFactory(d -> new SimpleStringProperty(Double.toString( d.getValue().getAmount() )));
//        settleCol.setCellValueFactory(d -> new SimpleStringProperty(Double.toString( d.getValue().getAmount() )));

        // set cell factories for columns, receive: (debt)
        debtorCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(Long.toString(d.getValue().getDebtor())));
        creditorCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(Long.toString(d.getValue().getCreditor())));
        amountCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(Double.toString( d.getValue().getAmount() )));
        renderSettleCol();
    }

    private void renderSettleCol(){

        //make a cellFactory for the buttons in Settle Column
        Callback<TableColumn<Debt, Void>, TableCell<Debt, Void>> cellFactory =
                new Callback<TableColumn<Debt, Void>, TableCell<Debt, Void>>() {
            @Override
            public TableCell<Debt, Void> call(final TableColumn<Debt, Void> param) {
                final TableCell<Debt, Void> cell = new TableCell<Debt, Void>() {

                    private final Button btn = new Button("Settle");

                    {
                        //define function of settle button
                        btn.setOnAction((ActionEvent event) -> {
                            //retrieve selected debt
                            Debt debt = getTableView().getItems().get(getIndex());
                            System.out.println("selectedDebt: " + debt);

                            //settle the debt (currently null since no backend)
//                            currentDebtManager.settleDebt(debt);

                            //delete and refresh
                            debtTable.getItems().remove(debt);
                            refresh();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        settleCol.setCellFactory(cellFactory);

        debtTable.getColumns().add(settleCol);
    }

    private void dummyTest(){

        // add participants
        Participant anna = new Participant("Anna", "e", "1", "2");
        Participant elsa = new Participant("Elsa", "e", "1", "2");
        Participant olaf = new Participant("olaf", "e", "1", "2");

        anna.setParticipantID(1);
        elsa.setParticipantID(2);
        olaf.setParticipantID(3);

        // server.addParticipant is not working
//        server.addParticipant(anna);
//        server.addParticipant(elsa);
//        server.addParticipant(olaf);

        // add debts
        Debt d1 = new Debt(10.00, "EUR", anna, elsa);
        Debt d2 = new Debt(69.00, "EUR", anna, olaf);
        Debt d3 = new Debt(5.00, "EUR", olaf, elsa);

        d1.setDebtID(10);
        d2.setDebtID(20);
        d3.setDebtID(30);

        //put into observable array
        ObservableList<Debt> list = FXCollections.observableArrayList();//d1,d2,d3);
        Event ev=server.getEvent(server.getCurrentId());
        list.addAll(ev.getDebts());
        debtTable.setItems(list);
    }
    public void connectEvent(Event ev){
        currentEvent = ev;
        System.out.println("Connecting to " + currentEvent);
    }

    private void cancelHandler(ActionEvent e){
        System.out.println("closed DebtsCtrl");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
        mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
    }

    public void refresh() {
        System.out.println("refreshed");
//        // refreshes the Debt Manager and resets the items in the table
//        var dm = server.getDebtManager();
//        currentDebtManager = dm;
//        data = dm.getDebts();
//        debtTable.setItems(data);

    }

    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "Debts.fxml");
    }
    public String getTitle(){
        return "Debts Page";
    }
}
