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
    private DummyCtrl createEventCtrl;
    private Scene createEvent;
    // joinEvent
    private DummyCtrl joinEventCtrl;
    private Scene joinEvent;
    // eventPage
    private DummyCtrl eventPageCtrl;
    private Scene eventPage;
    // addExpenses
    private DummyCtrl addExpensesCtrl;
    private Scene addExpenses;
    // addParticipant
    private DummyCtrl addParticipantCtrl;
    private Scene addParticipant;
    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
            Pair<AddQuoteCtrl, Parent> add) {

        // assign all vars to their respective Controllers/Scenes
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        // set first page
        showOverview();
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
}