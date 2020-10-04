package VoronoiDeluxe;

import javafx.scene.paint.Color;

/**
 * A separate class for the Points that will be placed in the Voronoi Diagram. Every point
 * will have a ROW value, a COL value and a Color.
 *
 * This was created by Chase Urban for use in the voronoi backend component.
 *
 * @author Chase Urban
 * @author Tyler Reed
 */
public class Point {

    private final int row;
    private final int col;
    private Color color;

    /**
     * Default constructor for a single point.
     * @param r row
     * @param c col
     */
    Point(int r, int c){
        row = r;
        col = c;
        color = randomColor();
    }

    //Used to randomize the color when the point is placed onto the scene.
    public void randomizeColor(){
        color = randomColor();
    }

    //Used to get the ROW coordinate for the SEEDS array list.
    public int getRow() {
        return row;
    }

    //Used to get the COL coordinate for the SEEDS array list.
    public int getCol() {
        return col;
    }

    //Used to get the Color determined by the randomizeColor method. Will allow the
    // application to apply it that color.
    public Color getColor() {
        return color;
    }

    //Will create a random color based on RGB values. It is called by the randomizeColor()
    // method.
    public Color randomColor(){
        double r = Math.random();
        double g = Math.random();
        double b = Math.random();
        return new Color(r,g,b,1);
    }
}