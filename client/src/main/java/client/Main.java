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

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.*;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  private static final Injector INJECTOR = createInjector(new MyModule());
  private static final MyFXML FXML = new MyFXML(INJECTOR);

  public static void main(String[] args) throws URISyntaxException, IOException {
    launch(); // creates app instance, calls init(), and then start(javafx.stage.Stage)
  }

  @Override
  public void start(Stage primaryStage) throws IOException {

    // defines the pages (add new pages here, replace Controller with correct controller, and fxml with fxml file name)
    var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
    var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");


        var serverSelect = FXML.load(DummyCtrl.class, "client", "scenes", "Dummy.fxml");
        var mainPage = FXML.load(MainPageCtrl.class, "client", "scenes", "mainPage.fxml");
        var adminPage = FXML.load(AdminPageCtrl.class, "client", "scenes", "adminPage.fxml");
        var loginAdminPage = FXML.load(LoginAdminCtrl.class, "client", "scenes", "loginAdmin.fxml");
        var createEvent = FXML.load(CreateEventCtrl.class, "client", "scenes", "CreateEvent.fxml");
        var eventPage = FXML.load(EventPageController.class, "client", "scenes", "EventPage.fxml");
        var addExpense = FXML.load(AddExpenseCtrl.class, "client", "scenes", "AddExpense.fxml");
        //add
        var addParticipant = FXML.load(AddParticipantCtrl.class, "client", "scenes", "AddParticipant.fxml");


    // calls mainCtrl with the pages
    var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
    mainCtrl.initialize(primaryStage, overview, add,serverSelect, mainPage, adminPage,
                        loginAdminPage, createEvent, eventPage, addExpense, addParticipant);
  }
}
