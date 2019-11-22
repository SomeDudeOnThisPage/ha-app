package home.io;
import home.Application;

public class CommunicationAPI
{
  //0 = automatic, 1 = manual
  private static int mode = 1;

  public static synchronized void update(String data)
  {
    Application.debug("processing data packet with content " + data.replace("\n", "\\n").replace("\r", "\\r"));

    String[] parts = data.split(" ");
    //part with first relevant data is [5]
    //T = Temperature, L = Light, F = fan

    switch (mode) {
    case 0:
        if (parts[5].charAt(0) == 'T') {
          AutomaticControl.heating(parts[5].substring(1, 2));
        }
        else if (parts[5].charAt(0) == 'L') {
          //AutomaticControl.lights(parts[5], parts[6]);
        }
        else if (parts[5].charAt(0) == 'F') {
          //AutomaticControl.fan(parts[5], parts[6]);
        }
        else
          System.out.println("no valid Data");

    case 1:
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
    }
  }
}