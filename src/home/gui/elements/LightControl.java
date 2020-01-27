package home.gui.elements;

import home.Application;
import home.io.CommunicationAPI;
import home.model.Light;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the light config element.
 * The room config element holds several of these, as much as there are lights in a room.
 */
public class LightControl
{
  @FXML
  private Label state;

  @FXML
  private Label mode;

  /** ID of the managed light. */
  private int lightID;

  /** ID of the room the managed light is in. */
  private int roomID;

  /**
   * Sets the room and light ID of the managed light
   * @param room id
   * @param light id
   */
  public void setLight(int room, int light)
  {
    this.roomID = room;
    this.lightID = light;
  }

  /**
   * Returns the light ID of the managed light.
   * @return id
   */
  public int getLight()
  {
    return this.lightID;
  }

  /**
   * Sets the state content of the information label.
   * @param state light state
   */
  public void setState(Light.State state)
  {
    String s = (state == Light.State.LIGHT_ON) ? "on" : "off";
    this.state.setText(s);
  }

  /**
   * Sets the mode content of the information label.
   * @param mode light mode
   */
  public void setMode(Light.Mode mode)
  {
    String s = (mode == Light.Mode.MODE_AUTOMATIC) ? "auto" : "manual";
    this.mode.setText(s);
  }

  /**
   * Called when the state button has been pressed. Signals the CommunicationAPI to send an instruction.
   */
  @FXML
  public void onLightToggle()
  {
    // send message to WSN
    Light light = Application.getModel().getRoom(this.roomID).getLight(this.lightID);

    if (light.getMode() == Light.Mode.MODE_AUTOMATIC)
    {
      CommunicationAPI.setLightMode(this.roomID, this.lightID, Light.Mode.MODE_MANUAL);
    }

    if (light.getState() == Light.State.LIGHT_ON)
    {
      CommunicationAPI.setLight(this.roomID, this.lightID, Light.State.LIGHT_OFF);
    }
    else if (light.getState() == Light.State.LIGHT_OFF)
    {
      CommunicationAPI.setLight(this.roomID, this.lightID, Light.State.LIGHT_ON);
    }
  }

  /**
   * Called when the mode button has been pressed. Signals the CommunicationAPI to send an instruction.
   */
  @FXML
  public void onModeToggle()
  {
    // send message to WSN
    Light light = Application.getModel().getRoom(this.roomID).getLight(this.lightID);

    if (light.getMode() == Light.Mode.MODE_MANUAL)
    {
      CommunicationAPI.setLightMode(this.roomID, this.lightID, Light.Mode.MODE_AUTOMATIC);
    }
    else if (light.getMode() == Light.Mode.MODE_AUTOMATIC)
    {
      CommunicationAPI.setLightMode(this.roomID, this.lightID, Light.Mode.MODE_MANUAL);
    }
  }
}
