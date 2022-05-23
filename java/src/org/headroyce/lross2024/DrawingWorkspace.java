package org.headroyce.lross2024;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Key;
import java.util.ArrayList;

/**
 * The main drawing canvas for our application
 *
 * @author Brian Sea
 */
public class DrawingWorkspace extends StackPane {

    // The main drawing canvas
    private Canvas mainCanvas;

    // The active tool's constructor so we can make new objects
    private Constructor<? extends Tool> activeTool;

    // All the shapes in the world
    private ArrayList<Tool> worldShapes;

    // All the selected shapes in the world
    private ArrayList<Tool> selectedShapes;

    public DrawingWorkspace(){
        worldShapes = new ArrayList<>();
        selectedShapes = new ArrayList<>();


        mainCanvas = new Canvas();

        // Force the canvas to resize to the screen's size
        mainCanvas.widthProperty().bind(this.widthProperty());
        mainCanvas.heightProperty().bind(this.heightProperty());

        // Attach mouse handlers to the canvas
        EventHandler<MouseEvent> handler = new MouseHandler();
        mainCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, handler);
        mainCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, handler);
        mainCanvas.addEventHandler(MouseEvent.MOUSE_MOVED, handler);
        mainCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, handler);


        this.getChildren().add(mainCanvas);
    }

    /**
     * Switch to and active a new tool
     * @param t the constructor of the tool
     * @return true if the is switched, false otherwise
     */
    public boolean setActiveTool(Constructor<? extends Tool> t){
        boolean rtn = false;
        if( t != activeTool){
            activeTool = t;
            rtn = true;

            // Clear the selections and render the world
            for( Tool tool : selectedShapes){
                tool.select(false);
            }
            selectedShapes.clear();
            renderWorld();
        }
        return rtn;
    }

    Constructor<? extends Tool> getActiveTool() {
        return activeTool;
    }

    /**
     * Render the viewable canvas
     */
    public void renderWorld(){
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        gc.clearRect(0,0, mainCanvas.getWidth(), mainCanvas.getHeight());

        for(Tool tool : worldShapes){
            tool.render();

        }
    }

    /**
     * Helps to handle all of the mouse events on the canvas
     */
    private class MouseHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event){
            Point p = new Point(event.getX(), event.getY());

            if(event.getEventType().equals(MouseEvent.MOUSE_PRESSED)){
                if( activeTool != null ) {

                    // Nothing is selected, so create and select a new shape
                    if (selectedShapes.isEmpty()) {
                        try {

                            // Creates a new instance of the tool
                            Tool tool = activeTool.newInstance(mainCanvas);
                            worldShapes.add(tool);
                            selectedShapes.add(tool);

                            tool.select(true);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {

                    // See if we're in a selected shape
                    boolean inSelectShape = false;
                    for( Tool tool : selectedShapes ){
                        if( tool.contains(p)){
                            inSelectShape = true;
                            break;
                        }
                    }

                    // Not in a selected shape, so deselect everything
                    if( !inSelectShape ) {
                        // Deselect everything
                        for (Tool tool : selectedShapes) {
                            tool.select(false);
                        }
                        selectedShapes.clear();

                        // See if the mouse selects one of the shapes in the world
                        for (Tool tool : worldShapes) {
                            if (tool.contains(p)) {
                                selectedShapes.add(tool);
                                tool.select(true);
                                break;
                            }
                        }
                    }
                }

                // Send the mouse event to all selected shapes
                boolean eventHandled = false;
                for( Tool tool : selectedShapes){
                    eventHandled = tool.mouseDown(p) || eventHandled;
                }

                // If no selected shape handles the event, then
                // that means it's outside all shapes, so deselect everything
                if( !eventHandled ){
                    for( Tool shape : selectedShapes ){
                        shape.select(false);
                    }
                    selectedShapes.clear();
                }

            }
            else if( event.getEventType().equals(MouseEvent.MOUSE_RELEASED)){
                for( Tool tool : selectedShapes ){
                    tool.mouseUp(p);
                }
            }
            else if( event.getEventType().equals(MouseEvent.MOUSE_MOVED)){
                for( Tool tool : selectedShapes ){
                    tool.mouseMove(p);
                }
            }
            else if( event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)){
                for( Tool tool : selectedShapes ){
                    tool.mouseDrag(p);
                }
            }

            renderWorld();

        }
    }

    /**
     * runs when backspace is pressed, deletes a line if one is selected
     * @return true if shape was deleted, false otherwise
     */
    public boolean deleteTool(){
        if (!this.selectedShapes.isEmpty()){
            //loops through selectedShapes to see if worldShapes contains it
            for (Tool tool : selectedShapes){
                if (this.worldShapes.contains(tool)){
                    worldShapes.remove(tool);
                }
            }
            renderWorld();
            return true;
        }
        return false;
    }

    /**
     * clears all of the shapes
     * @return true if clear was successful, false otherwise
     */
    public boolean clearWorkspace(){
        if (worldShapes.size() == 0){
            return false;
        }
        //kill all the shapes in the world
        worldShapes.clear();
        renderWorld();
        return true;
    }
}
