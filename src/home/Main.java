package home;

import com.fazecast.jSerialComm.SerialPort;
import home.io.SerialIO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application
{
  public static void debug()
  {

  }

  @Override
  public void start(Stage stage) throws Exception
  {
    Parent root = FXMLLoader.load(getClass().getResource("/fxml/app.fxml"));
    stage.setTitle("Home Automation");
    stage.setScene(new Scene(root));
    stage.show();
  }

  /**
   * Main Application update loop
   */
  private void update()
  {

  }

  public static void main(String[] args)
  {
    SerialPort[] ports = SerialIO.getPorts();
    for (SerialPort port : ports)
    {
      System.out.println(port.getDescriptivePortName());

      // COM6 is an emulated serial port from com0com on my machine
      if (port.getSystemPortName().equals("COM7"))
      {
        SerialIO.setPort(port);

        // test write to emulated serial port
        String message = "Hello World\r\n";
        try
        {
          port.getOutputStream().write(message.getBytes());
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }

      }
    }

    launch(args);
  }
}
