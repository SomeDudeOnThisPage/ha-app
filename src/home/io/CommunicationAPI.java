package home.io;
import home.Application;
import home.model.House;
import home.model.Room;
import home.model.Light;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class CommunicationAPI
{

    public static synchronized void update(String data)
  {
    Application.debug("processing data packet with content " + data.replace("\n", "\\n").replace("\r", "\\r"));
    //part with first relevant data is [5] for RoomID
    House home = Application.getModel();

    if (home == null){
        System.out.println("ERROR: NO MODEL EXISTING");
    }
    String[] parts = data.split(" ");

    if (parts[5].equals("setLightmode")){
        int roomID = Integer.parseInt(parts[6]);

        //++++ROBIN ICH KANN DIE KACK ROOMID NICHT ABRUFEN++++
        //Room room = House.getRoom(roomID);
        //Light[] lights = room.getLights();
        int LightID = Integer.parseInt(parts[7]);

        if (parts[8].equals("ON")) {

        }
        else if (parts[8].equals("OFF")){
         //   CommunicationAPIInterface.;
        }
    else if (parts[5].equals("setTemperature")){

        }
    }

  }
}