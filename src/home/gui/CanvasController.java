package home.gui;

import home.Application;
import home.gui.elements.FloorPlan;
import home.model.Light;
import home.model.Room;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import org.json.simple.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This class is used to control the functionality of the canvas subscene.
 * The CanvasController also manages the view component of the MVC-Model
 *
 * @see MainController
 *
 * @author Robin Buhlmann
 * @version 0.1
 * @since 2019-11-20
 */
public class CanvasController implements Initializable
{
  /**
   * The ScrollPlane serving as our subscene-root element.
   */
  @FXML
  public ScrollPane sroot;

  /**
   * The ScrollPlane serving as our subscene-root element.
   */
  @FXML
  public StackPane croot;

  /**
   * FloorPlan object to display on the canvas.
   */
  private FloorPlan view;

  /**
   * I ran out of nice variable names give me a break.
   */
  private boolean drawingPolygon;

  /**
   * I ran out of nice variable names give me a break.
   */
  private boolean drawingLight;
  private Light currentLight;

  /**
   * The polygon being drawn currently. Is null if we're not drawingPolygon.
   */
  private ArrayList<Integer> drawnPolygon;

  /**
   * Idk some hack
   */
  private String currentName;

  /**
   * Idk some hack
   */
  private boolean currentManaged;

  /**
   * Idk some hack
   */
  private int currentID;

  @Override
  public void initialize(URL ignored0, ResourceBundle ignored1)
  {
    this.drawingPolygon = false;
    this.drawingLight = false;

    this.sroot.setPannable(true);

    // custom scroll event filter to scale our view instead of scrolling our scroll pane
    this.sroot.addEventFilter(ScrollEvent.ANY, event -> {
      if (this.view != null)
      {
        if (event.getDeltaY() > 0)
        {
          this.view.scale(0.1);
        }
        else
        {
          this.view.scale(-0.1);
        }
      }

      // prevent event propagation
      event.consume();
    });

    Application.setCanvas(this);
  }

  public void drawLight(int id, int roomID)
  {
    this.drawingLight = true;
    this.currentLight = new Light(id, 0, 0);
    this.currentID = roomID;

    this.view.draw();
  }

  /**
   * Returns whether we're in 'drawingPolygon mode' or not.
   * @return drawingPolygon
   */
  public boolean isDrawing()
  {
    return this.drawingPolygon || this.drawingLight;
  }

  /**
   * Returns the polygon that's currently being drawn.
   * @return polygon
   */
  public ArrayList<Integer> getDrawingPolygon()
  {
    return this.drawnPolygon;
  }

  public Light getDrawingLight()
  {
    return this.currentLight;
  }

  /**
   * Tells the system to start drawing a polygon.
   * @param name name of the room
   */
  public void startDraw(String name, int id, boolean managed)
  {
    this.drawingPolygon = true;
    this.drawnPolygon = new ArrayList<>();
    this.currentName = name;
    this.currentID = id;
    this.currentManaged = managed;

    // redraw once to show grid
    Application.canvas().getView().draw();

    Application.status("Use [SHIFT] + [LMB] to draw a Room. [SHIFT] + [RMB] to undo. Add at least 3 points. Press [ENTER] to confirm. Press [SHIFT] + [ENTER] to cancel.");
  }

  /**
   * Tells the system to end drawing a polygon.
   */
  public void endDraw()
  {
    this.drawingPolygon = false;
    this.drawnPolygon = null;
  }

  /**
   * Returns the current canvas object used for rendering.
   * @return canvas
   */
  public FloorPlan getView()
  {
    return this.view;
  }

  /**
   * Constructs a FloorPlan and appends the canvas to the scroll pane.
   */
  public void populate()
  {
    croot.getChildren().clear();

    this.view = new FloorPlan(Application.getModel().getSize());
    this.view.setFocusTraversable(true);

    this.sroot.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER)
      {
        if (this.drawingPolygon)
        {
          if (e.isShiftDown())
          {
            Application.status("Cancelled drawing room.");
            this.endDraw();
            this.view.draw();
            return;
          }

          if (this.drawnPolygon.size() < 3)
          {
            Application.status("Cancelled drawing room.");
            // tell the user he f'd up
            DialogManager.info("could not create room", "You need to select at least three points by pressing [SHIFT] + [LMB] on the Viewport.\nThe room has not been created, please try again.");
            this.endDraw();
            this.view.draw();
            return;
          }

          Application.getModel().addRoom(new Room(this.currentName, this.currentID, this.drawnPolygon, currentManaged));

          Application.debug("Creating room with polygon indices [" + this.drawnPolygon.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]");

          // end drawingPolygon procedure
          this.endDraw();

          // redraw view after our room ahs been added
          this.view.draw();

          // repopulate our controller controller to account for the new room...
          Application.control().populate();

          // repopulate our main controller to account for the new room...
          Application.controller().menu_populateRemoveRoomMenuItem(Application.getModel());

          // make a nice status :)
          Application.status("Created \'" + this.currentName + "\'.");
        }
        else if (this.drawingLight)
        {
          try
          {
            // add the light to the room
            Application.getModel().getRoom(this.currentID).addLight(this.currentLight);

            // repopulate our controller controller to account for the new light...
            Application.control().populate();

            Application.status("Created a new light.");
          }
          catch(Exception ex)
          {
            Application.debug(ex.getMessage());
            Application.status("Could not create light :(");
          }

          this.drawingLight = false;
          this.currentLight = null;
          this.view.draw();
        }
      }
    });

    // handle clicks for polygon drawingPolygon
    this.view.setOnMouseClicked(e -> {
      if (e.isShiftDown())
      {
        if (this.drawingPolygon)
        {
          try
          {
            if (e.getButton() == MouseButton.PRIMARY)
            {
              // add point
              this.drawnPolygon.add(this.view.approximateNearestClickyPoint(e.getX(), e.getY()));
            }
            else if (e.getButton() == MouseButton.SECONDARY)
            {
              //remove point
              this.drawnPolygon.remove(this.drawnPolygon.size() - 1);
            }

            this.view.draw();
          }
          catch (Exception ex)
          {
            Application.debug(ex.getMessage());
          }
        }
        else if (this.drawingLight)
        {
          double[] pos = this.view.translateCoordinates(e.getX(), e.getY());

          this.currentLight.setPosition(pos[0], pos[1]);
          this.view.draw();
        }
      }
    });

    croot.getChildren().add(this.view);
  }
}
