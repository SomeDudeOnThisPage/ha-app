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
    String message = "setLight "+roomid+" "+lightid+" "+status;
    SerialIO.write(message);
  }

  public static synchronized void setLightMode(int roomid, int lightid, Light.Mode mode){
    String message = "setLightMode "+roomid+" "+lightid+" "+mode;
    SerialIO.write(message);
  }

  public static synchronized void tempReference(int roomid, float temp){
    String message = "temperatureRef "+roomid+" "+temp;
    SerialIO.write(message);
  }

  public static synchronized void deviceStatus(){}

  public static synchronized void initWSN(String message){
    final int[] count = {5};
    Timer timer = new Timer();
    TimerTask countdown = new TimerTask(){
      @Override
      public void run(){
        if (count[0] > 0)
          count[0]--;
        if (count[0] == 0)
          Application.debug("NO WSN FOUND");
      }
    };

    switch (message){
      case "HELLO WSN":
        SerialIO.write("HELLO WSN");
        timer.schedule(countdown,0, 1000);
      case "HELLO APPLICATION":
        countdown.cancel();
        Application.debug("HELLO APPLICATION");
    }

  }
  /**
   * This method is called by the serial-management class when an ingoing message is received.
   * @param data Received data in serialized string form
   * @see SerialIO
   */
  public static void update(String data) {
    Application.debug("processing data packet with content " + data.replace("\n", "\\n").replace("\r", "\\r"));

    if (listener == null ) {Application.debug("ERROR: LISTENER NOT AVAILABLE");}

    String[] messageSplit = data.split("\r\n");
    System.out.println(messageSplit.length + " " + messageSplit[0]);

    for (int k=0; k<messageSplit.length; k++) {
      String[] parts = messageSplit[k].split(" ");

      String instruction = parts[0];

      switch (instruction) {
        case "LIGHTSWITCH":
          /*Light.State state;
          int lightID = Integer.parseInt(parts[2]);

          if (instructionData.equals("ON\r")) {
            state = Light.State.LIGHT_ON;
            listener.onLightSwitch(roomID, lightID, state);
          } else if (instructionData.equals("OFF\r")) {
            state = Light.State.LIGHT_OFF;
            listener.onLightSwitch(roomID, lightID, state);
          }
          else
          {
            Application.debug("NO VALID MESSAGE");
          }
          break;
*/
        case "LIGHTMODE":
          /*
          Light.Mode mode;
          int lightID2 = Integer.parseInt(parts[2]);

          if (instructionData.equals("AUTO")) {
            mode = Light.Mode.MODE_AUTOMATIC;
            listener.onLightMode(roomID, lightID2, mode);
          } else if (instructionData.equals("MANUAL")) {
            mode = Light.Mode.MODE_MANUAL;
            listener.onLightMode(roomID, lightID2, mode);
          }
          break;
*/
        case "TEMPERATURE":
          if (parts.length < 3 || parts[1] == null || parts[2] == null) { break; }
          float temperature = Float.parseFloat(parts[2]);
          listener.onTemperature(Integer.parseInt(parts[1]), temperature);
          break;

        case "HELLO APPLICATION":
          /*CommunicationAPI.initWSN(data);
          break;*/

        default:
          Application.debug("received invalid message - no instruction matches");
          break;
        }
      }
    }
  }
