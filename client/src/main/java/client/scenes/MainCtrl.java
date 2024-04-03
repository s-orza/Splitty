/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.*;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Locale;

public class MainCtrl {

    private Stage primaryStage;
    private static ArrayList<Long> recents;
    private static String currency;
    private static String url;
    private static Locale lang;

    private Controller ctrl;
    private Scene scene;
    private ServerUtils server = new ServerUtils();


    public void initialize(Stage primaryStage, Pair<Controller, Parent> dummy, String title){
        // assign all vars to their respective Controllers/Scenes
        this.primaryStage = primaryStage;
        this.ctrl = dummy.getKey();
        this.scene = new Scene(dummy.getValue());
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            closeApp();
        });
    }

    public void popup(String text, String title) {
        VBox layout = new VBox(10);
        Label label = new Label(text);
        Button cancelButton = new Button("Cancel");

        // Set up the stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        cancelButton.setOnAction(e -> {
            popupStage.close();
        });

        // Set up the layout
        layout.getChildren().addAll(label, cancelButton);
        layout.setAlignment(Pos.CENTER);

        // Set the scene and show the stage
        Scene scene = new Scene(layout, 370, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    public ArrayList<Long> getRecents() {
        return recents;
    }

    public static String getUrl() {
        return url;
    }

    public void setRecents(ArrayList<Long> recents) {
        this.recents = recents;
    }

    public void removeRecent(long id) {
        this.recents.remove(id);
    }

    public void addRecent(long id) {
        if (!recents.contains(id)) {
            recents.add(id);
        }
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static String getCurrency() {
        return currency;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void closeApp() {
        lang = MainPageCtrl.getCurrentLocale();
        ArrayList<Event> serverEvents= (ArrayList<Event>) server.getEvents();
        serverEvents.sort(new EventActivitySort());
        ArrayList <Long> exported = new ArrayList<>();
        int i = recents.size();
        while (i > 0 && !serverEvents.isEmpty()) {
            Long current = serverEvents.removeFirst().getEventId();
            if(recents.contains(current)){
                exported.add(current);
                i--;
            }
        }
        url = server.getServerUrl();
        AppConfig config = new AppConfig(currency, exported, url, lang);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, config);
            String json = writer.toString();
            System.out.println(json);
            String filePath = new File("").getAbsolutePath().replace("\\", "/");
            filePath += "/";
            //Open file
            // FileOutputStream Class Used
            FileOutputStream fileOutputStream = new FileOutputStream(filePath + "App-Config.json");
            // Write data to the file if needed.
            fileOutputStream.write(json.getBytes());
            //Close file
            fileOutputStream.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        server.stop();
    }
}




