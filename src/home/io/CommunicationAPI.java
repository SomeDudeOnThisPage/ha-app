package home.io;
import home.Application;
import home.model.Room;


public class CommunicationAPI
{

    public static synchronized void update(String data)
  {
    Application.debug("processing data packet with content " + data.replace("\n", "\\n").replace("\r", "\\r"));

    //part with first relevant data is [5] for RoomID
    //T = Temperature, L = Light, F = fan

      //0 = automatic, 1 = manual
      int mode = 1;

      switch (mode) {
        case 0:
            AutomaticControl.process(data);
        case 1:
            ManualControl.process(data);
    }


   /*
        if (parts[5].charAt(0) == 'T') {
          ManualControl.heating();
        }
        else if (parts[5].charAt(0) == 'L') {
          ManualControl.lights();
        }
        else if (parts[5].charAt(0) == 'F') {
          ManualControl.fan();
        }
        else
          System.out.println("no valid Data");
    }*/
  }
}