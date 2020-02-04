package home.model;

import home.Application;
import home.gui.DialogManager;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * It's a light.
 * The light can be either on or off, and its' mode can be either manual or automatic.
 *
 * @author Robin Buhlmann
 * @since 2019-11-22
 */
public class Light extends Circle
{
  /**
   * State of a light.
   */
  public enum State { LIGHT_ON, LIGHT_OFF }

  /**
   * Mode of a light.
   */
  public enum Mode { MODE_AUTOMATIC, MODE_MANUAL }

  /**
   * State of the light.
   */
  private State state;

  /**
   * Mode of the light.
   */
  private Mode mode;

  /**
   * Position of the light on the 2D canvas.
   */
  private double[] position;

  private int id;
  private String name;

  private Tooltip tooltip;

  /**
   * Sets the state of the light.
   * @param state state
   */
  public void setState(State state)
  {
    this.state = state;
    this.updateTooltip();
  }

  /**
   * Returns the state of the light.
   * @return state
   */
  public Light.State getState() { return this.state; }

  /**
   * Sets the mode of the light.
   * @param mode mode
   */
  public void setMode(Mode mode)
  {
    this.mode = mode;
    this.updateTooltip();
  }

  /**
   * Returns the mode of the light.
   * @return mode
   */
  public Mode getMode() { return this.mode; }

  /**
   * Returns the position of the light.
   * @return position
   */
  public double[] getPosition() { return this.position; }

  public int getID()
  {
    return this.id;
  }

  public void setPosition(double x, double y)
  {
    this.position[0] = x;
    this.position[1] = y;
  }

  private void updateTooltip()
  {
    //Tooltip.uninstall(this, this.tooltip);
    //Tooltip.install(this, tooltip);
    Platform.runLater(() -> this.tooltip.setText("Room:\t" + this.name + "\nLight\t#" + this.id + "\nState:\t" + this.state + "\nMode:\t" + this.mode));
  }

  /**
   * Creates a new Light object.
   * @param x x-position on the 2D canvas
   * @param y y-position on the 2D canvas
   */
  public Light(int id, double x, double y, String name)
  {
    this.id = id;

    this.state = State.LIGHT_OFF;
    this.mode = Mode.MODE_MANUAL;

    this.name = name;

    this.position = new double[] {x, y};

    DropShadow shadow = new DropShadow();
    shadow.setOffsetY(6.0f);
    shadow.setOffsetX(2.0f);
    shadow.setColor(Color.DARKGRAY);

    this.tooltip = new Tooltip();
    this.tooltip.setShowDelay(new Duration(0.0));
    this.tooltip.setHideDelay(new Duration(0.0));
    this.tooltip.setShowDuration(new Duration(Double.MAX_VALUE));

    Tooltip.install(this, this.tooltip);

    this.updateTooltip();

    this.setEffect(shadow);

    ContextMenu cmenu = new ContextMenu();
    MenuItem delete = new MenuItem("Delete");
    delete.setOnAction(e -> {
      // remove self. the java equivalent of suicide.
      if (DialogManager.confirm("Are you sure?", "Do you want to remove this Light? This cannot be undone!"))
      {
        for (Room room : Application.getModel().getRooms())
        {
          room.getLights().remove(this);
        }
        Application.canvas().removeInteractable(this);
      }
    });

    cmenu.getItems().addAll(delete);

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
        else if (e.getButton() == MouseButton.SECONDARY)
        {
          cmenu.show(this, e.getScreenX(), e.getScreenY());
        }
      }
    });

    this.setOnContextMenuRequested(Event::consume);

    this.setOnMouseReleased(e -> {
      this.setCursor(Cursor.DEFAULT);
    });

    this.setOnMouseEntered(e -> this.setCursor(Cursor.CROSSHAIR));
    this.setOnMouseExited(e -> this.setCursor(Cursor.DEFAULT));

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
  }
}