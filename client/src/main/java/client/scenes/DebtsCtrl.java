package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Pair;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.inject.Inject;

import static com.google.inject.Guice.createInjector;

public class DebtsCtrl implements Controller{


    @FXML
    private TableColumn<?, ?> amountCol;

    @FXML
    private Button cancelButton;

    @FXML
    private TableColumn<?, ?> creditorCol;

    @FXML
    private TableView<?> debtTable;

    @FXML
    private TableColumn<?, ?> debtorCol;

    @FXML
    void close(ActionEvent event) {

    }

    //Imports used to swap scenes
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    private Stage stage;
    private ServerUtils server;

    @Inject
    public DebtsCtrl(ServerUtils server) {
        this.server = server;
    }

    @FXML
    public void initialize() {}


    public static Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "Debts.fxml");
    }
    public static String getTitle(){
        return "Debts Page";
    }
}
