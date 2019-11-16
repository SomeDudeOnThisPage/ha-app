package home.io;

import com.fazecast.jSerialComm.SerialPort;

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
      current.setComPortParameters(38400, 8, 1, 0);
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
