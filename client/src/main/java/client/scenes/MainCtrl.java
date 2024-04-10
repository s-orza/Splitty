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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainCtrl {

    private Stage primaryStage;
    private static AppConfig config;
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
        primaryStage.setTitle(ServerUtils.getServerUrl() + " " + title);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            closeApp();
        });
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

    /**
     * This method reads the config, it's used to update the email provided in the config during runtime,
     * so you don't have to restart the app if you have an incorrect email setup. Since server also checks the
     * current email in the config when sending an email decided this is the best approach
     */
    public void refresh(){
        AppConfig config = new AppConfig();
        try {
            AppConfig newConfig = readConfig();
            List<String> cur = Arrays.asList("EUR", "USD", "RON", "CHF");
            if(newConfig.getCurrency() != null && cur.contains(newConfig.getCurrency())){
                config.setCurrency(newConfig.getCurrency());
            }
            else{
                System.out.println("Config had incorrect Curency!");
            }
            if(newConfig.getIp() != null){
                config.setIp(newConfig.getIp());
            }else{
                System.out.println("Config had incorrect Ip!");
            }
            if(newConfig.getPort() != null){
                config.setPort(newConfig.getPort());
            }else{
                System.out.println("Config had incorrect Port!");
            }
            List<String> lan = Arrays.asList("en", "nl", "es", "US", "de", "xx");
            if(newConfig.getLang() != null && lan.contains(newConfig.getLang().getLanguage())){
                config.setLang(newConfig.getLang());
            }else{
                System.out.println("Config had incorrect Language!");
            }
            if(newConfig.getEmail() != null && newConfig.getEmail().matches(
                    "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")){
                config.setEmail(newConfig.getEmail());
            }else{
                System.out.println("Config had incorrect Email!");
            }
            config.setPassword(newConfig.getPassword());
            config.setRecentEvents(newConfig.getRecentEvents());
        }catch (Exception e){
            e.printStackTrace();
        }
        this.config = config;
    }

    public void setUrl(String ip, String port) {
        this.url = "http://" + ip + ":" + port + "/";
    }
    public static AppConfig getConfig() {
        return config;
    }

    public static void setConfig(AppConfig config) {
        MainCtrl.config = config;
    }

    public void closeApp() {
        lang = MainPageCtrl.getCurrentLocale();
        ArrayList<Event> serverEvents = (ArrayList<Event>) server.getEvents();
        serverEvents.sort(new EventActivitySort());
        ArrayList<Long> exported = new ArrayList<>();
        int i = recents.size();
        while (i > 0 && !serverEvents.isEmpty()) {
            Long current = serverEvents.removeFirst().getEventId();
            if (recents.contains(current)) {
                exported.add(current);
                i--;
            }
        }
        url = server.getServerUrl();
        String id = url.replace("/", "").split(":")[1];
        String port = url.replace("/", "").split(":")[2];
        AppConfig savedConfig = new AppConfig(currency, exported, id, port, lang);
        savedConfig.setEmail(config.getEmail());
        savedConfig.setPassword(config.getPassword());
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, savedConfig);
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
    public void popup(String text, String title,String buttonText){
        VBox layout = new VBox(10);
        Label label = new Label(text);
        Button button = new Button(buttonText);

        // Set up the stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        button.setOnAction(e -> {
            popupStage.close();
        });

        // Set up the layout
        layout.getChildren().addAll(label, button);
        layout.setAlignment(Pos.CENTER);

        // Set the scene and show the stage
        Scene scene = new Scene(layout, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    private AppConfig readConfig() throws Exception {
        File selectedFile = new File("App-Config.json");
        if (selectedFile != null && selectedFile.getName().contains(".json")) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                AppConfig config = mapper.readValue(selectedFile, AppConfig.class);
                return config;
            } catch (FileNotFoundException e) {
                System.out.println("No config file was found!");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Running default settings");
        return new AppConfig();
    }
}




