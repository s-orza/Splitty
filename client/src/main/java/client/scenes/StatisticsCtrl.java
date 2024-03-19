package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Debt;
import commons.Expense;
import commons.Tag;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;

import static com.google.inject.Guice.createInjector;

public class StatisticsCtrl implements Controller, Initializable {
    @javafx.fxml.FXML
    private TableView<Debt> debtTable;

    @FXML
    private TableColumn<Debt, String> debtorCol;

    @FXML
    private TableColumn<Debt, String> creditorCol;

    @FXML
    private TableColumn<Debt, String> amountCol;

    @FXML
    private TableColumn<Debt, Void> settleCol;

    @FXML
    private Button cancelButton;

    //Imports used to swap scenes
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private static final MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

    private Stage stage;
    private ServerUtils server;
    @FXML
    private Button okButton;

    @FXML
    private PieChart pieChart;

    @FXML
    private Text titleId;

    @FXML
    private Text totalSpentText;
    @FXML
    private ListView<String> legendListView;
    private double totalAmount;
    private List<Expense> expenses;

    @Inject
    public StatisticsCtrl(ServerUtils server) {
        this.server = server;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        //get all Expenses
        expenses=server.getAllExpensesOfEvent(7952);

        totalAmount=0;
        Map<String, Double> tagsWithValues=new HashMap<>();
        for(Expense e:expenses)
        {
            totalAmount+=e.getMoney();
            System.out.println(e.getType()+"  "+e.getMoney());
            if(tagsWithValues.containsKey(e.getType()))
            {
                double k=tagsWithValues.get(e.getType());
                k=k+e.getMoney();
                tagsWithValues.put(e.getType(), k);
            }
            else
                tagsWithValues.put(e.getType(),e.getMoney());
        }
        //we need to change in future from EUR to other currencies
        totalSpentText.setText("Total sum spent: "+totalAmount+ " EUR");
        //create the pieChart
        createPieChart(tagsWithValues);
    }

    private void createPieChart(Map<String, Double> tagsWithValues)
    {
        ObservableList<PieChart.Data> pieChartData =FXCollections.observableArrayList();

        Map<String, String> tagColors = new HashMap<>();
        for(String tag: tagsWithValues.keySet()){
            double amount=tagsWithValues.get(tag);
            double percentage = (amount/ totalAmount) * 100;
            String name=tag + " (" + String.format("%.2f", percentage) + "%)";
            PieChart.Data data=new PieChart.Data(name,amount);
            //data.getNode().setStyle("-fx-pie-color: " + "#ffffff" + ";");
            //put an if to see if it EXISTED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if(!tagColors.containsKey(name))
                tagColors.put(name,"#f0ff11");
            pieChartData.add(data);
        }
        pieChart.setData(pieChartData);
        //add colors
        pieChartData.forEach(data -> {
            String color = tagColors.get(data.getName());
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        });
        System.out.println(tagColors);

        legendListView.setCellFactory(param -> new LegendListCell(tagColors));

        // Populate the ListView with legend item names
        legendListView.getItems().clear();
        legendListView.getItems().addAll(tagColors.keySet());



    }
    private static class LegendListCell extends ListCell<String> {
        private final Map<String, String> tagColors;

        public LegendListCell(Map<String, String> tagColors) {
            this.tagColors = tagColors;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                Circle circle = new Circle(6);
                circle.setFill(Color.web(tagColors.get(item)));

                setGraphic(new HBox(circle, new Label(item)));
            }
        }
    }

    public void exitPage(ActionEvent e){
        System.out.println("closed DebtsCtrl");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        mainCtrl.initialize(stage, EventPageCtrl.getPair(), EventPageCtrl.getTitle());
    }

    public static Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "StatisticsPage.fxml");
    }
    public static String getTitle(){
        return "Stats Page";
    }
}
