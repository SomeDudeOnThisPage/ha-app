package home.io;
import home.model.Light;

/**
 * <h1>APIListener</h1>
 * This interface should be implemented by classes that want to listen to the ingoing serial messages.
 *
 * @author Maximilian Morlock
 * @version 1.0
 * @since 2019-11-30
 */
public interface APIListener
{
  //definition for ingoing callbacks
  void onLightSwitch(int roomID, int lightID, Light.State state);
  void onLightMode(int roomID, int lightID, Light.Mode mode);
  void onTemperature(int roomID, float actual);
  void onTemperatureReference(int roomID, float actual);
  void onStart_init();
  void onEnd_init();
}
