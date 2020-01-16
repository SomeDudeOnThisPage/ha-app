package home.io;
import home.Application;
import home.model.Light;

import java.time.chrono.IsoChronology;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * <h1>CommunicationAPI</h1>
 * The static CommunicationAPI class is used to handle incoming and outgoing network traffic between the application and the WSN.
 * It uses an APIListener Interface set on creation to implement simple callbacks.
 *
 * @author Maximilian Morlock
 * @version -1000000
 * @since 2019-11-30
 */
public class CommunicationAPI
{

  /**
   * This is the listener used that implements the callbacks defined in the APIListener class.
   * @see APIListener
   */
  private static APIListener listener;

  /**
   * This method is called when the application first starts and defines a listener object with attached callbacks.
   * @param listener APIListener instance
   */
  public static synchronized void initialize(APIListener listener)
  {
    CommunicationAPI.listener = listener;
  }

  public static synchronized void setLight(int roomid, int lightid, Light.State status){
    String s = (status == Light.State.LIGHT_ON) ? "on" : "off";

    String message = "light_switch "+roomid+" "+lightid+" "+s;
    SerialIO.write(message + "\r\n");
  }

  public static synchronized void setLightMode(int roomid, int lightid, Light.Mode mode){
    String s = (mode == Light.Mode.MODE_MANUAL) ? "manual" : "auto";

    String message = "light_mode "+roomid+" "+lightid+" "+s;
    SerialIO.write(message + "\r\n");
  }

  public static synchronized void tempReference(int roomid, float temp){
    String message = "temperature_reference "+roomid+" "+temp;
    SerialIO.write(message + "\r\n");
  }

  public static synchronized void init()
  {
    String message = "start_init";
    SerialIO.write(message + "\r\n");
  }


  /**
   * This method is called by the serial-management class when an ingoing message is received.
   * @param data Received data in serialized string form
   * @see SerialIO
   */
  public static void update(Byte[] data) {
    Application.debug("processing data packet with content " + Arrays.toString(data).replace("\n", "\\n").replace("\r", "\\r"));

    if (listener == null ) {Application.debug("ERROR: LISTENER NOT AVAILABLE\r\n");}

    if (data.length != 4) {
      Application.debug("invalid length of received data", Level.WARNING);
    }
    else {
      Byte instruction = data[0];
      switch (instruction) {
        //start_init
        case 0x00:
          listener.onStart_init();
          break;

        //end_init
        case 0x01:
          listener.onEnd_init();
          break;

        //light_switch
        case 0x02:
          byte light_switch = data[3];
          Light.State state;

          if (light_switch == 0x01){
            state = Light.State.LIGHT_ON;
            listener.onLightSwitch(data[1], data[2], state);
          }
          else if (light_switch == 0x00){
            state = Light.State.LIGHT_OFF;
            listener.onLightSwitch(data[1], data[2], state);
          }
          break;

        //light_mode
        case 0x03:
          byte light_mode = data[3];
          Light.Mode mode;

          if (light_mode == 0x00){
            mode = Light.Mode.MODE_MANUAL;
            listener.onLightMode(data[1], data[2], mode);
          }
          else if (light_mode == 0x01){
            mode = Light.Mode.MODE_AUTOMATIC;
            listener.onLightMode(data[1], data[2], mode);
          }
          break;

        //temperature
        case 0x04:
            float result = data[2];
            float decimal = data[3];
            decimal = decimal/100;
            result = result + decimal;
            listener.onTemperature(data[1], result);
            break;

        //temperature_reference
        case 0x05:
          float reference = data[2];
          float decimal2 = data[3];
          decimal = decimal2/100;
          result = reference + decimal2;
          listener.onTemperatureReference(data[1], reference);
          break;

        default:
          Application.debug("received invalid message - no instruction matches");
          break;
      }
    }


    /*
    String[] parts = data.split(" ");

    String instruction = parts[0];

    switch (instruction) {
      case "light_switch":

        if (parts.length < 4 || parts[1] == null || parts[2] == null || parts[3] == null) {
          Application.debug("received message for method light_switch not valid", Level.WARNING);
          break;
        }
        int roomID = Integer.parseInt(parts[1]);
        int lightID = Integer.parseInt(parts[2]);
        String instruct = parts[3];

        Light.State state;

        if (instruct.equals("on")) {
          state = Light.State.LIGHT_ON;
          listener.onLightSwitch(roomID, lightID, state);
        } else if (instruct.equals("off")) {
          state = Light.State.LIGHT_OFF;
          listener.onLightSwitch(roomID, lightID, state);
        }
        break;

      case "light_mode":
        if (parts.length < 4 || parts[1] == null || parts[2] == null || parts[3] == null) {
          Application.debug("received message for method light_mode not valid", Level.WARNING);
          break;
        }

        int roomID2 = Integer.parseInt(parts[1]);
        int lightID2 = Integer.parseInt(parts[2]);
        String instructionData = parts[3];
        Light.Mode mode;

        if (instructionData.equals("auto")) {
          mode = Light.Mode.MODE_AUTOMATIC;
          listener.onLightMode(roomID2, lightID2, mode);
        } else if (instructionData.equals("manual")) {
          mode = Light.Mode.MODE_MANUAL;
          listener.onLightMode(roomID2, lightID2, mode);
        }
        break;

      case "temperature":
        if (parts.length < 3 || parts[1] == null || parts[2] == null) {
          Application.debug("received message for method temperature not valid", Level.WARNING);
          break;
        }
        float temperature = Float.parseFloat(parts[2]);
        listener.onTemperature(Integer.parseInt(parts[1]), temperature);
        break;

      case "temperature_reference":
        if (parts.length < 3 || parts[1] == null || parts[2] == null) {
          Application.debug("received message for method temperature_reference not valid", Level.WARNING);
          break;
        }
        float reference = Float.parseFloat(parts[2]);
        listener.onTemperatureReference(Integer.parseInt(parts[1]), reference);
        break;

      case "start_init":
        if (parts.length != 1) {
          Application.debug("not a valid operation");
          break;
        }
        listener.onStart_init();
        break;

      case "end_init":
        if (parts.length != 1) {
          Application.debug("not a valid operation");
          break;
        }
        listener.onEnd_init();
        break;

      default:
        Application.debug("received invalid message - no instruction matches");
        break;
      }*/
   }
  }
