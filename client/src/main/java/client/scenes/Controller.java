package client.scenes;

import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.util.Pair;

import static com.google.inject.Guice.createInjector;


public interface Controller {
  //imports used to swap scenes
 Injector INJECTOR = createInjector(new MyModule());
 MyFXML FXML = new MyFXML(INJECTOR);
 MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

  Pair<Controller, Parent> getPair();
  String getTitle();
  /* swapping to a page on button press template
  public void goToScene(ActionEvent e){
        System.out.println("Go to page");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, DummyCtrl.getPair(), DummyCtrl.getTitle());
    }
   */
}
