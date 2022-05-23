package org.headroyce.lross2024;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

/**
 * @author Lucas Ross
 *
 * The tool for creating 3-point triangles
 */
public class TriTool extends Tool{

    // Model
    private LList<Point> points;
    private Point movePoint;

    // The mode states
    // "draw"   -- currently drawing the shape
    // "modify" -- changing the already drawn shape

    // Mouse status to detect dragging
    private boolean mouseDown, mouseMove;

    // Selected elements of the line
    private int selectedPoint, oldSelectedPoint;

    // The styling of the lines
    private static final int LINE_WIDTH = 2;
    // the interaction circles when selected
    private static final int POINT_RADIUS = 5;

    // View
    private Canvas view;

    /**
     * Get the name of this tool
     * @return the all lowercase name for this tool
     */
    static public String toolName() { return "triangle"; }

    /**
     * Create the graphical element used to activate the tool
     * @return the top-level JavaFX Graphical Node
     */
    static public Node renderTool() {
        Button toolGUI = new Button("Triangle");
        return toolGUI;
    }




    public TriTool(Canvas view){
        setMode("draw");

        this.view = view;
        points = new LList<>();
        select(false);
    }

    /**
     * Sets the selection mode of the shape
     * @param selectMe true to select the shape
     */
    public void select(boolean selectMe){
        super.select(selectMe);

        if( selectMe == false ) {
            this.selectedPoint = -2;
            movePoint = null;
        }
    }

    /**
     * Checks to the see if a point is within the shape
     * @param p the PIXI point to test
     * @return true if p is inside the shape, false otherwise
     */
    @Override
    public boolean contains(Point p){

        // Register contain if within 5px
        final int DIST_WITHIN = 5;

        // Test each line segment for distance
        boolean inShape = false;
        for( int spot = 0; spot < this.points.size()-1; spot++ ){

            // line-Point Distance forumula
            Point fPoint = this.points.get(spot);
            Point lPoint = this.points.get(spot+1);

            double numerator = (lPoint.y-fPoint.y)*p.x - (lPoint.x-fPoint.x)*p.y+lPoint.x*fPoint.y - lPoint.y*fPoint.x;
            numerator = Math.abs(numerator);

            double denominator = (lPoint.y-fPoint.y)*(lPoint.y-fPoint.y) + (lPoint.x-fPoint.x)*(lPoint.x-fPoint.x);
            denominator = Math.sqrt(denominator);

            if( numerator/denominator < DIST_WITHIN ){
                inShape = true;
                break;
            }
        }

        return inShape;
    }

    /**
     * Allow the shape to handle pressing of the mouse
     * @param md the point where the mouse was pressed
     * @return true if the event was handled, false otherwise
     */
    @Override
    public boolean mouseDown(Point md){
        this.mouseDown = true;

        // If the same point is selected twice in a row, then we delete the point
        // so save the old selected point
        oldSelectedPoint = selectedPoint;

        // Index of the selected point, Must start be below -1
        selectedPoint = -2;

        // Check to see if the point is within an interaction circle
        // Using Point-Circle distance formula
        int spot = 0;
        for( Point point : this.points ){
            double X2 = (point.x - md.x) * (point.x-md.x);
            double Y2 = (point.y - md.y) * (point.y-md.y);
            double R2 = POINT_RADIUS*POINT_RADIUS;

            // Point is within the circle!
            if( X2+Y2 < R2 ){
                selectedPoint = spot;
                break;
            }
            spot = spot + 1;
        }

        // Should we remain selected?
        boolean stayActive = true;
        if( this.getMode().equals("draw")){

            // We 'double clicked' the last circle, so finish the shape
            if( selectedPoint == this.points.size() -1 ){
                this.setMode("modify");
                stayActive = false;
            }
            else {
                if (this.points.size() == 2) {
                    //first we add the last point of the triangle
                    this.points.add(md);
                    //then we make a cycle, where the next point is the same point as the first node
                    md = this.points.get(0);
                    //deselect the triangle cause its done
                    this.setMode("modify");
                    stayActive = false;
                }
                this.points.add(md);
            }
        }
        return stayActive;
    }

    /**
     * Handle a mouse movement. Use the select attribute to see if shape is currently selected.
     * @param p the location where the mouse currently is
     * @return true if the event is handled, false otherwise
     */
    @Override
    public boolean mouseMove( Point p ){
        if( this.points.size() == 0 ){ return false; }

        if( getMode().equals("draw")) {
            Point lastPoint = this.points.get(this.points.size()-1);

            // Create a move point so we can draw it during a render
            if (lastPoint != null && getMode().equals("draw")) {
                movePoint = p;
            }
        }
        return true;
    }

    /**
     * Handle a mouse drag.  Use the selected attribute to see if the shape is currently selected.
     * @param p the location of the dragging
     * @return true if the event is handled, false otherwise
     */
    @Override
    public boolean mouseDrag( Point p ){
        this.mouseMove = true;
        if( this.getMode().equals("modify") && this.selectedPoint >= 0) {
            // User is dragging a point, so move it
            Point selectedPoint = this.points.get(this.selectedPoint);
            selectedPoint.x = p.x;
            selectedPoint.y = p.y;
        }
        return true;
    }

    /**
     * Handle a mouse release.  Use the selected attribute to see if the shape is currently selected.
     * @param p the location where the mouse was released
     * @return true if the event was handled, false otherwise
     */
    @Override
    public boolean mouseUp(Point p){
        // Clear mouse statuses
        this.mouseDown = this.mouseMove = false;
        return true;
    }

    /**
     * Render the line
     */
    @Override
    public void render(){
        if( this.points.size() == 0 ){ return; }

        GraphicsContext gc = view.getGraphicsContext2D();

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(TriTool.LINE_WIDTH);

        Point firstPoint = this.points.get(0);
        gc.moveTo(firstPoint.x, firstPoint.y);
        for(int spot = 1; spot < this.points.size(); spot++ ){
            Point point = this.points.get(spot);
            gc.strokeLine(firstPoint.x, firstPoint.y, point.x, point.y);
            firstPoint = point;
        }

        if( this.isSelected()){
            renderWidgets();
        }

    }

    /**
     * Render the selection and interaction GUI
     */
    @Override
    public void renderWidgets(){

        // If we aren't currently selected, then get out
        if( !isSelected() ){
            return;
        }

        GraphicsContext gc = view.getGraphicsContext2D();
        gc.setFill(Color.BLACK);

        // Draw the interaction circles
        int spot = 0;
        for( Point p : this.points ){
            gc.setFill(Color.BLACK); // actual points

            gc.fillOval(p.x-POINT_RADIUS, p.y-POINT_RADIUS, POINT_RADIUS*2, POINT_RADIUS*2);
            spot ++;
        }

        // Draw the move point and a line to it from the last committed point of the line
        if( movePoint != null ){
            Point lastPoint = this.points.get(this.points.size()-1);
            gc.strokeLine(lastPoint.x, lastPoint.y, movePoint.x, movePoint.y);
            gc.fillOval(movePoint.x-POINT_RADIUS, movePoint.y-POINT_RADIUS, POINT_RADIUS*2, POINT_RADIUS*2);
        }

        // If there's a selected point, then we color it
        if( selectedPoint >= 0 ){
            gc.setFill(Color.GREEN);
            Point point = this.points.get(selectedPoint);
            gc.fillOval(point.x-POINT_RADIUS, point.y-POINT_RADIUS, POINT_RADIUS*2, POINT_RADIUS*2);
        }
    }

}
