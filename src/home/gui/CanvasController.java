package home.gui;

import home.Application;
import home.gui.elements.FloorPlan;
import home.model.Light;
import home.model.Room;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

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
   * The StackPane serving as our subscene-root element.
   */
  @FXML
  public StackPane croot;

  /**
   * Anchor Pane serving as a root of all interactable objects.
   * The ObservableList of interactables the canvas is currently managing is the list of children accessed by interactables.getChildren().
   */
  private AnchorPane interactables;

  /**
   * FloorPlan object to display.
   */
  private FloorPlan view;

  /**
   * Are we in room drawing mode?
   */
  private boolean drawing;

  /**
   * Idk some hack
   */
  private Room current;

  @Override
  public void initialize(URL ignored0, ResourceBundle ignored1)
  {
    this.drawing = false;

    this.sroot.setPannable(true);

    // custom scroll event filter to scale our view instead of scrolling our scroll pane
    this.sroot.addEventFilter(ScrollEvent.ANY, event -> {
      if (this.view != null)
      {
        if (!event.isShiftDown())
        {
          event.consume();

          if (event.getDeltaY() > 0)
          {
            this.view.scale(0.1);
          }
          else
          {
            this.view.scale(-0.1);
          }
        }
      }
    });

    // context menu for the scroll pane
    ContextMenu cmenu = new ContextMenu();
    Menu newItem = new Menu("New...");
    newItem.getStyleClass().add("menuu");

    MenuItem newRoom = new MenuItem("Room");
    newRoom.setOnAction(a -> Application.controller().menu_onNewRoom());

    MenuItem newLight = new MenuItem("Light");
    newLight.setOnAction(a -> Application.controller().menu_onNewLight());

    MenuItem newLabel = new MenuItem("Label");
    newLabel.setOnAction(a -> Application.controller().menu_onNewLabel());

    MenuItem newTemp = new MenuItem("Temperature Display");
    newTemp.setOnAction(a -> Application.controller().menu_onNewTemperature());

    newItem.getItems().addAll(newRoom, newLight, newLabel, newTemp);

    cmenu.getItems().add(newItem);

    this.sroot.setOnContextMenuRequested(e -> {
      if (!this.isDrawing())
      {
        cmenu.show(this.sroot, e.getScreenX(), e.getScreenY());
      }
    });
    this.sroot.setOnMouseClicked(e -> cmenu.hide());

    Application.setCanvas(this);
  }

  /**
   * Adds an interactable GUI Object to the Interactables Anchor Panes' Children.
   * @param interactable item
   */
  public void addInteractable(Node interactable)
  {
    this.interactables.getChildren().add(interactable);
    this.view.draw();
  }

  public void removeInteractable(Node interactable)
  {
    this.interactables.getChildren().remove(interactable);
    this.view.draw();
  }

  public ObservableList<Node> getInteractables()
  {
    return this.interactables.getChildren();
  }

  /**
   * Returns whether we're in 'drawing mode' or not.
   * @return drawing
   */
  public boolean isDrawing()
  {
    return this.drawing;
  }

  /**
   * Returns the polygon that's currently being drawn.
   * @return polygon
   */
  public ArrayList<Integer> getDrawing()
  {
    return this.current.getIndices();
  }

  /**
   * Tells the system to start drawing a polygon.
   * @param name name of the room
   */
  public void startDraw(String name, int id, boolean managed)
  {
    this.drawing = true;
    this.current = new Room(name, id, new ArrayList<>(), managed);

    // redraw once to show grid
    Application.canvas().getView().draw();

    Application.status("Use [SHIFT] + [LMB] to draw a Room. [SHIFT] + [RMB] to undo. Add at least 3 points. Press [ENTER] to confirm. Press [SHIFT] + [ENTER] to cancel.");
  }

  /**
   * Tells the system to end drawing a polygon.
   */
  public void endDraw()
  {
    this.drawing = false;
    this.current = null;
    this.view.draw();
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
    // (re-) initialize
    this.initialize(null, null);

    this.croot.getChildren().clear();

    this.interactables = new AnchorPane();
    this.interactables.setPickOnBounds(false); // make transparent to mouse clicks so we can click the canvas for room draw events

    this.view = new FloorPlan(Application.getModel().getSize(), this.interactables);
    this.view.setFocusTraversable(true);

    this.sroot.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER)
      {
        if (this.drawing)
        {
          if (e.isShiftDown())
          {
            Application.status("Cancelled drawing room.");
            this.endDraw();
            return;
          }

          if (this.current.getIndices().size() < 3)
          {
            Application.status("Cancelled drawing room.");
            DialogManager.info("could not create room", "You need to select at least three points by pressing [SHIFT] + [LMB] on the Viewport.\nThe room has not been created, please try again.");
          }
          else
          {
            Application.debug("Creating room with polygon indices [" + this.current.getIndices().stream().map(Object::toString).collect(Collectors.joining(", ")) + "]");

           Application.getModel().addRoom(this.current);

            // repopulate our controller controller to account for the new room...
            Application.control().populate();
            // repopulate our main controller to account for the new room...
            Application.controller().menu_populateRemoveRoomMenuItem(Application.getModel());
            Application.status("Created \'" + this.current.getName() + "\'.");
          }

          this.endDraw();
        }
      }
    });

    this.view.setOnMouseClicked(e -> {
      if (e.isShiftDown())
      {
        if (this.drawing)
        {
          // handle clicks for drawing a polygon
          if (e.getButton() == MouseButton.PRIMARY)
          {
            // add point
            int index = this.view.approximateNearestClickyPoint(e.getX(), e.getY());
            if (index >= 0)
            {
              this.current.getIndices().add(index);
            }
          }
          else if (e.getButton() == MouseButton.SECONDARY)
          {
            //remove point
            if (this.current.getIndices().size() >= 1)
            {
              this.current.getIndices().remove(this.current.getIndices().size() - 1);
            }
          }

          // redraw to show changes
          this.view.draw();
        }
      }
    });

    // add view and interactable root after initialization
    this.croot.getChildren().add(this.view);
    this.croot.getChildren().add(this.interactables);
  }
}
