package home.io;
import home.Application;
import home.model.House;
import home.model.Room;
import home.model.Light;
import home.model.Temperature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
  public static synchronized void setLight(int roomid, int lightid, int status){ /*serialize*/ }
  public static synchronized void setLightMode(int roomid, int lightid, int mode){ /*serialize*/ }
  public static synchronized void tempReference(int roomid, Temperature temp){ /*serialize*/ }

  /**
   * This method is called by the serial-management class when an ingoing message is received.
   * @param data Received data in serialized string form
   * @see SerialIO
   */
  public static synchronized void update(String data)
  {
    Application.debug("processing data packet with content " + data.replace("\n", "\\n").replace("\r", "\\r"));

    // stell sicher dass CommunicationAPI.initialize() gecallt wurde indem du (listener != null) checkst!!!!!!!!!

    //part with first relevant data is [5] for RoomID
    House home = Application.getModel();

    if (home == null){
        System.out.println("ERROR: NO MODEL EXISTING");
    }

    String[] parts = data.split(" ");

    if (parts[5].equals("setLightmode")){

        // was machst du hier mit dem modell???????????
        // warum??
        /*int roomID = Integer.parseInt(parts[6]);
        Room room = home.getRoom(roomID);
        Light[] lights = room.getLights();
        int LightID = Integer.parseInt(parts[7]);*/
        int mode = Integer.parseInt(parts[8]);

        if (parts[8].equals("SWITCH")) {
            // hier wäre dann: listener.onLightSwitch(...) !!!
        }

    else if (parts[5].equals("setTemperature")){

        }
    }

  }
}