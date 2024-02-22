package client.scenes;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginAdminCtrl {

    @FXML
    private TextField loginInput;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void login(ActionEvent e) throws IOException {
        System.out.println("login to admin page");
        String passcode = loginInput.getText();
        System.out.println(passcode);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminPage.fxml"));
        root = loader.load();
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void close(ActionEvent e) throws IOException {
        System.out.println("close window");
        Parent root = FXMLLoader.load(getClass().getResource("/mainPage.fxml"));
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}