package home.io;

import home.Application;

public class CommunicationAPI
{
  public static synchronized void update(String data)
  {
    Application.debug("processing data packet with content \'" + data.replace("\n", "\\n").replace("\r", "\\r") + "\'");
  }
}