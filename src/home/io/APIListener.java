package home.io;

import home.model.Light;
import home.model.Room;

/**
 * <h1>APIListener</h1>
 * This interface should be implemented by classes that want to listen to the ingoing serial messages.
 *
 * @author Maximus Morlockus
 * @version 0.1
 * @since 2019-11-30
 */
public interface APIListener
{
  // og also hier kannst du alle INGOING callbacks definieren, die kann ich dann je nach implementierung des modells implementieren in einer
  // klasse die das interface hier implementiert

  void onLightSwitch(int roomID, int lightID, Light.State state);
  void onLightMode(int roomID, int lightID, Light.Mode mode);
  void onTemperature(int roomID, float actual);
  void onDebug(String message);
}
