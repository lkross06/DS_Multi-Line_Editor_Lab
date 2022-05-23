package org.headroyce.lross2024;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The entire workspace of the application.
 *
 * @author Brian Sea
 */
public class MainWorkspace extends BorderPane {

    private DrawingWorkspace drawingArea;
    private VBox toolPalette;

    // All the registered tools
    // (Tool_Name -> Constructor)
    private HashMap<String, Constructor<? extends Tool>> registeredTools;

    //clear button
    private Button clearButton;

    public MainWorkspace(){

        ArrayList<Class<? extends Tool>> tools = new ArrayList<>();
        tools.add(LineTool.class);
        tools.add(TriTool.class);

        registeredTools = new HashMap<>();
        drawingArea = new DrawingWorkspace();
        toolPalette = new VBox(10);

        clearButton = new Button("CLEAR");
        //everytime clear button is pressed, call clearWorkspace() in the drawingWorkspace
        clearButton.setOnAction(e -> {
            drawingArea.clearWorkspace();
        });

        // Register all the currently supported tools
        for( Class<? extends Tool> tool : tools ) {
            try {

                // Grab the tool's name
                Method method = tool.getMethod("toolName");
                String toolname = (String) method.invoke(null);
                Constructor<? extends Tool> con = tool.getConstructor(Canvas.class);
                registeredTools.put(toolname, con);

                // Grab the GUI for the tool's selection area and attach it to the main GUI
                method = tool.getMethod("renderTool");
                Node toolGUI = (Node) method.invoke(null);

                // Handle switching to a tool and changing the status to indicate which tools we're on
                toolGUI.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (drawingArea.getActiveTool() == con) {
                            drawingArea.setActiveTool(null);
                            toolGUI.getStyleClass().remove("active");
                        } else {
                            drawingArea.setActiveTool(con);
                            toolGUI.getStyleClass().add("active");
                        }
                    }
                });
                toolGUI.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode().equals(KeyCode.BACK_SPACE)) {
                        drawingArea.deleteTool();
                    }
                });

                toolPalette.getChildren().add(toolGUI);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            toolPalette.setAlignment(Pos.CENTER);

            this.setLeft(toolPalette);
            this.setCenter(drawingArea);
            this.setRight(clearButton);
        }

    }
}
