package home.io;

import home.Application;
import home.model.Light;

/**
 * <h1>SerialAPIListener</h1>
 * This class implements the APIListener class.
 * It is used to specify behavior of the ingoing API callback methods.
 * An instance of this class is kept as reference by the CommunicationAPI class after it has been initialized.
 * @see CommunicationAPI
 *
 * @author Robin Buhlmann
 * @version 0.1
 * @since 2019-11-30
 */
public class SerialAPIListener implements APIListener
{
  @Override
  public void onLightSwitch(int roomID, int lightID, Light.State state)
  {
    Application.debug("method \'onLightSwitch\' has been called");
  }

  @Override
  public void onLightMode(int roomID, int lightID, Light.Mode mode)
  {
    Application.debug("method \'onLightMode\' has been called");
  }

  @Override
  public void onTemperature(int roomID, int lightID, double actual)
  {
    Application.debug("method \'onTemperature\' has been called");
  }

  @Override
  public void onDebug(String message)
  {
    Application.debug("method \'onDebug\' has been called");
  }
}
