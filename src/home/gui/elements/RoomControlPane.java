package home.gui.elements;

import home.Application;
import home.io.CommunicationAPI;
import home.model.Light;
import home.model.Room;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * @author Robin Buhlmann
 */
public class RoomControlPane extends TitledPane
{
  private VBox content;

  public RoomControlPane(Room room, int roomID)
  {
    super(room.getName(), new VBox());

    // somewhat counter-intuitive way to get the root element of our content but that's just how it is
    this.content = (VBox) this.getContent();

    // create a toggle button for all our lights
    Light[] lights = room.getLights();
    for (int i = 0; i < lights.length; i++)
    {
      Button bt = new Button("Light #" + i);
      bt.setUserData(i);

      // the button sets the light-mode to manual if it isn't on manual already and
      // toggles the lights' status
      bt.setOnAction(e -> {
        int lightID = (int) bt.getUserData();
        Light light = Application.getModel().getRoom(roomID).getLight(lightID);

        if (light.getMode() != Light.Mode.MODE_MANUAL)
        {
          CommunicationAPI.setLightMode(roomID, lightID, Light.Mode.MODE_MANUAL);
        }

        if (light.getState() == Light.State.LIGHT_ON)
        {
          CommunicationAPI.setLight(roomID, lightID, Light.State.LIGHT_OFF);
        }
        else
        {
          CommunicationAPI.setLight(roomID, lightID, Light.State.LIGHT_ON);
        }
      });

      // spent 10 minutes debugging why the button wasn't showing
      // turns out I forgot to add it to our pane
      // ...
      this.content.getChildren().add(bt);
    }
  }
}
