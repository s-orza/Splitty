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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

import static client.scenes.MainPageCtrl.currentLocale;

public class DebtsCtrl implements Controller, Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> searchByComboBox;
    @FXML
    private Accordion accordion;

    @FXML
    private Text preferredCurrencyText;
    @FXML
    private AnchorPane backGround;
    @FXML
    private ComboBox<String> moneyTypeSelector;

    @FXML
    private Button refreshButton;

    @javafx.fxml.FXML
    private Button filterButton;

    @FXML
    private Label debtsText;

    private Stage stage;
    private ServerUtils server;

    private static Event currentEvent;
    private long filterId = -1;
    private ObservableList<Participant> participants;
    private ObservableList<String> participantNames;
    private Map<Integer,Long> indexesToIds;
    private List<String> expenseTypesAvailable=new ArrayList<>();

    private ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);

    @Inject
    public DebtsCtrl(ServerUtils server) {
        this.server = server;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DebtsCtrl initializing");

        backgroundImage();
        keyShortCuts();

        //initialise the expenseTypesAvailable
        expenseTypesAvailable.clear();
        expenseTypesAvailable.addAll(List.of("EUR", "USD", "RON", "CHF"));

        refresh();
        toggleLanguage();
        // initialize close button
        cancelButton.setOnAction(this::cancelHandler);

        // setup the filter system
        filterSetup();

        System.out.println("DebtsCtrl finished initializing");
    }
    private void keyShortCuts() {
        cancelButton.requestFocus();
        backGround.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Scene scene = (backGround.getScene());
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        cancelButton.fire();
                    }
                });
            }
        });

        accordion.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT||event.getCode() == KeyCode.UP) cancelButton.requestFocus();
            if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.DOWN) refreshButton.requestFocus();
        });
    }

    private void toggleLanguage(){
        cancelButton.setText(resourceBundle.getString("cancelText"));
        filterButton.setText(resourceBundle.getString("filterText"));
        refreshButton.setText(resourceBundle.getString("refreshText"));
        debtsText.setText(resourceBundle.getString("debtsText"));
        preferredCurrencyText.setText(resourceBundle.getString("preferredCurrencyText"));
        searchByComboBox.setPromptText(resourceBundle.getString("selectPersonText"));
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
        participantNames.add(resourceBundle.getString("allText"));
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

    private void cancelHandler(ActionEvent e){
        System.out.println("closed DebtsCtrl");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
        mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
    }

    public void refresh() {
        System.out.println("Refreshing Debts...");
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

        // currency setup
        moneyTypeSelector.setOnAction(null);
        moneyTypeSelector.getItems().clear();
        moneyTypeSelector.getItems().addAll(expenseTypesAvailable);
        //here to change with the value from the config file
        moneyTypeSelector.setValue(MainCtrl.getCurrency());
        moneyTypeSelector.setOnAction(this::handleCurrencySelection);
    }

    public void renderAccordion(FilteredList<Debt> filteredList){
        String title;
        Collection<TitledPane> panes = new ArrayList<>();
        boolean emailAvailable = true;
        boolean paymentAvailable = true;
        for (Debt d: filteredList) {
            // debt title
            double number =server.convertCurrency(LocalDate.now() + "", d.getCurrency(),
                    MainCtrl.getCurrency(), d.getAmount());
            
            // check if amount is <0.01
            String amount = String.format("%.2f",number);
            if(number<0.01){
                amount = "<0.01";
            }
            title = server.getParticipantById(d.getDebtor()).getName()
                            + " " + resourceBundle.getString("owesText") + " " +
                    server.getParticipantById(d.getCreditor()).getName()
                            + " " + amount
                            + MainCtrl.getCurrency();

            // settle button
            Button button = new Button(resourceBundle.getString("settleText"));
            button.setOnAction(e-> {
                settleAction(d.getDebtID());
            });

            Button remindButton = new Button(resourceBundle.getString("reminderText"));
            remindButton.setOnAction(e-> {
                invite(d);
            });
            mainCtrl.refresh();
            if(mainCtrl.getConfig().getEmail() == null ||
                    server.getParticipant(d.getDebtor()) == null ||
                    !mainCtrl.getConfig().getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") ||
                            !server.getParticipant(d.getDebtor()).getEmail().matches(
                                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
                remindButton.setStyle("-fx-opacity: 0.5;");
                remindButton.setDisable(true);
                emailAvailable = false;
            }
            Participant p = server.getParticipantById(d.getCreditor());
            if(Objects.isNull(p.getBic()) ||
                    Objects.isNull(p.getIban())||
                    Objects.equals(p.getBic(), "") ||
                    Objects.equals(p.getIban(), "")){
                paymentAvailable = false;
            }

            // debt Info:
            String description = descriptionBuilder(p);

            // settle and reminder buttons
            HBox buttons = new HBox();
            buttons.getChildren().addAll(button, remindButton);
            buttons.setSpacing(10);

            // description + buttons
            VBox vbox = new VBox();
            vbox.getChildren().addAll(new Label(description), buttons);
            buttons.setSpacing(5);

            HBox icons = new HBox();
            icons.setSpacing(5);

            TitledPane tp = new TitledPane(title, vbox);
            try {
                //load the icons
                Image bankIcon=new Image(getClass().getResourceAsStream("/balance.png"));
                Image emailIcon=new Image(getClass().getResourceAsStream("/email.png"));
                ImageView bankView=new ImageView(bankIcon);
                ImageView emailView=new ImageView(emailIcon);
                bankView.setFitWidth(15);
                bankView.setFitHeight(15);
                emailView.setFitWidth(15);
                emailView.setFitHeight(15);

                // grey out if unavailable
                if(!paymentAvailable){bankView.setStyle("-fx-opacity: 0.5;");}
                if(!emailAvailable){emailView.setStyle("-fx-opacity: 0.5;");}


                icons.getChildren().addAll(bankView, emailView);
                tp.setGraphic(icons);
            }
            catch (Exception ignored) {}
            panes.add(tp);
        }

        accordion.getPanes().setAll(panes);
    }


    public void invite(Debt debt){
        Participant debtor = server.getParticipant(debt.getDebtor());
        Participant creditor = server.getParticipant(debt.getCreditor());

        if(server.sendMail(debtor.email,
                new MailStructure(resourceBundle.getString("emailDebtSubject"),
                        resourceBundle.getString("youOwe") + " " +
                                debt.getAmount() + " " +  debt.getCurrency() +
                                " --> " + creditor.getName() + ".\n" +
                                resourceBundle.getString("pSettle") + "\n" +
                                descriptionBuilder(creditor)+
                                "\n" + resourceBundle.getString("jSettle") + "\nIp: " +
                                server.getServerUrl() +
                                ".\n" + resourceBundle.getString("emailCode") + " " +
                                server.getCurrentId()))){
            mainCtrl.popup(resourceBundle.getString("emailOk"),
                    resourceBundle.getString("success"), "OK");
        }
        else{
            mainCtrl.popup(resourceBundle.getString("emailFail"), resourceBundle.getString("warningText"), "OK");
        }
    }
    public String descriptionBuilder(Participant p){
        // null and empty checks
        if(Objects.isNull(p.getBic()) ||
                Objects.isNull(p.getIban())||
                Objects.equals(p.getBic(), "") ||
                Objects.equals(p.getIban(), ""))
        {
            return resourceBundle.getString("bankInfoUnavailableText");
        }

        String result = resourceBundle.getString("bankInfoAvailable")+ "\r" +
                resourceBundle.getString("accountHolderText")+ " " + p.getName() + "\r" +
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
     if(searchByComboBox.getSelectionModel().getSelectedIndex()==-1)
     {
         return;
     }
        filterId = indexesToIds.get(searchByComboBox.getSelectionModel().getSelectedIndex());
        refresh();
    }

    /**
     * This is a basic handler that checks when you change the currency type
     * @param event an event
     */
    @FXML
    private void handleCurrencySelection(ActionEvent event) {
        String selectedMoneyType=moneyTypeSelector.getValue();
        //update in the config file
        System.out.println(selectedMoneyType);
        if(!MainCtrl.getCurrency().equals(selectedMoneyType))
        {
            mainCtrl.setCurrency(selectedMoneyType);
            refresh();
        }
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
