package client.scenes;

import client.utils.ServerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;

public class LoginAdminCtrl implements Controller{

    @FXML
    private TextField loginInput;
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
        System.out.println(passcode);
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
    public String getTitle() {
        return "Login Page";
    }

}