package home.gui;

import home.Application;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import org.json.simple.JSONObject;

import java.net.URL;
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

  @Override
  public void initialize(URL ignored0, ResourceBundle ignored1)
  {
    sroot.setPannable(true);

    // custom scroll event filter to scale our view instead of scrolling our scroll pane
    sroot.addEventFilter(ScrollEvent.ANY, event -> {
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

  /**
   * Constructs a FloorPlan and appends the canvas to the scroll pane.
   * @param path path to the map folder
   * @param data contents of map.json
   */
  public void setView(String path, JSONObject data)
  {
    this.view = new FloorPlan(path, data);
    sroot.setContent(this.view);
  }

  public FloorPlan getView()
  {
    return this.view;
  }
}
