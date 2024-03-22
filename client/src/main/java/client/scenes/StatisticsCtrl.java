package client.scenes;

import client.utils.ServerUtils;
import commons.Expense;
import commons.Tag;
import commons.TagId;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    private Button cancelEditButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ColorPicker colorPicker;

    @FXML
    private TextField editNameField;

    @FXML
    private Pane editPanel;
    @FXML
    private Button saveButton;
    @FXML
    private Text editOrAreYouSureText;
    @FXML
    private Text tagsText;

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
    private Map<String, String> tagColors;
    private Map<String, String> namesForLegend;
    private String selectedTagForEditing;//it also helps at deleting (from tag list)

    @Inject
    public StatisticsCtrl(ServerUtils server) {
        this.server = server;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        selectedTagForEditing=null;
        long eventId= server.getCurrentId();
        if(!server.checkIfTagExists("other", eventId))
            server.addTag(new Tag(new TagId("other",eventId),"#e0e0e0"));

        if(!server.checkIfTagExists("food", eventId))
            server.addTag(new Tag(new TagId("food",eventId),"#00ff00"));

        if(!server.checkIfTagExists("entrance fees", eventId))
            server.addTag(new Tag(new TagId("entrance fees",eventId),"#0000ff"));

        if(!server.checkIfTagExists("travel", eventId))
            server.addTag(new Tag(new TagId("travel",eventId),"#ff0000"));
        refresh();
    }

    /**
     * This function refreshes everything on this page.
     */
    public void refresh()
    {
        totalAmount=0;
        //get all Expenses
        long eventId=server.getCurrentId();
        expenses=server.getAllExpensesOfEvent(eventId);
        //get tags with values (each tag with the amount of money it contains.)
        Map<String, Double> tagsWithValues=getTagsWithValuesFromExpenses(expenses);
        //create the pieChart
        createPieChart(tagsWithValues);
        updateTextsOnTheScreen();
        //create the list of tags used in this event
        createTagsUsedInThisEventList(server.getAllTagsFromEvent(server.getCurrentId()));
        //to be sure it is not opened
        closeEditPane();
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
        long eventId=server.getCurrentId();
        //a map with the name (including percentages) and the color of a tag
        tagColors = new HashMap<>();
        //it maps a name (ex "food 23%") to the name for the legend (ex: "food 200 EUR")
        namesForLegend = new HashMap<>();
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
        legendListView.setCellFactory(null);
        legendListView.refresh();
        legendListView.setCellFactory(param -> new LegendListCell());

        // put data in the legend
        legendListView.getItems().clear();
        legendListView.getItems().addAll(tagColors.keySet());
    }
    private void createTagsUsedInThisEventList(List<Tag> tags)
    {
        tagsListView.getItems().clear();
        tagsListView.getItems().addAll(tags.stream().map(x->x.getId().getName()).sorted().toList());
        tagsListView.setCellFactory(param -> new TagsListViewCell());
        //we need these for making the front end beautiful
        tagsListView.setSelectionModel(null);
        tagsListView.setFocusTraversable(false);

        selectedTagForEditing=null;
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

        public LegendListCell() {

        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                Circle circle = new Circle(6);
                String color=tagColors.get(item)+"ff";
                circle.setFill(Paint.valueOf(color));
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
            //create the edit button
            editButton=new Button();
            try {
                //load the icon
                Image editIcon=new Image(getClass().getResourceAsStream("/pencil.png"));
                ImageView imageView=new ImageView(editIcon);
                imageView.setFitWidth(12);
                imageView.setFitHeight(12);
                editButton.setGraphic(imageView);
            }
            catch (Exception e) {}

            //create the delete button
            deleteButton=new Button();
            deleteButton.setShape(new javafx.scene.shape.Circle(6));
            try {
                //load the icon
                Image deleteIcon=new Image(getClass().getResourceAsStream("/bin.png"));
                ImageView imageView=new ImageView(deleteIcon);
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                deleteButton.setGraphic(imageView);
            }
            catch (Exception e) {}
            //edit the size of buttons/icons
            editButton.setPrefSize(11, 12);
            deleteButton.setPrefSize(11, 12);

            editButton.setOnAction(event -> handleEdit(getItem()));
            deleteButton.setOnAction(event -> handleDelete(getItem()));

            //Add components to the element (the line from the list view)
            container.getChildren().addAll(deleteButton,editButton,tagLabel);
            container.setHgrow(tagLabel, Priority.ALWAYS);

            container.setSpacing(4);

            // Set padding between lines
            //container.setPadding(new javafx.geometry.Insets(1));
        }
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                tagLabel.setText(item);
                if(item.equals("other")) {
                    try {

                        container.getChildren().get(0).setStyle("-fx-opacity: 0.5;");
                    }catch (Exception e){}
                }
                setGraphic(container);
            }
        }
    }
    private void handleEdit(String tag) {
        // Here we would edit the tag

        selectedTagForEditing=tag;
        System.out.println("Edit: " + tag);
        showEditOrDeletePane(tag,true);
    }

    // Method to handle delete button click
    private void handleDelete(String tag) {
        // Here we would delete the tag
        System.out.println("Delete: "+tag);
        if(!tag.equals("other"))
        {
            //open are you sure menu
            selectedTagForEditing=tag;
            //false is for are you sure menu
            showEditOrDeletePane(tag,false);
        }
        System.out.println("You cannot delete this tag.");
    }
    @FXML
    void saveEditTag(ActionEvent event) {
        String newName=editNameField.getText();
        String newColor=colorPicker.getValue().toString();
        newColor="#"+newColor.substring(2,8);
        //if we just change the color:
        if(newName.equals(selectedTagForEditing))
        {
            server.updateTag(selectedTagForEditing,server.getCurrentId(),
                    new Tag(new TagId(newName, server.getCurrentId()),newColor));

            refresh();
            return;
        }
        if(selectedTagForEditing.equals("other"))
        {
            //Problem!! We cannot change this tag's name.
            editNameField.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 5px;");
            return;
        }
        //verify if there is no other tag with this name and eventId
        if(!server.checkIfTagExists(newName,server.getCurrentId()))
        {
            //we can save it
            //also when we update a tag, all expenses that contains that tag will be changed
            server.updateTag(selectedTagForEditing,server.getCurrentId(),
                    new Tag(new TagId(newName, server.getCurrentId()),newColor));
            //reload the page
            refresh();
            return;
        }

        editNameField.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 5px;");
        System.out.println("There is already a tag with this name.");
    }
    @FXML
    void deleteTagButton(ActionEvent event) {
        if(server.deleteTagFromEvent(selectedTagForEditing, server.getCurrentId()))
            refresh();
    }
    void showEditOrDeletePane(String tagName,boolean editElseDelete)
    {
        editPanel.setVisible(true);
        //if we edit
        if(editElseDelete==true) {
            editOrAreYouSureText.setText("Edit tag "+selectedTagForEditing);
            editNameField.setVisible(true);
            colorPicker.setVisible(true);
            saveButton.setVisible(true);
            deleteButton.setVisible(false);
        }
        else //if we delete (are you sure?)
        {
            editOrAreYouSureText.setText("Are you sure?");
            editNameField.setVisible(false);
            colorPicker.setVisible(false);
            saveButton.setVisible(false);
            deleteButton.setVisible(true);
        }
        tagsText.setVisible(false);
        tagsListView.setVisible(false);
        Tag tag=server.getTagByIdOfEvent(tagName,server.getCurrentId());
        editNameField.setText(tagName);
        if(tag!=null)
            colorPicker.setValue(Color.web(tag.getColor()));
        else
            colorPicker.setValue(Color.WHITE);
    }
    @FXML
    void closeEditPane()
    {
        editPanel.setVisible(false);
        tagsText.setVisible(true);
        tagsListView.setVisible(true);
        //reset the fields
        editNameField.setText("");
        colorPicker.setValue(Color.WHITE);
        selectedTagForEditing=null;
        editNameField.setStyle("-fx-border-radius: 5px;");
    }
    /**
     * This is like a back button
     * @param e the action
     */
    public void exitPage(ActionEvent e){
        System.out.println("closed StatsCtrl");
        closeEditPane();
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