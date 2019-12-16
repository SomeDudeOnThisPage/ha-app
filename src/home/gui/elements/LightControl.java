package home.gui.elements;

import home.Application;
import home.io.CommunicationAPI;
import home.model.House;
import home.model.Light;
import javafx.fxml.FXML;

/**
 * Controller for the light config element.
 * The room config element holds several of these, as much as there are lights in a room.
 */
public class LightControl
{
  private int lightID;
  private int roomID;

  public void setLight(int room, int light)
  {
    this.roomID = room;
    this.lightID = light;
  }

  public void cancelLightToggleResetTimer()
  {
  }

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
