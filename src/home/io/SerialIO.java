package home.io;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import home.Application;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * @author Robin Buhlmann
 */
public class SerialIO
{
  private static boolean init = false;

  /**
   * As set in usartManager.h
   */
  private static final int USART_PACKET_SIZE = 50;

  /**
   * As set in usartManager.h
   */
  private static final int USART_BAUDRATE = 38400;

  /**
   * https://github.com/Fazecast/jSerialComm/issues/197
   */
  private static String messages = "";

  private static SerialPort current;

  /**
   * Initializes the SerialIO class.
   */
  public static synchronized void initialize()
  {
    // initialize list of ports
    SerialPort[] ports = SerialIO.getPorts();
    for (SerialPort port : ports)
    {
      Application.debug("found registered serial port \'" + port.getDescriptivePortName() + "\' at \'" + port.getSystemPortName() + "\'");
    }

    SerialIO.init = true;
  }

  public static boolean isSet()
  {
    return SerialIO.current != null;
  }

  /**
   * Sets a new port to be used by the SerialIO input / output methods.
   * The previous port is closed and the new one opened and configured.
   * @param port port to be used
   */
  public static synchronized void setPort(SerialPort port)
  {
    if (!SerialIO.init)
    {
      Application.debug("could not set new port - SerialIO was not initialized", Level.WARNING);
      return;
    }

    // cleanup previous port
    SerialIO.cleanup();

    // set new port
    SerialIO.current = port;

    if (port != null)
    {
      Application.debug("connecting to serial port \'" + port.getDescriptivePortName() + "\' at \'" + port.getSystemPortName() + "\'");

      // open the port to be able to write and read from it
      SerialIO.current.openPort();

      // set parameters as in UsartManager.h
      SerialIO.current.setComPortParameters(USART_BAUDRATE, USART_PACKET_SIZE, 1, 0);

      // the DataListener gets called everytime data is available because we set the ListeningEvent to
      // LISTENING_EVENT_DATA_AVAILABLE
      SerialIO.current.addDataListener(new SerialPortDataListener()
      {
        /**
         * Used to set the ListeningEvents the Listener should react to.
         * @return event type
         */
        @Override
        public int getListeningEvents()
        {
          return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        /**
         * Called when an event, in our case LISTENING_EVENT_DATA_AVAILABLE is triggered.
         * @param event the event with the relevant data
         */
        @Override
        public void serialEvent(SerialPortEvent event)
        {
          SerialIO.messages += new String(event.getReceivedData());

          while (SerialIO.messages.contains("\r\n"))
          {
            String[] message = messages.split("\\r\\n", 2);
            SerialIO.messages = (message.length > 1) ? message[1] : "";

            Application.debug("received data packet with size of " + message[0].length() + " bytes with content \'" + message[0] + "\'");
            CommunicationAPI.update(message[0]);
          }
        }
      });

      // enable controls in our controller again (if the user already loaded a model)
      Application.control().disableControls(false);
    }
  }

  /**
   * Returns a list of ports WITH REFRESHING THE LIST
   * @return list of serial ports
   */
  public static SerialPort[] getPorts()
  {
    return SerialPort.getCommPorts();
  }

  public static synchronized void cleanup()
  {
    if (SerialIO.current != null)
    {
      Application.debug("closing connection to serial port \'" + SerialIO.current.getDescriptivePortName() + "\' at \'" + SerialIO.current.getSystemPortName() + "\'");
      SerialIO.current.closePort();
      SerialIO.current = null;
    }
  }

  public static synchronized void write(String data)
  {
    if (!SerialIO.init)
    {
      Application.debug("could not write to port - SerialIO was not initialized", Level.WARNING);
      return;
    }

    if (SerialIO.current == null || !SerialIO.current.isOpen())
    {
      Application.debug("could not write to port - no port selected or port is closed", Level.WARNING);

      // let the user know they forgot to select a port
      Application.status("could not write to port - no port selected or port is closed");
      return;
    }

    // jSerialComm buffers internally so there's no need for us to handle that manually
    SerialIO.current.writeBytes(data.getBytes(), data.length());

    // let's not print line breaks in debug messages
    Application.debug("sent data packet with content \'" + data.replace("\n", "\\n").replace("\r", "\\r") + "\'");
  }
}
