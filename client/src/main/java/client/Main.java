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

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.*;
import client.utils.ServerUtils;

import commons.Password;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) throws URISyntaxException, IOException {
    launch(); // creates app instance, calls init(), and then start(javafx.stage.Stage)
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    // calls mainCtrl with the pages (change MainPageCtrl to your desired page to be shown)

    MainCtrl mainCtrl = new MainCtrl();
    MainPageCtrl mainPageCtrl = new MainPageCtrl(new ServerUtils());
    mainCtrl.initialize(primaryStage, mainPageCtrl.getPair(), mainPageCtrl.getTitle());

    AdminPageCtrl adminPageCtrl = new AdminPageCtrl(new ServerUtils());

    primaryStage.setOnCloseRequest(e-> {
      adminPageCtrl.stop();
    });

    EventPageCtrl eventPage = new EventPageCtrl(new ServerUtils());

    primaryStage.setOnCloseRequest(e-> {
      eventPage.stop();
    });
  }
}
