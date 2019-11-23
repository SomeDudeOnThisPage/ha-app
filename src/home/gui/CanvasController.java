package home.gui;

import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import org.json.simple.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is used to control the functionality of the canvas subscene.
 *
 * @see MainController
 *
 * @author Robin Buhlmann
 * @version 0.1
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

      event.consume();
    });
  }

  /**
   * Constructs a FloorPlan
   */
  public void setView(String path, JSONObject data)
  {
    this.view = new FloorPlan(path, data);
    sroot.setContent(this.view);
  }
}
