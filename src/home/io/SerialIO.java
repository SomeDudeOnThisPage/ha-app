package home.io;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import home.Application;

import java.io.IOException;

public class SerialIO
{
  /**
   * As set in usartManager.h
   */
  private static final int USART_PACKET_SIZE = 50;

  /**
   * As set in usartManager.h
   */
  private static final int USART_BAUDRATE = 38400;

  private static SerialPort current;

  public static synchronized void initialize()
  {
    // initialize list of ports
    SerialPort[] ports = SerialIO.getPorts();
    for (SerialPort port : ports)
    {
      Application.debug("found registered serial port \'" + port.getDescriptivePortName() + "\' at \'" + port.getSystemPortName() + "\'");
    }
  }

  public static synchronized void setPort(SerialPort port)
  {
    SerialIO.cleanup();

    current = port;

    if (port != null)
    {
      Application.debug("connecting to serial port \'" + port.getDescriptivePortName() + "\' at \'" + port.getSystemPortName() + "\'");

      current.openPort();
      current.setComPortParameters(USART_BAUDRATE, USART_PACKET_SIZE, 1, 0);

      current.addDataListener(new SerialPortDataListener()
      {
        @Override
        public int getListeningEvents()
        {
          return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
        }

        @Override
        public void serialEvent(SerialPortEvent event)
        {
          if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) { return; }
          if (current.bytesAvailable() == 0) { return; }

          byte[] data = new byte[current.bytesAvailable()];
          current.readBytes(data, data.length);

          Application.debug("received data packet with size of " + data.length + " bytes");

          CommunicationAPI.update(new String(data));
        }
      });
    }
  }

  public static SerialPort[] getPorts()
  {
    return SerialPort.getCommPorts();
  }

  public static synchronized void cleanup()
  {
    if (current != null)
    {
      Application.debug("closing connection to serial port \'" + current.getDescriptivePortName() + "\' at \'" + current.getSystemPortName() + "\'");
      current.closePort();
    }
  }

  public static synchronized void write(String data)
  {
    // jSerialComm buffers internally so there's no need for us to handle that manually
    try
    {
      current.getOutputStream().write(data.getBytes());

      // let's not print line breaks in debug messages
      Application.debug("sent data packet with content \'" + data.replace("\n", "\\n").replace("\r", "\\r") + "\'");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
