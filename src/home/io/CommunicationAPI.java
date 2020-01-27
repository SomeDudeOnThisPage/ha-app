package home.io;
import home.Application;
import home.model.Light;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * <h1>CommunicationAPI</h1>
 * The static CommunicationAPI class is used to handle incoming and outgoing network traffic between the application and the WSN.
 * It uses an APIListener Interface set on creation to implement simple callbacks.
 *
 * @author Maximilian Morlock
 * @version 1.0
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


  /**
   * This outgoing method is used to switch a light to either on or off
   * @param roomid
   * @param lightid
   * @param status
   */
  public static synchronized void setLight(int roomid, int lightid, Light.State status){
    String s = (status == Light.State.LIGHT_ON) ? "on" : "off";

    //Converting integer parameters to a single byte
    String hex1 = Integer.toHexString(roomid);
    byte rmID = Byte.parseByte(hex1, 16);

    String hex2 = Integer.toHexString(lightid);
    byte lID = Byte.parseByte(hex2, 16);

    Byte[] message;

    //Filling the array depending on the given light_state
    if (s.equals("off")){
      message = new Byte[]{0x02, rmID, lID, 0x00, 0x0d};
    }
    else {
      message = new Byte[]{0x02, rmID, lID, 0x01, 0x0d};
    }
    //sending the message
    SerialIO.write(message);
  }


  /**
   * This outgoing method switches a lights' mode to manual or auto
   * @param roomid
   * @param lightid
   * @param mode
   */
  public static synchronized void setLightMode(int roomid, int lightid, Light.Mode mode){
    //same procedure as for lightState!
    String s = (mode == Light.Mode.MODE_MANUAL) ? "manual" : "auto";

    String hex1 = Integer.toHexString(roomid);
    byte rmID = Byte.parseByte(hex1, 16);

    String hex2 = Integer.toHexString(lightid);
    byte lID = Byte.parseByte(hex2, 16);

    Byte[] message;

    if (s.equals("manual")){
      message = new Byte[]{0x03, rmID, lID, 0x00, 0x0d};
    }
    else {
      message = new Byte[]{0x03, rmID, lID, 0x01, 0x0d};
    }

    SerialIO.write(message);
  }


  /**
   * this outgoing method sends a temperature reference to the WSN
   * @param roomid
   * @param temp
   */
  public static synchronized void tempReference(int roomid, float temp){
    //To get the int value for everything before the decimal point
    //the float value is simply parsed to int
    int vorkomma = (int) temp;

    //Calculation for post-decimal places
    //those need to be converted to an int value as well!
    //Multiplication by 100 "deletes" the decimal point since only 2 places are significant.
    //mod 100 calculation eliminates the pre-decimal places
    float nach = temp * 100;
    float nach2 = nach%100;
    int nachkomma = (int) nach2;

    //converting everything to byte
    String hex1 = Integer.toHexString(roomid);
    Byte rmID = Byte.parseByte(hex1, 16);

    String hex2 = Integer.toHexString(vorkomma);
    Byte pre = Byte.parseByte(hex2, 16);

    String hex3 = Integer.toHexString(nachkomma);
    Byte after = Byte.parseByte(hex3, 16);

    Byte[] message = new Byte[]{0x05, rmID, pre, after, 0x0d};
    SerialIO.write(message);
  }

  /**
   * Outgoing method for initializing WSN
   */
  public static synchronized void init()
  {
    Byte[] message = new Byte[]{0x00, 0x00, 0x00, 0x00, 0x0d};
    SerialIO.write(message);
  }


  /**
   * This method is called by the serial-management class when an ingoing message is received.
   * @param data Received data in form of a Byte[] Array
   * @see SerialIO
   */
  public static void update(Byte[] data) {
    Application.debug("processing data packet with content " + Arrays.toString(data).replace("\n", "\\n").replace("\r", "\\r"));

    if (listener == null ) {Application.debug("ERROR: LISTENER NOT AVAILABLE\r\n");}

    //checking whether the incoming message has a valid length
    if (data.length != 4) {
      Application.debug("invalid length of received data", Level.WARNING);
    }
    else {
      //The first bytes represents the operation that should be executed
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

          //depending on light_switch, the light state is set
          //the other information are given and passed to the callback functions
          if (light_switch == 0x01){
            state = Light.State.LIGHT_ON;
            //                     roomID   lightID  state
            listener.onLightSwitch(data[1], data[2], state);
          }
          else if (light_switch == 0x00){
            state = Light.State.LIGHT_OFF;
            listener.onLightSwitch(data[1], data[2], state);
          }
          break;

        //light_mode
        case 0x03:
          //same as light_state!
          byte light_mode = data[3];
          Light.Mode mode;

          if (light_mode == 0x00){
            mode = Light.Mode.MODE_MANUAL;
            //                   roomID   lightID  mode
            listener.onLightMode(data[1], data[2], mode);
          }
          else if (light_mode == 0x01){
            mode = Light.Mode.MODE_AUTOMATIC;
            listener.onLightMode(data[1], data[2], mode);
          }
          break;

        //temperature
        case 0x04:
            //pre- and post-decimal places
            float result = data[2];
            float decimal = data[3];

            //bringing decimal places to float and add them with integer places
            decimal = decimal/100;
            result = result + decimal;

            //                     roomID   temperature
            listener.onTemperature(data[1], result);
            Application.debug(result);
            break;

        //temperature_reference
        case 0x05:
          //same as temperature
          float reference = data[2];
          float decimal2 = data[3];

          decimal = decimal2/100;
          result = reference + decimal2;

          //                              roomID   temperature
          listener.onTemperatureReference(data[1], result);
          break;

        default:
          Application.debug("received invalid message - no instruction matches");
          break;
      }
    }
   }
  }
