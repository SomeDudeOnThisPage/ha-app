package home.model;

import home.Application;
import home.gui.DialogManager;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Robin Buhlmann
 * @since 2019-11-22
 */
public class Temperature extends ImageView
{
  private float reference;
  private float value;

  private double[] position;

  private Tooltip tooltip;

  private String name;

  public void setReference(float reference)
  {
    this.reference = reference;
    this.updateTooltip();
  }
  public float getReference() { return this.reference; }

  public void set(float value)
  {
    this.value = value;
    this.updateTooltip();
  }
  public float get() { return this.value; }

  public double[] getPosition()
  {
    return this.position;
  }

  private void updateTooltip()
  {
    //Tooltip.uninstall(this, this.tooltip);
    //Tooltip.install(this, tooltip);
    String v = (this.value <= 0) ? "n/a" : this.value + "°C";
    String reference = (this.reference <= 0) ? "n/a" : this.reference + "°C";

    Platform.runLater(() -> this.tooltip.setText("Room:\t" + this.name + "\nCurrent Temperature:\t" + v + "\nReference Temperature:\t" + reference));
  }

  public Temperature(float reference, float value, double[] position, String name)
  {
    try
    {
      BufferedImage diffuse = ImageIO.read(new File("resources/textures/radiator_diffuse.png"));
      this.setImage(SwingFXUtils.toFXImage(diffuse, null));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    this.tooltip = new Tooltip();
    this.tooltip.setShowDelay(new Duration(0.0));
    this.tooltip.setHideDelay(new Duration(0.0));
    this.tooltip.setShowDuration(new Duration(Double.MAX_VALUE));

    Tooltip.install(this, this.tooltip);

    ContextMenu cmenu = new ContextMenu();
    MenuItem delete = new MenuItem("Delete");
    delete.setOnAction(e -> {
      // remove self. the java equivalent of suicide.
      if (DialogManager.confirm("Are you sure?", "Do you want to remove this Temperature Indicator? This cannot be undone!"))
      {
        for (Room room : Application.getModel().getRooms())
        {
          if (room.temperature() == this)
          {
            // disable drawing
            room.temperature().position[0] = -1;
            room.temperature().position[1] = -1;
            Application.canvas().removeInteractable(this);
            return;
          }
        }
      }
    });

    cmenu.getItems().addAll(delete);

    this.setOnMouseClicked(e -> {
      if (e.isShiftDown() && e.getButton() == MouseButton.SECONDARY && !Application.canvas().isDrawing())
      {
        cmenu.show(this, e.getScreenX(), e.getScreenY());
      }
    });

    this.setOnContextMenuRequested(Event::consume);

    double[] t = new double[2];
    this.setOnMousePressed(e -> {
      if (e.isShiftDown() && !Application.canvas().isDrawing())
      {
        if (e.getButton() == MouseButton.PRIMARY)
        {
          t[0] = this.getTranslateX() - e.getSceneX();
          t[1] = this.getTranslateY() - e.getSceneY();
          this.setCursor(Cursor.MOVE);
        }
      }
    });

    this.setOnMouseReleased(e -> {
      this.setCursor(Cursor.DEFAULT);
    });

    this.setOnMouseDragged(e -> {
      if (e.isShiftDown() && e.getButton() == MouseButton.PRIMARY && !Application.canvas().isDrawing())
      {
        this.setTranslateX(e.getSceneX() + t[0]);
        this.setTranslateY(e.getSceneY() + t[1]);
        this.position[0] = (e.getSceneX() + t[0]) / Application.canvas().getView().getScale();
        this.position[1] = (e.getSceneY() + t[1]) / Application.canvas().getView().getScale();

        e.consume();
      }
    });

    this.reference = reference;
    this.value = value;
    this.name = name;

    this.position = position;
    this.updateTooltip();
  }

  public Temperature(double[] position, String name)
  {
    this(-1, -1, position, name);
  }
}
