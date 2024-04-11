package client.scenes;

import client.utils.ServerUtils;
import commons.AppConfig;
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
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static client.scenes.MainPageCtrl.currentLocale;


public class ServerSelectCtrl implements Controller, Initializable {

    @FXML
    private TextField ipInput;
    @FXML
    private TextField portInput;
    @FXML
    private Button loginButton;
    @FXML
    private AnchorPane backGround;

    @FXML
    private Label selectServerText;

    //Imports used to swap scenes
    private Stage stage;
    private ServerUtils server;
    private AppConfig config = mainCtrl.getConfig();

    @Inject
    public ServerSelectCtrl(ServerUtils server) {
        this.server = server;

    }

    public void login(ActionEvent e){
        String ip = ipInput.getText();
        String port = portInput.getText();
        ipInput.setText(ip);
        portInput.setText(port);
        try{
            ServerUtils.setServerUrl("http://" + ip + ":" + port + "/");
        }catch (Exception exception){
            exception.printStackTrace();
            mainCtrl.popup("Couldn't connect to the server", " Warning", "Ok");
            return;
        }

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        MainPageCtrl mainPageCtrl = new MainPageCtrl(server);
        mainCtrl.initialize(stage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());
    }

    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "ServerSelect.fxml");
    }

    @Override
    public String getTitle() {
        return "Server select";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backgroundImage();
        keyShortCuts();
        resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        selectServerText.setText(resourceBundle.getString("selectServerText"));
        loginButton.setText(resourceBundle.getString("loginText"));
        ipInput.setText(config.getIp());
        portInput.setText(config.getPort());
    }

    private void keyShortCuts() {
        ipInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.ENTER) portInput.requestFocus();
        });
        portInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT||event.getCode() == KeyCode.ENTER) loginButton.requestFocus();
            if (event.getCode() == KeyCode.LEFT) ipInput.requestFocus();
        });
        loginButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) portInput.requestFocus();
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