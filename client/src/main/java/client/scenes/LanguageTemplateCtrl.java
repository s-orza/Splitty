package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
//import javafx.fxml.Initializable;

import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;

import java.io.File;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static client.scenes.MainPageCtrl.currentLocale;

public class LanguageTemplateCtrl implements Controller, Initializable {
    private ServerUtils server;

    private Stage stage;

    ResourceBundle resourceBundle;

    @FXML
    private AnchorPane backGround;

    @FXML
    Button englishFlagButton;
    @FXML
     Button dutchFlagButton;
    @FXML
     Button germanFlagButton;
    @FXML
     Button spanishFlagButton;
    @FXML
    private Button englishButton;
    @FXML
    private Button dutchButton;
    @FXML
    private Button germanButton;
    @FXML
    private Button spanishButton;

    @FXML
    private Label downloadTemplateTitle;

    @FXML
    private Label instructionsText;

    @FXML
    private Button backButton;



    private void setUpLanguage(){
        resourceBundle = ResourceBundle.getBundle("messages", currentLocale);

        instructionsText.setText(resourceBundle.getString("instructionsText"));
        downloadTemplateTitle.setText(resourceBundle.getString("downloadTempleteTitle"));
        englishButton.setText(resourceBundle.getString("englishButtonText"));
        dutchButton.setText(resourceBundle.getString("dutchButtonText"));
        germanButton.setText(resourceBundle.getString("germanButtonText"));
        spanishButton.setText(resourceBundle.getString("spanishButtonText"));
        backButton.setText(resourceBundle.getString("backText"));
    }
    @Inject
    public LanguageTemplateCtrl(ServerUtils server) {
        this.server = server;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("in template");
//        backgroundImage();
        setUpLanguage();
        putFlag(englishFlagButton, "enFlag.png");
        putFlag(dutchFlagButton, "nlFlag.png");
        putFlag(germanFlagButton, "deFlag.png");
        putFlag(spanishFlagButton, "esFlag.png");
        backButton.setOnMouseClicked(e->{
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            MainPageCtrl mainPageCtrl = new MainPageCtrl(server);
            mainCtrl.initialize(stage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());
        });
        englishButton.setOnMouseClicked(e->{
            handleDownloadButton("messages_en.properties");
        });
        dutchButton.setOnMouseClicked(e->{
            handleDownloadButton("messages_nl.properties");
        });
        germanButton.setOnMouseClicked(e->{
            handleDownloadButton("messages_de.properties");
        });
        spanishButton.setOnMouseClicked(e->{
            handleDownloadButton("messages_es.properties");
        });
        englishFlagButton.setOnMouseClicked(e->{
            handleDownloadButton("messages_en.properties");
        });
        dutchFlagButton.setOnMouseClicked(e->{
            handleDownloadButton("messages_nl.properties");
        });
        germanFlagButton.setOnMouseClicked(e->{
            handleDownloadButton("messages_de.properties");
        });
        spanishFlagButton.setOnMouseClicked(e->{
            handleDownloadButton("messages_es.properties");
        });
    }

    public void handleDownloadButton(String fileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Properties File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Properties Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                Files.copy(Paths.get("oopp-team-33\\client\\src\\main\\resources\\" + fileName),
                        file.toPath());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
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

    private void putFlag(Button button, String path){
        Image image = new Image(path);
        BackgroundSize backgroundSize =
                new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                backgroundSize);

        Background background = new Background(backgroundImage);

        button.setBackground(background);
    }

    private void setUpDownloadListener(){

    }


    @Override
    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "LanguageTemplate.fxml");
    }

    @Override
    public String getTitle() {
        return "Language Template";
    }


}
