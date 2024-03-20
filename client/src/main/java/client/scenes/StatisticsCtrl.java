package client.scenes;

import client.utils.ServerUtils;
import commons.Expense;
import commons.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.geometry.Pos;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;

public class StatisticsCtrl implements Controller, Initializable {

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
    private ListView<String> tagsListView;
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
        totalAmount=0;
        //get all Expenses
        long eventId=7952;
        eventId = server.getCurrentId();
        expenses=server.getAllExpensesOfEvent(eventId);
        //get tags with values (each tag with the amount of money it contains.)
        Map<String, Double> tagsWithValues=getTagsWithValuesFromExpenses(expenses);
        //create the pieChart
        createPieChart(tagsWithValues);
        updateTextsOnTheScreen();
        //create the list of tags used in this event
        createTagsUsedInThisEventList(tagsWithValues.keySet());
    }

    /**
     * It creates from a list of expenses a map of tag names and total value
     * of that tag in the event.
     * @param expensesList the list of expenses
     * @return a map with tags and total values of the tags.
     */
    private Map<String, Double> getTagsWithValuesFromExpenses(List<Expense> expensesList)
    {
        Map<String, Double> tagsWithValues=new HashMap<>();
        for(Expense e:expensesList)
        {
            System.out.println(e.getType()+"  "+e.getMoney());
            String type=e.getType();
            if(type==null || type.equals(""))
                type="other";
            if(tagsWithValues.containsKey(type))
            {
                double k=tagsWithValues.get(type);
                //in case the currency is not in EUR, we would change it here.
                k=k+e.getMoney();
                tagsWithValues.put(type,k);
            }
            else
                tagsWithValues.put(type,e.getMoney());
        }
        return tagsWithValues;
    }

    /**
     * Here it create the pie chart and set colors base on the input.
     * Also, it creates a legend with colors, names and percentages.
     * This is a good function to call when the page needs to refresh
     * @param tagsWithValues the input
     */
    private void createPieChart(Map<String, Double> tagsWithValues)
    {
        ObservableList<PieChart.Data> pieChartData =FXCollections.observableArrayList();
        //calculate the total amount of money
        totalAmount=0;
        totalAmount=expenses.stream().mapToDouble(x->x.getMoney()).sum();
        long eventId=7952;
        eventId = server.getCurrentId();
        //a map with the name (including percentages) and the color of a tag
        Map<String, String> tagColors = new HashMap<>();

        //it maps a name (ex "food 23%") to the name for the legend (ex: "food 200 EUR")
        Map<String, String> namesForLegend = new HashMap<>();

        for(String tag: tagsWithValues.keySet()){
            double amount=tagsWithValues.get(tag);
            double percentage=(amount/totalAmount)*100;
            //the tag name + %
            String name=tag+ " ("+String.format("%.2f", percentage)+"%)";
            //name for the legend.
            String legendName=tag+"  "+amount+" EUR";

            PieChart.Data data=new PieChart.Data(name,amount);
            pieChartData.add(data);
            //preparing tagColors to use for personalized colors

            Tag t=server.getTagByIdOfEvent(tag,eventId);
            if(t!=null)
                tagColors.put(name,t.getColor());
            else
                tagColors.put(name,"#aaaaaa");
            //This is a gray color.

            namesForLegend.put(name,legendName);

        }
        pieChart.setData(pieChartData);
        //add colors to the pie chart
        pieChartData.forEach(data -> {
            String color = tagColors.get(data.getName());
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        });

        System.out.println(tagColors);

        // create the legend
        legendListView.setCellFactory(param -> new LegendListCell(tagColors,namesForLegend));

        // put data in the legend
        legendListView.getItems().clear();
        legendListView.getItems().addAll(tagColors.keySet());
    }
    private void createTagsUsedInThisEventList(Set<String> tags)
    {
        tagsListView.getItems().clear();
        tagsListView.getItems().addAll(tags.stream().sorted().toList());
        tagsListView.setCellFactory(param -> new TagsListViewCell());
        //we need these for making the front end beautiful
        tagsListView.setSelectionModel(null);
        tagsListView.setFocusTraversable(false);
    }

    /**
     * Here it updates the total amount text and the title
     */
    private void updateTextsOnTheScreen()
    {
        //update the name of the event
        long eventId=7952;
        eventId = server.getCurrentId();
        titleId.setText("Statistics for event "+eventId);
        //in case we don't have the total amount in EUR, we need to change it to EUR
        totalSpentText.setText("Total sum spent: "+totalAmount+ " EUR");
    }

    /**
     * Class used for the legend.
     */
    private class LegendListCell extends ListCell<String> {
        private final Map<String, String> tagColors;
        private final Map<String, String> namesForLegend;

        public LegendListCell(Map<String, String> tagColors,
                              Map<String, String> namesForLegend) {
            this.tagColors = tagColors;
            this.namesForLegend=namesForLegend;
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
                //for setting the size of the text (by default it is 12)
                Label label=new Label(namesForLegend.get(item));
                label.setFont(new Font(13));
                //we need to align the circle and the text
                HBox hbox=new HBox(circle, label);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setSpacing(3);
                setGraphic(hbox);
            }
        }
    }
    private class TagsListViewCell extends ListCell<String>{
        private HBox container;
        private Label tagLabel;
        private Button editButton;
        private Button deleteButton;

        private TagsListViewCell() {
            container=new HBox();
            tagLabel=new Label();
            editButton=new Button("E");
            deleteButton=new Button("D");
            //edit the size of buttons/icons
            editButton.setPrefSize(12, 12);
            deleteButton.setPrefSize(25, 12);

            editButton.setOnAction(event -> handleEdit(getItem()));
            deleteButton.setOnAction(event -> handleDelete(getItem()));

            // Add components to the element (the line from the list view)
            container.getChildren().addAll(deleteButton,editButton,tagLabel);
            container.setHgrow(tagLabel, Priority.ALWAYS);

            // Set spacing between components, 4 it is good
            container.setSpacing(4);

            // Set padding between lines
            container.setPadding(new javafx.geometry.Insets(1));
        }
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                tagLabel.setText(item);
                setGraphic(container);
            }
        }
    }
    private void handleEdit(String tag) {
        // Here we would edit the tag
        System.out.println("Edit: " + tag);
    }

    // Method to handle delete button click
    private void handleDelete(String tag) {
        // Here we would delete the tag
        System.out.println("Delete: " + tag);
    }

    /**
     * This is like a back button
     * @param e the action
     */
    public void exitPage(ActionEvent e){
        System.out.println("closed StatsCtrl");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        EventPageCtrl eventPageCtrl = new EventPageCtrl(server);
        mainCtrl.initialize(stage, eventPageCtrl.getPair(), eventPageCtrl.getTitle());
    }

    public Pair<Controller, Parent> getPair() {
        return FXML.load(Controller.class, "client", "scenes", "StatisticsPage.fxml");
    }
    public String getTitle(){
        return "Stats Page";
    }
}
