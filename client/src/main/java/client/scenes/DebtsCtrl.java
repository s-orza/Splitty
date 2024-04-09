package client.scenes;

import client.utils.ServerUtils;
import commons.Debt;
import commons.Event;
import commons.MailStructure;
import commons.Participant;
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
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

public class DebtsCtrl implements Controller, Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> searchByComboBox;
    @FXML
    private Accordion accordion;

    @FXML
    private AnchorPane backGround;

    @FXML
    private Button refreshButton;

    @FXML
    Button invite;

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

        // setup the filter system
        filterSetup();

        System.out.println("DebtsCtrl finished initializing");
    }
    private void keyShortCuts() {
        cancelButton.requestFocus();

        accordion.setOnKeyPressed(event -> {
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
        renderAccordion(filteredList);
    }

    public void renderAccordion(FilteredList<Debt> filteredList){
        String title;
        Collection<TitledPane> panes = new ArrayList<>();
        for (Debt d: filteredList) {
            // debt title
            title = server.getParticipantById(d.getDebtor()).getName()
                            + " owes " + server.getParticipantById(d.getCreditor()).getName()
                            + " " + Double.toString( d.getAmount())
                            + d.getCurrency();
            // settle button
            Button button = new Button("Settle");
            button.setOnAction(e-> {
                settleAction(d.getDebtID());
            });

            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(e-> {
                invite(d);
            });
            mainCtrl.refresh();
            if(mainCtrl.getConfig().getEmail() == null ||
                    server.getParticipant(d.getDebtor()) == null ||
                    !mainCtrl.getConfig().getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") ||
                            !server.getParticipant(d.getDebtor()).getEmail().matches(
                                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
                inviteButton.setStyle("-fx-opacity: 0.5;");
                inviteButton.setDisable(true);
                return;
            }

            // debt Description
            String description = descriptionBuilder(server.getParticipantById(d.getCreditor()));

            VBox vbox = new VBox();
            vbox.getChildren().addAll(new Label(description), button, inviteButton);

            TitledPane tp = new TitledPane(title, vbox);
            panes.add(tp);
        }

        accordion.getPanes().setAll(panes);
    }


    public void invite(Debt debt){
        Participant debtor = server.getParticipant(debt.getDebtor());
        Participant creditor = server.getParticipant(debt.getCreditor());

        if(server.sendMail(debtor.email,
                new MailStructure("Please settle your debt!",
                        "You owe " + debt.getAmount() + " " +  debt.getCurrency() +
                                " to " + creditor.getName() + ".\n" +
                                "Please settle your debt with them as soon as possible!\n" +
                                descriptionBuilder(creditor)+
                                "\nSee your debt using the Splitty app and join your friends!\nIp: " +
                                server.getServerUrl() +
                                ".\nUse this code to join: " +
                                server.getCurrentId()))){
            mainCtrl.popup("Email sent succesfully", "Succes", "OK");
        }
        else{
            mainCtrl.popup("Email failed", "Warning", "OK");
        }
    }
    public String descriptionBuilder(Participant p){
        // null and empty checks
        if(Objects.isNull(p.getBic()) ||
                Objects.isNull(p.getIban())||
                Objects.equals(p.getBic(), "") ||
                Objects.equals(p.getIban(), ""))
        {
            return "Bank information unavailable! Please transfer the money in person.";
        }

        String result = "Bank information information available! Please transfer the money to:\r" +
                "Account Holder: " + p.getName() + "\r" +
                "IBAN: " + p.getIban() + "\r" +
                "BIC: " + p.getBic() + "\r";

        return result;
    }

    public void settleAction(long debtId){
        server.deleteDebt(currentEvent.getEventId(), debtId);

        //delete and refresh
        refresh();
    }

    public void filter(){
        filterId = indexesToIds.get(searchByComboBox.getSelectionModel().getSelectedIndex());
        refresh();
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
