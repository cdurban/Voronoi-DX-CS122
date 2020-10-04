package VoronoiDeluxe;

import java.util.ArrayList;

/**
 * The backend of the voronoi diagram. Cells are controlled if they are closest to a
 * user-placed seed, which can be determined by one of three distances in this case.
 *
 * The backend was, for the most part, worked on by Chase Urban.
 *
 * @author Chase Urban
 * @author Tyler Reed
 */
public class Voronoi {

    //Initialize 2D arrays for each of the three distance types.
    private int[][] E_GRID; // E_GRID, M_GRID, and C_GRID contain values that point to the
    private int[][] M_GRID; // index of the seed they are "controlled by".
    private int[][] C_GRID;

    private final int ROWS = 400; //Height of the Rectangle
    private final int COLS = 700; //Width of the Rectangle
    private final ArrayList<Point> SEEDS = new ArrayList<>();
    //ArrayList of created Points, aka our seeds.

    private ArrayList<double[][]> EUCLID_DISTANCES = new ArrayList<>();
    private ArrayList<int[][]> MANHAT_DISTANCES = new ArrayList<>();
    private ArrayList<int[][]> CHESSB_DISTANCES = new ArrayList<>();
    //ArrayLists to store the 2D arrays for each distances

    /**
     * Default Constructor: set the starting position of the grid, no matter the distance,
     * to be empty.
     */
    Voronoi(){
        E_GRID = new int[ROWS][COLS];
        for(int i = 0; i < ROWS; i++){
            for(int j = 0;j<COLS;j++){
                E_GRID[i][j] = 0;
            }
        }
        M_GRID = new int[ROWS][COLS];
        for(int i = 0; i < ROWS; i++){
            for(int j = 0;j<COLS;j++){
                M_GRID[i][j] = 0;
            }
        }
        C_GRID = new int[ROWS][COLS];
        for(int i = 0; i < ROWS; i++){
            for(int j = 0;j<COLS;j++){
                C_GRID[i][j] = 0;
            }
        }
    }

    public int[][] getE_GRID() {
        return E_GRID;
    }

    public int[][] getM_GRID() {
        return M_GRID;
    }

    public int[][] getC_GRID() {
        return C_GRID;
    }

    public ArrayList<Point> getSEEDS() {
        return SEEDS;
    }

    /**
     * Method to get the Manhattan Distance for each cell and point.
     * Manhattan distance is the sum of the differences of the rows and columns.
     */
    private int getManhattanDistance(int row1, int col1, int row2, int col2){
        int rowDistance = Math.abs(row2-row1);
        int colDistance = Math.abs(col2-col1);
        return rowDistance + colDistance;
    }

    /**
     * Method to get the Euclidean Distance for each cell and point.
     * Euclidean distance is a mathematical distance based off of the pythagorean theorem.
     */
    private double getEuclideanDistance(int row1, int col1, int row2, int col2){
        double changeInCOLS = Math.pow(col2-col1,2);
        double changeInROWS = Math.pow(row2-row1,2);
        return Math.sqrt(changeInCOLS + changeInROWS);
    }

    /**
     * Method to get the Chessboard Distance for each cell and point.
     * Chessboard distance equal to the higher of the distances of rows and columns.
     */
    private int getChessboardDistance(int row1, int col1, int row2, int col2){
        int rowDistance = Math.abs(row2-row1);
        int colDistance = Math.abs(col2-col1);
        if(rowDistance > colDistance){
            return rowDistance;
        }
        else return colDistance;
    }

    /**
     * Adds a point to the arrayList of SEEDS
     */
    public void addSeed(int row, int col){
        for (Point s:SEEDS){
            if(s.getRow() == row){
                if(s.getCol() == col){
                    break;
                }
            }
        }
        SEEDS.add(new Point(row,col)); //Add the coords of the Point to the array list.
        updateDistances();
        updateGrids();
    }

