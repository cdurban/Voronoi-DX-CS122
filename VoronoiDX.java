package VoronoiDeluxe;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * This is a JAVAFX version of the Voronoi Game assignment. It will allow a user to place
 * many seeds (or Points) onto a 300x550 node. These points will "control" the area around
 * them by setting the area (1x1 rectangles closest to it) around it to be the same color.
 * Every cell will be a randomized color. There can be as many seeds as desired by the
 * user. There will be controls to change the game. It can be based off of Euclidean,
 * Manhattan or Chessboard distance all of which will change the visual state of the game.
 * The user will also be able to change the color of every seed to be a random color.
 *
 * This was worked on mainly by Tyler Reed
 *
 * @author Chase Urban
 * @author Tyler Reed
 */
public class VoronoiDX extends Application {

    private final int rows = 400; //variables for rows and columns
    private final int cols = 700;

    private Voronoi voronoi = new Voronoi();
    //Initialize the game

    private boolean showPoints = true;
    //True if the user chooses the "see seeds" or false if they choose not to.

    private Rectangle[][] diagram = new Rectangle[rows][cols];
    //array of rectangles to be used in the diagram

    private int selectionMode = 0;
    //Selection mode, 0 for Euclidean distance, 1 for Manhattan, 2 for Chessboard.

    private ArrayList<int[]> points = new ArrayList<>();
    //an arrayList containing the points clicked.

    private GridPane diagramBox = new GridPane();//box the diagram is contained within
    //The gridPane that will contain the diagram.


    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        //GridPane allows for the most organization in an application with ROWs and COlS
        Scene scene = new Scene(root);

        class DiagramClickHandler implements EventHandler<MouseEvent>{

            private int r,c;//row and column of diagram cell to be passed to voronoi

            private DiagramClickHandler(int r, int c){this.r = r; this.c = c;}

            @Override
            public void handle(MouseEvent event) {
                voronoi.addSeed(r,c); // Adds clicked cell to voronoi
                points.add(new int[]{r,c});// Adds the point clicked to the arraylist
                                           // of point coordinates
                updateColors(); //Updates colors based off of new point
            }
        }

        for(int r = 0; r < rows; r++){       // initializes the array of rectangles, and
            for(int c = 0; c < cols; c++){   // therefore the diagram to 1x1 MINTCREAM
                                             // rectangles, and adds the click handler
                diagram[r][c] = new Rectangle(1,1, Color.MINTCREAM);
                diagram[r][c].setOnMouseClicked(new DiagramClickHandler(r,c));
                diagramBox.add(diagram[r][c],c,r);

            }
        }
        diagramBox.setStyle("-fx-border-width:5;-fx-border-color:BLACK;");

        //Horizontal Box for the IO. Will hold the buttons, the checkbox, etc.
        HBox optionBox = new HBox(35);

        //Button that when used will randomize the colors of every cell displayed
        Button colorButton = new Button("Randomize Colors");
        colorButton.setOnAction(event -> {
            if (voronoi.getE_GRID() != null) {
                voronoi.randomizeColors();
                updateColors();
            }
        });

        //Button that will reset the game back to its original state: cleared.
        Button clearButton = new Button("Clear All");
        clearButton.setOnAction(event -> {
            voronoi = new Voronoi();
            reset();
        });

        // Checkbox that when selected (selected by default) will show where each seed is
        // placed. When it is unselected it will hide the dots showcasing the placement of
        // a seed.
        CheckBox showSeedsCheckBox = new CheckBox("Show Seeds");
        showSeedsCheckBox.setSelected(true);
        showSeedsCheckBox.setOnAction(event -> {
            showPoints = showSeedsCheckBox.isSelected();
            updateColors();
        });

        //List of selectable values, it will change the distance calculation method to the
        // chosen version.
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Euclidean Distance","Manhattan Distance",
                        "Chessboard Distance"
                );
        ChoiceBox distanceBox = new ChoiceBox(options);
        distanceBox.getSelectionModel().selectFirst();
        distanceBox.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectionMode = newValue.intValue();//sets the distance selection mode
                    updateColors();
                }
        );

        //Adds everything to the HBox for the IO and then spaces them apart.
        optionBox.getChildren().addAll(
                colorButton,clearButton,showSeedsCheckBox,distanceBox
        );
        optionBox.setPadding(new Insets(20));
        optionBox.setAlignment(Pos.CENTER);

        root.add(diagramBox,1,1); //add diagramBox and optionBox to the
        root.add(optionBox,1,2);
        root.setPadding(new Insets(5));

        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("Voronoi Deluxe");
        primaryStage.show();
    }

    /**
     * Updates the colors of all of the pixels by:
     *  - Determining the selection mode, and using the grid of that type from Voronoi to:
     *        - Find and set the color associated with the point that controls each point.
     * Also draws points as 5x5 squares if show points box is ticked.
     */
    private void updateColors(){
        int[][] grid;
        if(selectionMode == 0)grid = voronoi.getE_GRID();
        else if (selectionMode == 1)grid = voronoi.getM_GRID();
        else grid = voronoi.getC_GRID();
        ArrayList<Point> seeds = voronoi.getSEEDS();
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                if (grid != null) {
                    diagram[r][c].setFill(seeds.get(grid[r][c]).getColor());
                }
            }
        }
        if (showPoints){ // draws a 5x5 black square at each clicked point, only if
                         // showPoints is true.
            for (int[] point: points){
                int x = point[0] -2;
                int y = point[1] -2;
                for (int i = 0; i < 5; i++){
                    for (int j = 0; j < 5; j++){
                        if (!((x+i)>rows) && !((y+j)>cols)){
                            diagram[x+i][y+j].setFill(Color.BLACK);
                        }
                    }
                }
            }
        }
    }

    /**
     * Resets the voronoi variable and the diagram field.
     */
    private void reset(){
        voronoi = new Voronoi();
        points = new ArrayList<>();
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                diagram[r][c].setFill(Color.MINTCREAM);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}