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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
   * Base grid size in pixels.
   */
  private static final double BASE_GRID_SIZE = 16.0;

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
  private static final double LIGHT_SIZE = 64.0;

  /**
   * Light sprite diffuse texture.
   */
  private Image light;

  /**
   * Light sprite diffuse texture if the light is on.
   */
  private Image light_on;

  private int scale;

  public boolean grid;

  /**
   * GraphicsContext of the canvas.
   */
  private GraphicsContext graphics;

  /**
   * Current scale of the view.
   * Clamped to SCALE_MAX, SCALE_MIN
   */
  private double scaleFactor;

  private ArrayList<Integer> currentDrawablePolygon;

  /**
   * Iterates over our grid and determines the point with the least distance to two arbitrary coordinates.
   * Returns a numerical index of our grid node that is closest.
   * @param x x-coordinate
   * @param y y-coordinate
   * @return point index
   * @throws Exception something something no closest point found
   */
  public int approximateNearestClickyPoint(double x, double y) throws Exception
  {
    double lDist = Double.MAX_VALUE;
    int fx = -1;
    int fy = -1;

    // iterate grid and find closest
    double interval = (this.getWidth() * this.scaleFactor) / (FloorPlan.BASE_GRID_SIZE * this.scaleFactor);
    for (double i = 1; i < this.scale + 1; i++)
    {
      for (double j = 1; j < this.scale + 1; j++)
      {
        // point coordinates
        double px = i * interval;
        double py = j * interval;

        // distance formula on 2d grounds
        double dist = Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2));

        // if the found distance is smaller than current, replace current
        if (dist < lDist)
        {
          fx = (int) i;
          fy = (int) j;

          lDist = dist;
        }
      }
    }

    if (fx != -1 && fy != -1)
    {
      return (fx - 1) + ((fy - 1) * this.scale);
    }

    throw new Exception("no clicky point found :(");
  }

  public void scale(double scale)
  {
    this.scaleFactor += scale;

    // clamp scale to preset values
    this.scaleFactor = Math.max(FloorPlan.SCALE_MIN, Math.min(FloorPlan.SCALE_MAX, this.scaleFactor));

    this.prefWidth(1024.0 * this.scaleFactor);
    this.prefHeight(1024.0 * this.scaleFactor);

    this.setWidth(1024.0 * this.scaleFactor);
    this.setHeight(1024.0 * this.scaleFactor);
  }

  public void draw()
  {
    this.graphics.clearRect(0, 0, this.getWidth(), this.getHeight());

    // grid interval used for drawing the grid and snap the room polygons to
    double interval = (this.getWidth() * this.scaleFactor) / (FloorPlan.BASE_GRID_SIZE * this.scaleFactor);

    // draw grid if we're in drawing mode or whatever...
    if (this.grid)
    {
      // create a grid of map_size * map size in x/y coordinates
      // these coords are also used to snap the polygons of the model rooms to
      for (double i = 1; i < scale + 1; i++)
      {
        for (double j = 1; j < scale + 1; j++)
        {
          this.graphics.strokeLine(interval * i - 2.5, interval * j, interval * i + 2.5, interval * j);
          this.graphics.strokeLine(interval * i, interval * j - 2.5, interval * i, interval * j + 2.5);
        }
      }
    }

    // draw model
    House model = Application.getModel();

    if (model == null) return;

    // stroke room structure polygons
    this.graphics.setLineWidth(10.0f * this.scaleFactor);

    for (Room room : model.getRooms())
    {
      int[] points = room.getPolygonialPointStructure();

      double[] px = new double[points.length];
      double[] py = new double[points.length];

      for (int i = 0; i < points.length; i++)
      {
        // convert numeric grid index to x/y coordinate and insert them into our polygon draw thingy
        px[i] = (points[i] % this.scale /* add 1 cause the grid starts at 1 * interval */ + 1) * interval;
        py[i] = Math.floor((double) points[i] / this.scale /* add 1 cause the grid starts at 1 * interval */ + 1) * interval;
      }

      // draw polygon with the points we created
      this.graphics.strokePolygon(px, py, points.length);
    }

    // draw Lights
    for (Room room: model.getRooms())
    {
      int i = 0;
      for (Light light : room.getLights())
      {
        double[] v2_position = light.getPosition();
        double size = FloorPlan.LIGHT_SIZE * this.scaleFactor;

        // draw off light sprite when our light is off, on light sprite when it is on
        // not a fancy way to do this but it works fine
        if (light.getState() == Light.State.LIGHT_OFF)
        {
          this.graphics.drawImage(this.light, v2_position[0] * this.scaleFactor, v2_position[1] * this.scaleFactor, size, size);
        }
        else
        {
          this.graphics.drawImage(this.light_on, v2_position[0] * this.scaleFactor, v2_position[1] * this.scaleFactor, size, size);
        }

        // draw light ID
        this.graphics.setFont(new Font("Arial", 16 * this.scaleFactor));
        this.graphics.fillText("Light #" + i++, v2_position[0] * this.scaleFactor, v2_position[1] * this.scaleFactor);
      }
    }

    // draw current drawing polygon
    if (this.currentDrawablePolygon != null)
    {
      this.graphics.setStroke(Color.RED);
      double[] px = new double[this.currentDrawablePolygon.size()];
      double[] py = new double[this.currentDrawablePolygon.size()];

      int i = 0;
      for (int index : this.currentDrawablePolygon)
      {
        px[i] = (index % this.scale /* add 1 cause the grid starts at 1 * interval */ + 1) * interval;
        py[i] = Math.floor((double) index / this.scale /* add 1 cause the grid starts at 1 * interval */ + 1) * interval;
        i++;
      }

      this.graphics.strokePolygon(px, py, this.currentDrawablePolygon.size());
    }

    this.graphics.setStroke(Color.BLACK);
    this.graphics.setLineWidth(1.0f);
  }

  public void setDrawablePolygon(ArrayList polygonIndices)
  {
    this.currentDrawablePolygon = polygonIndices;
  }

  public FloorPlan(String path, JSONObject data)
  {
    // debug
    this.grid = true;

    this.prefWidth(1024);
    this.prefHeight(1024);

    this.setWidth(1024);
    this.setHeight(1024);

    this.graphics = this.getGraphicsContext2D();
    this.scaleFactor = 1.0;

    this.scale = ((Long) data.get("map_size")).intValue();

    // redraw on canvas-size change
    widthProperty().addListener(e -> this.draw());
    heightProperty().addListener(e -> this.draw());

    try
    {
      BufferedImage light = ImageIO.read(new File(path + "\\" + data.get("light_diffuse").toString()));
      this.light = SwingFXUtils.toFXImage(light, null);

      BufferedImage light_on = ImageIO.read(new File(path + "\\" + data.get("light_diffuse_on").toString()));
      this.light_on = SwingFXUtils.toFXImage(light_on, null);
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

    this.draw();
  }
}

















