package client.scenes;

import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.ResourceBundle;

import static client.scenes.MainPageCtrl.currentLocale;
import static com.google.inject.Guice.createInjector;

public class LoginAdminCtrl implements Controller, Initializable {

    @FXML
    private TextField loginInput;

    @FXML
    private Button cancelButton;

    @FXML
    private Button loginButton;

    @FXML
    private Text passwordText;
    //Imports used to swap scenes
    private Stage stage;
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    public void login(ActionEvent e){
        System.out.println("login to admin page");
        String passcode = loginInput.getText();
        System.out.println(passcode);
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, AdminPageCtrl.getPair(), AdminPageCtrl.getTitle());
    }
    public void close(ActionEvent e){
        System.out.println("close window");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, MainPageCtrl.getPair(), MainPageCtrl.getTitle());
    }
    public static Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "loginAdmin.fxml");
    }
    public static String getTitle() {
        return "Login Page";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        loginButton.setText(resourceBundle.getString("loginText"));
        cancelButton.setText(resourceBundle.getString("cancelText"));
        passwordText.setText(resourceBundle.getString("passwordText"));
    }
}