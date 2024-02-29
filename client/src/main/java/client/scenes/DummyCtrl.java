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

import client.MyFXML;
import client.MyModule;
import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Pair;

import static com.google.inject.Guice.createInjector;

public class DummyCtrl implements Controller{

    private Stage stage;
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    //add FXML elements with annotation

    public static Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "loginAdmin.fxml");
    }
    public static String getTitle() {
        return "Login Page";
    }
}
