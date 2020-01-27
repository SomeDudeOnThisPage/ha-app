package home.io;
import home.Application;
import home.model.Light;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * <h1>CommunicationAPI</h1>
 * The static CommunicationAPI class is used to handle network traffic between the application and the WSN.
 * It uses an APIListener Interface set on creation to implement simple callbacks {@link APIListener}.
 * The traffic is divided into two main parts:
 * <ul>
 *     <li>Ingoing messages</li>
 *     <li>Outgoing messages</li>
 * </ul>
 *
 * Ingoing as well as outgoing messages are represented by Byte[]-Arrays.
 * Incoming data gets processed for further usage in the application. Data that comes from the application and should
 * be sent to the WSN gets adapted to the standards of the communication. Many messages are symmetric, meaning they are both in- and outgoing!
 *
 *
 * <br>
 * To make processing as easy and uniformly as possible, it was chosen that
 * every message has a fixed length of 5bytes.
 * The general scheme for in- and outgoing arrays looks like this:
 * <br>
 *
 * Certain rules apply to messages:
 * <ul>
 *    <li>A message has a length of five bytes.</li>
 *    <li>A message starts with an opcode with a length of 1B. Available opcodes are described in detail below.</li>
 *    <li>Bytes 2-4 are data bytes used for communication. The type of data varies depending on the opcode.</li>
 *    <li>The fifth byte is a carriage-return delimiter (0x0D) used to split messages.</li>
 * </ul>
 *
 * <p>Opcodes:</p>
 * <ol>
 *     <li><b>0x00</b>: start init</li>
 *     <li><b>0x01</b>: end init</li>
 *     <li><b>0x02</b>: light_switch</li>
 *     <li><b>0x03</b>: light_mode</li>
 *     <li><b>0x04</b>: temperature</li>
 *     <li><b>0x05</b>: temperature_reference</li>
 * </ol>
 * <br><br>
 * For further details, look at the methods described in detail.
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
   * This outgoing method is used to switch a light to either on or off.
   * The parameters get converted to a single byte each. To get an int value into a single byte, it is parsed
   * to a HexString at first and then parsed to a byte. This way, the size is reduced from 32bit/4B to 8bit/1B. <br>
   * Depending on the value for "status", the corresponding
   * byte either gets the value <b>0x00</b> or <b>0x01</b>.
   * The bytes get packed into a Byte[]-Array which is then sent to the WSN.
   * @param roomid unique identifier for a room
   * @param lightid unique identifier for a light inside a room
   * @param status the current state of a light. This can either be "on" or "off"
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
   * This outgoing method switches a lights' mode to manual or auto. The functionality is identical
   * to <b>setLight</b> with the only difference being the fourth byte representing the mode rather than the state.
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

          decimal2 = decimal2/100;
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
