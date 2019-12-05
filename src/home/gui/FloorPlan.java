package home.gui;

import home.Application;
import home.model.House;
import home.model.Light;
import home.model.Room;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * FloorPlan class extending Canvas.
 * Used for visual representation of our current model.
 *
 * @author Robin Buhlmann
 * @version 0.1
 * @since 2019-11-26
 */
public class FloorPlan extends Canvas
{
  /**
   * Base canvas size in pixels.
   */
  private static final double BASE_SIZE = 1024.0;

  /**
   * Maximum scale of visual canvas content.
   */
  private static final double SCALE_MAX = 2.0;

  /**
   * Minimum scale of visual canvas content.
   */
  private static final double SCALE_MIN = 0.5;

  /**
   * Base-Size of light sprites.
   */
  private static final double LIGHT_SIZE = 128.0;

  /**
   * Floor Plan diffuse texture.
   */
  private Image diffuse;

  /**
   * Light sprite diffuse texture.
   */
  private Image light;

  /**
   * GraphicsContext of the canvas.
   */
  private GraphicsContext graphics;

  /**
   * Current scale of the view.
   * Clamped to SCALE_MAX, SCALE_MIN
   */
  private double scaleFactor;

  /**
   * (Re-)draws the view.
   */
  private void draw()
  {
    // draw base floor plan
    this.graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
    this.graphics.drawImage(this.diffuse, 0.0, 0.0, FloorPlan.BASE_SIZE * this.scaleFactor, FloorPlan.BASE_SIZE * this.scaleFactor);

    // draw lights
    House model = Application.getModel();
    if (model != null)
    {
      for (Room room : model.getRooms())
      {
        for (Light light : room.getLights())
        {
          double[] v2_position = light.getPosition();
          double size = FloorPlan.LIGHT_SIZE * this.scaleFactor;
          this.graphics.drawImage(this.light, v2_position[0] * this.scaleFactor, v2_position[1] * this.scaleFactor, size, size);
        }
      }
    }

    // draw temperature text
    // to-do
  }

  /**
   * Scales the content by a factor, clamps the scale and redraws the scene.
   * @param factor scale (additive)
   */
  public void scale(double factor)
  {
    this.scaleFactor += factor;

    // clamp scale to preset values
    this.scaleFactor = Math.max(FloorPlan.SCALE_MIN, Math.min(FloorPlan.SCALE_MAX, this.scaleFactor));

    this.prefWidth(1024.0 * this.scaleFactor);
    this.prefHeight(1024.0 * this.scaleFactor);

    this.setWidth(1024.0 * this.scaleFactor);
    this.setHeight(1024.0 * this.scaleFactor);
  }

  /**
   * Creates a new FloorPlan object.
   * @param path path to the folder containing map files
   * @param data data from map.json
   */
  public FloorPlan(String path, JSONObject data)
  {
    this.prefWidth(1024);
    this.prefHeight(1024);

    this.setWidth(1024);
    this.setHeight(1024);

    this.graphics = this.getGraphicsContext2D();

    // redraw on canvas-size change
    widthProperty().addListener(e -> this.draw());
    heightProperty().addListener(e -> this.draw());

    try
    {
      BufferedImage diffuse = ImageIO.read(new File(path + "\\" + data.get("diffuse").toString()));
      this.diffuse = SwingFXUtils.toFXImage(diffuse, null);

      BufferedImage light = ImageIO.read(new File(path + "\\" + data.get("light_diffuse").toString()));
      this.light = SwingFXUtils.toFXImage(light, null);
    }
    catch (IOException e)
    {
      // uh oh, seems like some data is corrupted and we couldn't load our map...
      Application.debug("could not load image from path " + path + "\\" + data.get("diffuse").toString(), Level.WARNING);

      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Could not load Floor Plan Image");
      alert.setContentText("cannot load image from \'" + path + "\\" + data.get("diffuse").toString() + "\'\nValidate your files or try again with a different map.");
      alert.showAndWait();

      return;
    }

    this.scale(1.25);
    this.draw();
    this.setVisible(true);
  }
}
