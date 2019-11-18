package home.io;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

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

  public static synchronized void setPort(SerialPort port)
  {
    current = port;

    if (port != null)
    {
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
          System.out.println("Received data of size: " + data.length);
          CommunicationAPI.update(new String(data));
        }
      });
    }
  }

  public static SerialPort[] getPorts()
  {
    return SerialPort.getCommPorts();
  }

  /**
   * Adds data to the buffer to be sent next.
   * @param data data to be sent
   */
  public static synchronized void send(String data)
  {
  }
}
