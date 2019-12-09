package home;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

/**
 * This is the MOST basic and simple implementation of a Logger.
 * I would not use this if developing a real Application, but for this super-small-scale project it's fine...
 */
public class Logger
{
  private java.util.logging.Logger logger;

  public void log(Object message, Level level)
  {
    this.logger.log(level, message.toString());
  }

  public Logger(String name)
  {
    try
    {
      FileInputStream fis =  new FileInputStream("logging.properties");
      LogManager.getLogManager().readConfiguration(fis);

      File file = new File("logs\\" + name + ".log");
      if (!file.exists())
      { file.createNewFile(); }

      FileHandler handler = new FileHandler("logs\\" + name + ".log", 1024 * 1024, 1, true);
      this.logger = java.util.logging.Logger.getLogger(Logger.class.getName());
      this.logger.addHandler(handler);

      handler.setFormatter(new SimpleFormatter());
    }
    catch (IOException e) { e.printStackTrace(); }
  }
}