    /**
     * Goes through the arrayList of seeds, and randomizes the color of each
     */
    public void randomizeColors(){
        for(int i = 0; i < SEEDS.size(); i++){
            Point n = SEEDS.get(i);
            n.randomizeColor();
            SEEDS.set(i,n);
        }
    }

    /**
     * Updates the ArrayLists of distances of (and therefore from) each point
     */
    public void updateDistances(){
        EUCLID_DISTANCES = new ArrayList<>();
        for (Point n : SEEDS) {
            double[][] dis = new double[ROWS][COLS];
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    //Set the array of distances equal to the values of the distances
                    // based on Euclidean Distance.
                    dis[r][c] = getEuclideanDistance(n.getRow(), n.getCol(), r, c);
                }
            }
            EUCLID_DISTANCES.add(dis); //Add the distances to the ArrayList of distances.
        }

        MANHAT_DISTANCES = new ArrayList<>();
        for (Point n : SEEDS) {
            int[][] dis = new int[ROWS][COLS];
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    //Set the array of distances equal to the values of the distances
                    // based on Manhattan Distance.
                    dis[r][c] = getManhattanDistance(n.getRow(), n.getCol(), r, c);
                }
            }
            MANHAT_DISTANCES.add(dis); //Add the distances to the ArrayList of distances.
        }

        CHESSB_DISTANCES = new ArrayList<>();
        for (Point n : SEEDS) {
            int[][] dis = new int[ROWS][COLS];
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    //Set the array of distances equal to the values of the distances
                    // based on Chessboard Distance.
                    dis[r][c] = getChessboardDistance(n.getRow(), n.getCol(), r, c);
                }
            }
            CHESSB_DISTANCES.add(dis); //Add the distances to the ArrayList of distances.
        }
    }

    /**
     * This method will update the grids to set the cells to be under the control of the
     * closest seed changed by the distance method chosen by the user.
     */
    private void updateGrids(){
        //Grids of the lowest seed at each cell on the main grid, one for each type of
        // distance.
        int[][] E_LOWEST = new int[ROWS][COLS];
        int[][] M_LOWEST = new int[ROWS][COLS];
        int[][] C_LOWEST = new int[ROWS][COLS];

        for(int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                double lowE = ROWS * COLS;  // Sets the lowest value to be insanely high,
                int lowEInd = 0; //index    // so the first value will always be lower.
                int lowM = ROWS * COLS;     // Repeats for all distances.
                int lowMInd = 0; //index
                int lowC = ROWS * COLS;
                int lowCInd = 0; //index

                for (int x = 0; x < EUCLID_DISTANCES.size(); x++) {
                    double[][] d = EUCLID_DISTANCES.get(x);
                    if (d[r][c] < lowE) {
                        lowE = d[r][c];
                        //Sets the lowest value if the value at [r][c] in this distance
                        // array is lowest.
                        lowEInd = x; //Sets the index of the lowest value
                    }
                }
                E_LOWEST[r][c] = lowEInd;

                for (int x = 0; x < MANHAT_DISTANCES.size(); x++) {
                    int[][] d = MANHAT_DISTANCES.get(x);
                    if (d[r][c] < lowM) {
                        lowM = d[r][c];
                        //Sets the lowest value if the value at [r][c] in this distance
                        // array is lowest.
                        lowMInd = x; //Sets the index of the lowest value
                    }
                }
                M_LOWEST[r][c] = lowMInd;

                for (int x = 0; x < CHESSB_DISTANCES.size(); x++) {
                    int[][] d = CHESSB_DISTANCES.get(x);
                    if (d[r][c] < lowC) {
                        lowC = d[r][c];
                        //Sets the lowest value if the value at [r][c] in this distance
                        // array is lowest.
                        lowCInd = x; //Sets the index of the lowest value
                    }
                }
                C_LOWEST[r][c] = lowCInd;
            }
        }
        //Set each grid equal to the updated grid based on its distance calculation
        // method.
        E_GRID = E_LOWEST;
        M_GRID = M_LOWEST;
        C_GRID = C_LOWEST;
    }
}