package client.scenes;

import client.utils.ServerUtils;
import commons.Debt;
import commons.Event;
import commons.Participant;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class DebtsCtrl implements Controller, Initializable {


    @FXML
    private TableView<Debt> debtTable;

    @FXML
    private TableColumn<Debt, String> debtCol;

    @FXML
    private TableColumn<Debt, Void> settleCol;

    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> searchByComboBox;

    @FXML
    private AnchorPane backGround;

    @FXML
    private Button refreshButton;

    private Stage stage;
    private ServerUtils server;

    private static Event currentEvent;
    private long filterId = -1;
    private ObservableList<Participant> participants;
    private ObservableList<String> participantNames;
    private Map<Integer,Long> indexesToIds;

    @Inject
    public DebtsCtrl(ServerUtils server) {
        this.server = server;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DebtsCtrl initializing");
        backgroundImage();
        keyShortCuts();
        refresh();

        // initialize close button
        cancelButton.setOnAction(this::cancelHandler);

        // render the columns
        renderCols();

        // setup the filter system
        filterSetup();

        System.out.println("DebtsCtrl finished initializing");
    }


    private void filterSetup(){
        System.out.println("Filtering");

        indexesToIds = new HashMap<>();
        List<Participant> participantList = server.getParticipantsOfEvent(server.getCurrentId());
        participants = FXCollections.observableList(participantList);
        participantNames=FXCollections.observableArrayList();

        // add all selection
        participantNames.add("-- All --");
        indexesToIds.put(0,(long)-1);

        int k=1;
        for(Participant p:participantList)
        {
            participantNames.add(p.getName());
            //map the position in the selection combo box to ids
            indexesToIds.put(k,p.getParticipantID());
            k++;
        }



        System.out.println("done Filtering");

        searchByComboBox.setItems(participantNames);
    }

    private void keyShortCuts() {
        cancelButton.requestFocus();

        debtTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT||event.getCode() == KeyCode.UP) cancelButton.requestFocus();
            if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.DOWN) refreshButton.requestFocus();
        });
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
        debtCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                server.getParticipantById(d.getValue().getDebtor()).getName()
                + " owes " + server.getParticipantById(d.getValue().getCreditor()).getName()
                + " " + Double.toString( d.getValue().getAmount())
                + d.getValue().getCurrency()));
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

                            //settle the debt
                            server.deleteDebt(currentEvent.getEventId(), debt.getDebtID());

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

        debtTable.getColumns().removeAll(settleCol);
        debtTable.getColumns().add(settleCol);
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
        // refreshes the Debt Manager and resets the items in the table
        var event = server.getEvent(server.getCurrentId());
        currentEvent = event;
        List<Debt> data = event.getDebts();
        FilteredList<Debt> filteredList = new FilteredList<>(FXCollections.observableList(data));

        // filter: debtor ID check
        filteredList.setPredicate(
            new Predicate<Debt>(){
                public boolean test(Debt debt){
                    // check if no filter
                    if(filterId == -1) {return true;}
                    // else check if debtor matches filter
                    return debt.getDebtor() == filterId;
                }
            }
        );

        // only set equal to new filter
        debtTable.setItems(filteredList);
    }

    public void filter(){
        filterId = indexesToIds.get(searchByComboBox.getSelectionModel().getSelectedIndex());
        refresh();
    }

    @FXML
    void personWasSelected()
    {
//        long id=indexesToIds.get(searchByComboBox.getSelectionModel().getSelectedIndex());
//        Participant x;
//        x=server.getParticipant(id);
//        if(x==null)
//            return;
//        fromxButton.setText("From "+x.getName());
//        includingxButton.setText("Including "+x.getName());
//        if(fromxButton.isSelected())
//            searchFromX(new ActionEvent());
//        else
//        if(includingxButton.isSelected())
//            searchIncludingX(new ActionEvent());
//        else
//            //this is useful if we deselect all options.
//            searchAll(new ActionEvent());
    }

    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "Debts.fxml");
    }
    public String getTitle(){
        return "Debts Page";
    }

    /**
     * The getter method for the currentEvent attribute
     *
     * @return currentEvent of this object
     **/
    public static Event getCurrentEvent() {
        return currentEvent;
    }

    /**
     * The setter method for the currentEvent attribute
     *
     * @param currentEvent The value to set currentEvent to
     **/
    public static void setCurrentEvent(Event currentEvent) {
        DebtsCtrl.currentEvent = currentEvent;
    }

    /**
     * The getter method for the filterId attribute
     *
     * @return filterId of this object
     **/
    public long getFilterId() {
        return filterId;
    }

    /**
     * The setter method for the filterId attribute
     *
     * @param filterId The value to set filterId to
     **/
    public void setFilterId(long filterId) {
        this.filterId = filterId;
    }
}
