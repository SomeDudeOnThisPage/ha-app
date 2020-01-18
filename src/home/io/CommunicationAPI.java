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

    String hex1 = Integer.toHexString(roomid);
    byte rmID = Byte.parseByte(hex1, 16);

    String hex2 = Integer.toHexString(lightid);
    byte lID = Byte.parseByte(hex2, 16);

    if (s.equals("off")){
      Byte[] message = new Byte[]{0x02, rmID, lID, 0x00, 0x0d};
    }
    else {
      Byte[] message = new Byte[]{0x02, rmID, lID, 0x01, 0x0d};
    }

    //SerialIO.write(message);
  }

  public static synchronized void setLightMode(int roomid, int lightid, Light.Mode mode){
    String s = (mode == Light.Mode.MODE_MANUAL) ? "manual" : "auto";

    String hex1 = Integer.toHexString(roomid);
    byte rmID = Byte.parseByte(hex1, 16);

    String hex2 = Integer.toHexString(lightid);
    byte lID = Byte.parseByte(hex2, 16);

    if (s.equals("manual")){
      Byte[] message = new Byte[]{0x03, rmID, lID, 0x00, 0x0d};
    }
    else {
      Byte[] message = new Byte[]{0x03, rmID, lID, 0x01, 0x0d};
    }

    //SerialIO.write(message);
  }

  public static synchronized void tempReference(int roomid, float temp){
    int vorkomma = (int) temp;

    float nach = temp * 100;
    float nach2 = nach%100;
    int nachkomma = (int) nach2;

    String hex1 = Integer.toHexString(roomid);
    Byte rmID = Byte.parseByte(hex1, 16);

    String hex2 = Integer.toHexString(vorkomma);
    Byte pre = Byte.parseByte(hex2, 16);

    String hex3 = Integer.toHexString(nachkomma);
    Byte after = Byte.parseByte(hex3, 16);

    Byte[] message = new Byte[]{0x05, rmID, pre, after, 0x0d};
    //System.out.println(Arrays.toString(message));
    //SerialIO.write(message);
  }

  public static synchronized void init()
  {
    Byte[] message = new Byte[]{0x00, 0x00, 0x00, 0x00, 0x0d};
    //SerialIO.write(message);
  }


  /**
   * This method is called by the serial-management class when an ingoing message is received.
   * @param data Received data in serialized string form
   * @see SerialIO
   */
  public static void update(Byte[] data) {
    Application.debug("processing data packet with content " + Arrays.toString(data).replace("\n", "\\n").replace("\r", "\\r"));

    if (listener == null ) {Application.debug("ERROR: LISTENER NOT AVAILABLE\r\n");}

    if (data.length != 5) {
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
            Application.debug(result);
            break;

        //temperature_reference
        case 0x05:
          float reference = data[2];
          float decimal2 = data[3];

          decimal = decimal2/100;
          result = reference + decimal2;

          listener.onTemperatureReference(data[1], result);
          break;

        default:
          Application.debug("received invalid message - no instruction matches");
          break;
      }
    }
   }
  }
