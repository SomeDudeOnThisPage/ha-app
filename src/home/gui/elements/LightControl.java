package home.gui.elements;

import home.model.Light;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

/**
 * Controller for the light config element.
 * The room config element holds several of these, as much as there are lights in a room.
 */
public class LightControl
{
  private Light light;

  @FXML
  protected ToggleButton lightToggle;

  public void setLight(Light light)
  {
    this.light = light;
  }

  @FXML
  public void onLightToggle()
  {
    this.lightToggle.setDisable(true);
  }
}
