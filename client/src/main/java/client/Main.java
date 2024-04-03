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
package client;

import client.scenes.*;
import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.AppConfig;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Application {

  MainCtrl mainCtrl = new MainCtrl();
  MainPageCtrl mainPageCtrl;

  public static void main(String[] args) throws URISyntaxException, IOException {
    launch(); // creates app instance, calls init(), and then start(javafx.stage.Stage)
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    // calls mainCtrl with the pages (change MainPageCtrl to your desired page to be shown)
    AppConfig config = new AppConfig();
    try {
      AppConfig newConfig = readConfig();
      if(newConfig.getCurrency() != null){
        config.setCurrency(newConfig.getCurrency());
      }
      else{
        System.out.println("Config had incorrect Curency!");
      }
      if(     newConfig.getIp() != null){
        config.setIp(newConfig.getIp());
      }else{
        System.out.println("Config had incorrect Ip!");
      }
      if(newConfig.getPort() != null){
        config.setPort(newConfig.getPort());
      }else{
        System.out.println("Config had incorrect Port!");
      }
      if(newConfig.getLang() != null){
        config.setLang(newConfig.getLang());
      }else{
        System.out.println("Config had incorrect Language!");
      }
      config.setRecentEvents(newConfig.getRecentEvents());
    }catch (Exception e){
      e.printStackTrace();
    }
    try{
      ServerUtils.setServerUrl("http://" + config.getIp() + ":" + config.getPort() + "/");
    }catch (Exception exception){
      exception.printStackTrace();
    }
    mainPageCtrl= new MainPageCtrl(new ServerUtils());
    mainCtrl.setRecents(config.getRecentEvents());
    mainCtrl.setCurrency(config.getCurrency());
    mainCtrl.setUrl(config.getIp(), config.getPort());
    mainPageCtrl.setLang(config.getLang());
    mainCtrl.initialize(primaryStage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());
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
