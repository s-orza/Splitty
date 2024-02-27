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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    // add all Controller and Scene vars here
    // quoteOverview
    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    // addQuote
    private AddQuoteCtrl addCtrl;
    private Scene add;

    // serverSelect
    private DummyCtrl serverSelectCtrl;
    private Scene serverSelect;
    // mainPage
    private DummyCtrl mainPageCtrl;
    private Scene mainPage;
    // adminPage
    private DummyCtrl adminPageCtrl;
    private Scene adminPage;
    // createEvent
    private CreateEventCtrl createEventCtrl;
    private Scene createEvent;
    // joinEvent
    private DummyCtrl joinEventCtrl;
    private Scene joinEvent;
    // eventPage
    private EventPageController eventPageCtrl;
    private Scene eventPage;
    // addExpense
    private AddExpenseCtrl addExpenseCtrl;
    private Scene addExpense;
    // addParticipant
    private DummyCtrl addParticipantCtrl;
    private Scene addParticipant;
    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
                           Pair<AddQuoteCtrl, Parent> add, Pair<DummyCtrl, Parent> serverSelect, Pair<DummyCtrl, Parent> mainPage,
                           Pair<DummyCtrl, Parent> adminPage, Pair<CreateEventCtrl, Parent> createEvent,
                           Pair<DummyCtrl, Parent> joinEvent, Pair<EventPageController, Parent> eventPage,
                           Pair<AddExpenseCtrl, Parent> addExpense, Pair<DummyCtrl, Parent> addParticipant) {

        // assign all vars to their respective Controllers/Scenes
        this.primaryStage = primaryStage;

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        // our pages
        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());
        this.serverSelectCtrl = serverSelect.getKey();
        this.serverSelect = new Scene(serverSelect.getValue());

        this.mainPageCtrl = mainPage.getKey();
        this.mainPage = new Scene(mainPage.getValue());

        this.adminPageCtrl = adminPage.getKey();
        this.adminPage = new Scene(adminPage.getValue());

        this.createEventCtrl = createEvent.getKey();
        this.createEvent = new Scene(createEvent.getValue());

        this.joinEventCtrl = joinEvent.getKey();
        this.joinEvent = new Scene(joinEvent.getValue());

        this.eventPageCtrl = eventPage.getKey();
        this.eventPage = new Scene(eventPage.getValue());

        this.addExpenseCtrl = addExpense.getKey();
        this.addExpense = new Scene(addExpense.getValue());

        this.addParticipantCtrl = addParticipant.getKey();
        this.addParticipant = new Scene(addParticipant.getValue());

        // set first page
        showEventPage();
        primaryStage.show();
    }

    // functions to set new stage
    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    // Our pages
    public void showServerSelect() {
        primaryStage.setTitle("Server Select");
        primaryStage.setScene(serverSelect);
    }

    public void showMainPage() {
        primaryStage.setTitle("Main Page");
        primaryStage.setScene(mainPage);
    }

    public void showAdminPage() {
        primaryStage.setTitle("Admin Page");
        primaryStage.setScene(adminPage);
    }

    public void showCreateEvent() {
        primaryStage.setTitle("Create Event");
        primaryStage.setScene(createEvent);
    }

    public void showJoinEvent() {
        primaryStage.setTitle("Join Event");
        primaryStage.setScene(joinEvent);
    }

    public void showEventPage() {
        primaryStage.setTitle("Event Page");
        primaryStage.setScene(eventPage);
    }

    public void showAddExpense() {
        primaryStage.setTitle("Add Expense");
        primaryStage.setScene(addExpense);
    }

    public void showAddParticipant() {
        primaryStage.setTitle("Add Participant");
        primaryStage.setScene(addParticipant);
    }
}