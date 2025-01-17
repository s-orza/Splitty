package client.scenes;

import client.utils.ServerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
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
    private Label loginText;

    @FXML
    private Text passwordText;
    @FXML
    private AnchorPane backGround;
    //Imports used to swap scenes
    private Stage stage;
    private ServerUtils server;

    @Inject
    public LoginAdminCtrl(ServerUtils server) {
        this.server = server;

    }

    public void login(ActionEvent e){
        String passcode = loginInput.getText();
        // authenticate password
        String password = server.getPass().getPassword();
        if(!password.equals(passcode)){
            loginInput.setStyle("-fx-background-color: #FF999C;");
            loginInput.setText("");
            ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
            passwordText.setText(resourceBundle.getString("wrongPasswordText"));
            return;
        }

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        AdminPageCtrl adminPageCtrl = new AdminPageCtrl(server);
        mainCtrl.initialize(stage, adminPageCtrl.getPair(), adminPageCtrl.getTitle());
    }


    public void close(ActionEvent e){
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
        backgroundImage();
        keyShortCuts();
        resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        loginButton.setText(resourceBundle.getString("loginText"));
        cancelButton.setText(resourceBundle.getString("cancelText"));
        passwordText.setText(resourceBundle.getString("passwordText"));
        loginText.setText(resourceBundle.getString("loginLabelText"));
    }

    private void keyShortCuts() {
        loginInput.requestFocus();

        loginInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) loginButton.requestFocus();
            if (event.getCode() == KeyCode.ENTER) loginButton.requestFocus();
            if (event.getCode() == KeyCode.LEFT) cancelButton.requestFocus();
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
}