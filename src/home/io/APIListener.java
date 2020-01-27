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
  /**
   * Definition for onLightSwitch callback
   * @param roomID
   * @param lightID
   * @param state
   */
  void onLightSwitch(int roomID, int lightID, Light.State state);

  /**
   * Definition for onLightMode callback
   * @param roomID
   * @param lightID
   * @param mode
   */
  void onLightMode(int roomID, int lightID, Light.Mode mode);

  /**
   * Definition for onTemperature callback
   * @param roomID
   * @param actual
   */
  void onTemperature(int roomID, float actual);

  /**
   * Definition for onDebug callback
   * @param message
   */
  void onDebug(String message);

  /**
   * Definition for onTemperature callback
   * @param roomID
   * @param actual
   */
  void onTemperatureReference(int roomID, float actual);

  /**
   * Definition for onStart_init callback
   */
  void onStart_init();

  /**
   * Definition for onEnd_init callback
   */
  void onEnd_init();
}
