package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Debt;
import commons.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
                tagsWithValues.put(e.getType(),tagsWithValues.get(e.getType()+e.getMoney()));
            else
                tagsWithValues.put(e.getType(),e.getMoney());
        }

        //we need to change in future from EUR to other currencies
        totalSpentText.setText("Total sum spent: "+totalAmount+ " EUR");
        //create the pieChart
        createPieChart();
    }

    private void createPieChart()
    {
        ObservableList<PieChart.Data> pieChartData =FXCollections.observableArrayList();
//                FXCollections.observableArrayList(
//                new PieChart.Data("Item 1", 20),
//                new PieChart.Data("Item 2", 30),
//                new PieChart.Data("Item 3", 50)
//        );
        //double total = pieChartData.stream().mapToDouble(PieChart.Data::getPieValue).sum();
        for(Expense e:expenses){
        //for (PieChart.Data data : pieChartData) {
            double percentage = (e.getMoney()/ totalAmount) * 100;
            String name=e.getType() + " (" + String.format("%.2f", percentage) + "%)";
            PieChart.Data data=(new PieChart.Data(name,e.getMoney()));
            pieChartData.add(data);
        }
        pieChart.setData(pieChartData);
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
