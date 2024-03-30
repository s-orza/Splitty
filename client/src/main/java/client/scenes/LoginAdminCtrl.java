package client.scenes;

import client.utils.ServerUtils;
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

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static client.scenes.MainPageCtrl.currentLocale;


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
    private ServerUtils server;

    @Inject
    public LoginAdminCtrl(ServerUtils server) {
        this.server = server;
    }

    public void login(ActionEvent e){
        System.out.println("login to admin page");
        String passcode = loginInput.getText();
        System.out.println("submitted pass:"+passcode);

        // authenticate password
        String password = server.getPass().getPassword();
        if(!password.equals(passcode)){
            return;
        }

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        AdminPageCtrl adminPageCtrl = new AdminPageCtrl(server);
        mainCtrl.initialize(stage, adminPageCtrl.getPair(), adminPageCtrl.getTitle());
    }


    public void close(ActionEvent e){
        System.out.println("close window");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        MainPageCtrl mainPageCtrl = new MainPageCtrl(server);
        mainCtrl.initialize(stage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());
    }
    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "loginAdmin.fxml");
    }

    @Override
    public String getTitle() {
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