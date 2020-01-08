package home.gui.elements;

import home.Application;
import home.model.House;
import home.model.Light;
import home.model.Room;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

/**
 * FloorPlan class extending Canvas.
 * Used for visual representation of our current model.
 *
 * WARNING: THIS CLASS IS A MESS. THE CODE IS A MESS. THE DRAWING METHODS ARE A MESS.
 *
 * @author Robin Buhlmann
 * @version 0.1
 * @since 2019-11-26
 */
public class FloorPlan extends Canvas
{
  /**
   * Base grid size in pixels.
   */
  private static final double BASE_GRID_SIZE = 64.0f;

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
   * Current scale or whatever.
   */
  private int scale;

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
   * Draws a grid based on the parameters set in the FloorPlan class.
   */
  private void drawGrid()
  {
    // grid interval used for drawing the grid and snap the room polygons to
    double interval = this.getWidth() / (this.scale + 1);

    // create a grid of map_size * map size in x/y coordinates
    // these coords are also used to snap the polygons of the model rooms to
    for (double i = 1; i < scale + 1; i ++)
    {
      for (double j = 1; j < scale + 1; j++)
      {
        this.graphics.strokeLine(interval * i - 2.5, interval * j, interval * i + 2.5, interval * j);
        this.graphics.strokeLine(interval * i, interval * j - 2.5, interval * i, interval * j + 2.5);
      }
    }
  }

  /**
   * Screen space (actually I think its actually NDC) coordinates to world space coordinates
   * @param x x coord
   * @param y y coord
   * @return coords normalized to world space
   */
  public double[] translateCoordinates(double x, double y)
  {
    return new double[] {x / this.scaleFactor, y / this.scaleFactor};
  }

  /**
   * Draws all rooms of a model.
   * @param model model
   */
  private void drawRooms(House model)
  {
    for (Room room : model.getRooms())
    {
      this.drawRoom(room.getIndices());
    }
  }

  /**
   * Creates and renders a single light sprite at given coordinates.
   * @param light light
   * @param drawID draw the ID of the light as text or not
   */
  private void drawLight(Light light, boolean drawID)
  {
    double x = light.getPosition()[0];
    double y = light.getPosition()[1];

    double radius = FloorPlan.LIGHT_SIZE / 2 * this.scaleFactor;

    this.graphics.strokeOval(this.scaleFactor * x - radius, this.scaleFactor * y - radius, radius, radius);

    if (drawID)
    {

    }
  }

  /**
   * Creates and renders a single room polygon.
   * @param indices polygon indices
   */
  private void drawRoom(ArrayList<Integer> indices)
  {
    double interval = this.getWidth() / (this.scale + 1);

    double[] px = new double[indices.size()];
    double[] py = new double[indices.size()];

    for (int i = 0; i < indices.size(); i++)
    {
      // convert numeric grid index to x/y coordinate and insert them into our polygon draw thingy
      px[i] = (indices.get(i) % this.scale /* add 1 cause the grid starts at 1 * interval */ + 1) * interval;
      py[i] = Math.floor((double) indices.get(i) / this.scale /* add 1 cause the grid starts at 1 * interval */ + 1) * interval;
    }

    // draw polygon with the points we created
    this.graphics.strokePolygon(px, py, indices.size());
  }

  /**
   * Draws all lights of a model.
   * @param model model
   */
  private void drawLights(House model)
  {
    for (Room room: model.getRooms())
    {
      for (Light light : room.getLights())
      {
        this.drawLight(light, true);
      }
    }
  }

  /**
   * Honestly I've given up naming my methods something descriptive so <b>BASK IN THE GLORY OF 'approximateNearestClickyPoint', MORTAL!</b>
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
    double interval = this.getWidth() / (this.scale + 1);
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

  /**
   * Scales the content of the canvas.
   * @param scale scale
   */
  public void scale(double scale)
  {
    this.scaleFactor += scale;

    // clamp scale to preset values
    this.scaleFactor = Math.max(FloorPlan.SCALE_MIN, Math.min(FloorPlan.SCALE_MAX, this.scaleFactor));

    this.prefWidth(BASE_GRID_SIZE * this.scale * this.scaleFactor);
    this.prefHeight(BASE_GRID_SIZE * this.scale * this.scaleFactor);

    this.setWidth(BASE_GRID_SIZE * this.scale * this.scaleFactor);
    this.setHeight(BASE_GRID_SIZE * this.scale * this.scaleFactor);
  }

  /**
   * (Re-) Draws the scene.
   */
  public void draw()
  {
    // clear canvas
    this.graphics.clearRect(0, 0, this.getWidth(), this.getHeight());

    // if there's no model one shall'nt shat themselves
    House model = Application.getModel();
    if (model == null) return;

    // draw grid if we're in drawing mode or whatever...
    if (Application.canvas().isDrawing()) { this.drawGrid(); }

    // prepare line widths for polygon drawing
    this.graphics.setLineWidth(this.scale / 2.0f * this.scaleFactor);

    // draw rooms
    this.drawRooms(model);

    // draw Lights. Do this AFTER drawing rooms should a light overlap with walls (e.g. a wall light)!
    this.drawLights(model);

    // draw current drawing polygon last so it is visible over everything
    if (Application.canvas().isDrawing())
    {
      ArrayList<Integer> polygon = Application.canvas().getDrawingPolygon();

      if (polygon != null)
      {
        this.drawRoom(polygon);
        double interval = this.getWidth() / (this.scale + 1);

        // draw it in red to differentiate it from existing rooms
        this.graphics.setStroke(Color.RED);
        double[] px = new double[polygon.size()];
        double[] py = new double[polygon.size()];

        int i = 0;
        for (int index : polygon)
        {
          px[i] = (index % this.scale /* add 1 cause the grid starts at 1 * interval */ + 1) * interval;
          py[i] = Math.floor((double) index / this.scale /* add 1 cause the grid starts at 1 * interval */ + 1) * interval;
          i++;
        }

        this.graphics.strokePolygon(px, py, polygon.size());
      }

      Light light = Application.canvas().getDrawingLight();

      if (light != null)
      {
        this.drawLight(light, false);
      }
    }

    // reset stroke
    this.graphics.setStroke(Color.BLACK);
    this.graphics.setLineWidth(1.0f);
  }

  /**
   * Extends Canvas.
   * @param size size of the map in grid ticks
   */
  public FloorPlan(int size)
  {
    this.graphics = this.getGraphicsContext2D();
    this.scaleFactor = 1.0;

    this.scale = size;

    float s = scale * (float) BASE_GRID_SIZE;

    this.prefWidth(s);
    this.prefHeight(s);

    this.setWidth(s);
    this.setHeight(s);

    // redraw on canvas-size change
    widthProperty().addListener(e -> this.draw());
    heightProperty().addListener(e -> this.draw());

    this.draw();
  }
}

















