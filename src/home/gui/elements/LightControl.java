package home.gui.elements;

import home.model.Light;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller for the light config element.
 * The room config element holds several of these, as much as there are lights in a room.
 */
public class LightControl
{
  private Light light;

  private Timer cancelLightToggleResetTimer;

  @FXML
  protected ToggleButton lightToggle;

  public void setLight(Light light)
  {
    this.light = light;
  }

  public void cancelLightToggleResetTimer()
  {
    if (this.cancelLightToggleResetTimer != null)
    {
      this.cancelLightToggleResetTimer.cancel();
    }
  }

  @FXML
  public void onLightToggle()
  {
    ToggleButton t = this.lightToggle;
    boolean previous = !lightToggle.isSelected();
    System.out.println(previous);
    TimerTask timer = new TimerTask()
    {
      @Override
      public void run()
      {
        t.setDisable(false);
        t.setSelected(previous);
      }
    };

    this.cancelLightToggleResetTimer = new Timer("soose");
    this.cancelLightToggleResetTimer.schedule(timer, 15000L);

    this.lightToggle.setDisable(true);
  }
}
