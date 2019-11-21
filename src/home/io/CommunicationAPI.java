package home.io;

import home.Application;

public class CommunicationAPI
{
  public static synchronized void update(String data)
  {
    String message = Application.debug("processing data packet with content " + data.replace("\n", "\\n").replace("\r", "\\r"));
    String[] parts = message.split(" ");
    //part with first relevant data is [5]
    //T = Temperature, L = Light, F = fan
    if (parts[5].charAt(0) == 'T'){
      Application.heating(parts[5].substring(1,2));
    }
    else if (parts[5].charAt(0) == 'L'){
      //Application.lights(parts[5], parts[6]);
    }
    else if (parts[5].charAt(0) == 'F'){
      //Application.fan(parts[5], parts[6]);
    }
    else
      System.out.println("no valid Data");
  }
}