package client.scenes;

import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;

public class LoginAdminCtrl implements Controller{

    @FXML
    private TextField loginInput;
    //Imports used to swap scenes
    private Stage stage;
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    public void login(ActionEvent e) throws IOException {
        System.out.println("login to admin page");
        String passcode = loginInput.getText();
        System.out.println(passcode);
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, AdminPageCtrl.getPair());
    }
    public void close(ActionEvent e) throws IOException {
        System.out.println("close window");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, MainPageCtrl.getPair());
    }
    public static Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "loginAdmin.fxml");
    }

}