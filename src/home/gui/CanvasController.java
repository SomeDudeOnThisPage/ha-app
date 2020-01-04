package home.gui;

import home.Application;
import home.model.Room;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.json.simple.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
  public ScrollPane sroot;

  /**
   * FloorPlan object to display on the canvas.
   */
  private FloorPlan view;

  /**
   * I ran out of nice variable names give me a break.
   */
  private boolean drawing;

  private ArrayList<Integer> drawnPolygon;
  private String currentName;

  @Override
  public void initialize(URL ignored0, ResourceBundle ignored1)
  {
    this.drawing = false;

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

  public void startDraw(String name)
  {
    this.drawing = true;
    this.drawnPolygon = new ArrayList<>();
    this.currentName = name;

    this.view.setDrawablePolygon(this.drawnPolygon);

    Application.status("Use [SHIFT] + [LMB] to draw a Room. [SHIFT] + [RMB] to undo. Add at least 3 points. Press [ENTER] to confirm. Press [SHIFT] + [ENTER] to cancel.");
  }

  public void endDraw()
  {
    this.drawing = false;
    this.drawnPolygon = null;

    if (this.view != null)
    {
      this.view.setDrawablePolygon(null);
    }
  }

  /**
   * Constructs a FloorPlan and appends the canvas to the scroll pane.
   * @param path path to the map folder
   * @param data contents of map.json
   */
  public void setView(String path, JSONObject data)
  {
    this.view = new FloorPlan(path, data);
    this.view.setFocusTraversable(true);

    this.sroot.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER && this.drawing)
      {
        // todo: actually create room...
        Application.getModel().addRoom(new Room(this.currentName, /* whatever the fuck this is */ this.drawnPolygon.stream().mapToInt(i -> i).toArray()));
        // ok so actually its just using a stream to map an Integer array to an int array. java fucking sucks man...
        // i mean i COULD just use an Integer array in the room class but im too lazy to change it, arrest me

        // end drawing procedure
        this.endDraw();

        // redraw view after our room ahs been added
        this.view.draw();

        // repopulate our controller controller to account for the new room...
        Application.control().populate();
      }
    });

    this.view.setOnMouseClicked(e -> {
      if (this.drawing)
      {
        try
        {
          if (e.isShiftDown())
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
        }
        catch(Exception ex)
        {
          Application.debug(ex.getMessage());
        }
      }
    });

    sroot.setContent(this.view);
  }

  public FloorPlan getView()
  {
    return this.view;
  }
}
