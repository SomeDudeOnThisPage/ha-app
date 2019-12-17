package home.io;
import home.Application;
import home.model.House;
import home.model.Room;
import home.model.Light;
import home.model.Temperature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

// So mein Schadz ich hab mal die funktionalität der API schemenhaft implementiert dass ich damit schonmal arbeiten kann
// hab versucht alles verständlich zu dokumentieren, schau dir einfach den zusammenhang von CommunicationAPI, APIListener und
// SerialAPIListener an.

/**
 * <h1>CommunicationAPI</h1>
 * The static CommunicationAPI class is used to handle incoming and outgoing network traffic between the application and the WSN.
 * It uses an APIListener Interface set on creation to implement simple callbacks.
 *
 * @author Maxiboi
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
    // die methode hier wird in Application.java in der start-methode aufgerufen und damit wird der listener gesetzt.
    // der listener implementiert dann die methoden die du in APIListener definieren kannst.
    // wenn du ne methode in APIListener definiertst zwingst du mich damit in meiner subklasse die funktionalität von
    // der methode zu implementieren.
    CommunicationAPI.listener = listener;
  }

  // die outgoing methods können direkt in der CommunicationAPI sein, auch static!
  // wichtig is nur dass die synchronized sind falls wir threading machen!

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


  /**
   * This method is called by the serial-management class when an ingoing message is received.
   * @param data Received data in serialized string form
   * @see SerialIO
   */
  public static void update(String data) {
    Application.debug("processing data packet with content " + data.replace("\n", "\\n").replace("\r", "\\r"));

    if (listener == null ) {Application.debug("ERROR: LISTENER NOT AVAILABLE\r\n");}

    String[] messageSplit = data.split("\r\n");

    for (int k=0; k<messageSplit.length; k++) {
      String[] parts = messageSplit[k].split(" ");

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
          continue;

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
          continue;

        case "temperature":
          if (parts.length < 3 || parts[1] == null || parts[2] == null) {
            Application.debug("received message for method temperature not valid", Level.WARNING);
            break;
          }
          float temperature = Float.parseFloat(parts[2]);
          listener.onTemperature(Integer.parseInt(parts[1]), temperature);
          continue;

        case "HELLO APPLICATION":
          /*CommunicationAPI.initWSN(data);
          break;*/

        case "start_init":
          if (parts.length != 1) {
            Application.debug("not a valid operation");
            break;
          }
          listener.onStart_init();
          continue;


        case "end_init":
          if (parts.length != 1) {
            Application.debug("not a valid operation");
            break;
          }
          listener.onEnd_init();
          continue;

        default:
          Application.debug("received invalid message - no instruction matches");
        }
      }

    Application.canvas().getView().draw();
    }
  }
