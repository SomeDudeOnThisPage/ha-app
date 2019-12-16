package home.io;

import home.Application;
import home.model.House;
import home.model.Light;
import home.model.Room;
import javafx.application.Platform;

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
  /**
   * Verifies that the data received from the WSN is valid for our current model and returns the current House instance if successful.
   * @param roomID room ID
   * @param lightID light ID - set to '-1' to ignore
   * @return Current House instance
   */
  private House verify(int roomID, int lightID) throws Exception
  {
    House model = Application.getModel();
    if (model == null) { throw new Exception("no model loaded"); }
    if (model.getRoom(roomID) == null) { throw new Exception("room #" + roomID + " not found"); }

    // only verify if our light exists if we specified a lightID
    if (lightID != -1)
    {
      if (model.getRoom(roomID).getLight(lightID) == null) { throw new Exception("light #" + lightID + " in room #" + roomID + " not found"); }
    }

    return model;
  }

  /**
   * Implementation of the onLightSwitch callback.
   * @param roomID room ID
   * @param lightID light ID
   * @param state state
   */
  @Override
  public void onLightSwitch(int roomID, int lightID, Light.State state)
  {
    Application.debug("method \'onLightSwitch\' has been called");
    try
    {
      House model = this.verify(roomID, lightID);

      // set state in model
      model.getRoom(roomID).getLight(lightID).setState(state);

      // set state in controller
      Application.control().getRoomControls(roomID).getLightControls(lightID).cancelLightToggleResetTimer();

      Application.status("turned on light #" + lightID + " in \'" + model.getRoom(roomID).getName() + "\'");
    }
    catch(Exception e)
    {
      // uh oh, our house doesn't support the received message...
      Application.debug("could not switch on light #" + lightID + " in room #" + roomID + ": " + e.getMessage());
    }
  }

  /**
   * Implementation of the onLightMode callback
   * @param roomID room ID
   * @param lightID light ID
   * @param mode mode
   */
  @Override
  public void onLightMode(int roomID, int lightID, Light.Mode mode)
  {
    Application.debug("method \'onLightMode\' has been called");
    try
    {
      House model = this.verify(roomID, lightID);

      // set state in model
      model.getRoom(roomID).getLight(lightID).setMode(mode);

      // set state in controller
      // todo

      Application.status("changed mode of light #" + lightID + " in \'" + model.getRoom(roomID).getName() + "\'");
    }
    catch(Exception e)
    {
      // uh oh, our house doesn't support the received message...
      Application.debug("could not switch mode of light #" + lightID + " in room #" + roomID + ": " + e.getMessage());
    }
  }

  /**
   * Implementation of the onTemperature callback.
   * @param roomID room ID
   * @param actual actual temperature
   */
  @Override
  public void onTemperature(int roomID, float actual)
  {
    Application.debug("method \'onTemperature\' has been called");
    try
    {
      House model = this.verify(roomID, -1);

      // set state in model
      model.getRoom(roomID).temperature().set(actual);

      // set state in controller
      Platform.runLater(() -> Application.control().getRoomControls(roomID).setTemperature(actual));

      // let's not spam the status bar with temperature changes
      //Application.status("received new temperature data in \'" + model.getRoom(roomID).getName() + "\'");

    }
    catch(Exception e)
    {
      // uh oh, our house doesn't support the received message...
      Application.debug("could not set temperature in room #" + roomID + ": " + e.getMessage());
    }
  }

  /**
   * Implementation of the onDebug callback.
   * @param message debug message
   */
  @Override
  public void onDebug(String message)
  {
    Application.debug("method \'onDebug\' has been called");
    Application.debug("[WSN]: " + message);
  }

  @Override
  public void initHouse(Room room, Light light)
  {
    Application.debug("Initializing: ");
    //Room to roomID, Light to lightID???
  }
}
