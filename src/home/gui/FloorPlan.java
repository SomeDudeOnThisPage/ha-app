package home.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class FloorPlan extends Canvas
{
  private static final double BASE_SIZE = 1024.0;

  private Image diffuse;

  private GraphicsContext graphics;
  private double scaleFactor;

  private void draw()
  {
    this.graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
    this.graphics.drawImage(this.diffuse, 0.0, 0.0, FloorPlan.BASE_SIZE * this.scaleFactor, FloorPlan.BASE_SIZE * this.scaleFactor);
  }

  private void scale(double factor)
  {
    this.scaleFactor = factor;

    this.prefWidth(1024.0 * this.scaleFactor);
    this.prefHeight(1024.0 * this.scaleFactor);

    this.setWidth(1024.0 * this.scaleFactor);
    this.setHeight(1024.0 * this.scaleFactor);
  }

  public FloorPlan()
  {
    this.prefWidth(1024);
    this.prefHeight(1024);

    this.setWidth(1024);
    this.setHeight(1024);

    this.graphics = this.getGraphicsContext2D();
    widthProperty().addListener(e -> this.draw());
    heightProperty().addListener(e -> this.draw());

    this.diffuse = new Image(getClass().getResource("/maps/m01/diffuse.png").toString());

    this.scale(0.5);
    this.setVisible(true);
  }
}
