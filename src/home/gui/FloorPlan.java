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

public class FloorPlan extends Canvas
{
  private static final double BASE_SIZE = 1024.0;
  private static final double SCALE_MAX = 2.0;
  private static final double SCALE_MIN = 0.5;

  private static final double LIGHT_SIZE = 128.0;

  private Image diffuse;
  private Image light;

  private GraphicsContext graphics;
  private double scaleFactor;

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
  }

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

  public FloorPlan(String path, JSONObject data)
  {
    this.prefWidth(1024);
    this.prefHeight(1024);

    this.setWidth(1024);
    this.setHeight(1024);

    this.graphics = this.getGraphicsContext2D();
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
      Application.debug("could not load image from path " + path + "\\" + data.get("diffuse").toString());

      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Could not load Floor Plan Image");
      alert.setContentText("cannot load image from \'" + path + "\\" + data.get("diffuse").toString() + "\'\nValidate your files or try again with a different map.");
      alert.showAndWait();
    }

    this.scale(1.25);
    this.setVisible(true);
  }
}
