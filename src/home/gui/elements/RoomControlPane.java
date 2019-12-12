package home.gui.elements;

import home.Application;
import home.io.CommunicationAPI;
import home.model.Light;
import home.model.Room;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

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

      this.content.getChildren().add(bt);
    }
  }
}
