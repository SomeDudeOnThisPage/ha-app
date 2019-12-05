package home;

import home.io.CommunicationAPI;
import home.io.SerialAPIListener;
import home.io.SerialIO;
import home.model.House;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

/**
 * "do some javadoc here you lazy shit" - me to myself.
 *
 * @author Robin Buhlmann
 * @author Maximilian Morlock (oder wie auch immer man deinen Nachnamen schreibt...)
 *
 * @version 0.1
 * @since 2019-11-18
 */
public class Application extends javafx.application.Application
{
  /** Debug flag. */
  private static boolean DEBUG = false;

  /** Log flag. */
  private static boolean LOG = true;

  /** Program arguments. */
  private static String[] args;

  /** Root stage. */
  public static Stage STAGE;

  /** Default program title. */
  public static final String TITLE = "ZigBee Home Automation";

  /** Logger. */
  private static Logger logger;

  /** The current model the application is using. */
  private static House model;

  /**
   * Sets the model to be used. This will trigger loading of a new model from a map.json file and a chain of requests to the WSN as values have to be reevaluated.
   * @param data JSON file containing the maps data
   */
  public static synchronized void setModel(JSONObject data)
  {
    try
    {
      Application.model = new House(data);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Returns the current model (or null if none is loaded).
   * @return model
   */
  public static synchronized House getModel()
  {
    return Application.model;
  }

  /**
   * Prints debug messages if the '-debug'-parameter is set, and logs them if '-nolog' has not been set.
   * @param message message (should be serializable, so ideally use a String...)
   * @param level severity for logging
   */
  public static void debug(Object message, Level level)
  {
    if (Application.DEBUG)
    {
      System.out.println("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "]" + ":[DEBUG] " + message.toString());
    }

    if (Application.LOG)
    {
      Application.logger.log(message, level);
    }
  }

  /**
   * Prints debug messages if the '-debug'-parameter is set, and logs them if '-nolog' has not been set.
   * Uses Level.INFO to log by default.
   * @param message message (should be serializable, so ideally use a String...)
   */
  public static void debug(Object message)
  {
    Application.debug(message, Level.INFO);
  }

  /**
   * Starts the JavaFX Application.
   * @param stage root stage
   * @throws Exception some JavaFX exception that shouldn't happen
   */
  @Override
  public void start(Stage stage) throws Exception
  {
    Application.STAGE = stage;

    // stage size (magic numbers, ya can't stop me...)
    stage.setMinWidth(800.0);
    stage.setMinHeight(600.0);

    // evaluate command line parameters
    Application.cmd(Application.args);

    // log additional command line parameters
    String output = (args == null) ? "without additional parameters" : "with additional parameters: " + String.join(", ", Application.args);
    Application.debug("starting application " + output);

    // initialize serial communication
    SerialIO.initialize();

    // initialize communications API
    CommunicationAPI.initialize(new SerialAPIListener());

    // load main program stage from FXML
    Parent root = FXMLLoader.load(new File("resources/fxml/app.fxml").toURI().toURL());
    stage.setTitle(Application.TITLE + " - no port selected");

    Scene scene = new Scene(root);
    scene.getStylesheets().add("/stylesheets/application_default.css");

    stage.setScene(scene);
    stage.show();
  }

  /**
   * Evaluates command line parameters.
   * @param args command line parameters
   */
  private static void cmd(String[] args)
  {
    if (args != null)
    {
      for (int i = 0; i < args.length; i++)
      {
        switch(args[i])
        {
          case "-debug":
            Application.DEBUG = true;
            continue;
          case "-load":
            // load map here based on string at args[i+1]
            i++;
          case "-nolog":
            Application.LOG = false;
            continue;
          // todo: Implement this parameter. Will be used to not require WSN-Check when port connection is established. Used for debugging when using e.g. putty
          // case "-nocheck"
          //   Application.NOCHECK = true
          //   continue;
          default:
            Application.debug("found illegal command line parameter \'" + args[i] + "\'", Level.SEVERE);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fatal Error");
            alert.setHeaderText("Invalid Command Line Parameter");
            alert.setContentText("\'" + args[i] + "\' is not a valid command line parameter.\nPlease restart the application.");
            alert.showAndWait();

            Platform.exit();
        }
      }
    }
  }

  /**
   * Initializes a logger and launches the JavaFX application.
   * @param args CLI parameters
   */
  public static void main(String[] args)
  {
    // setup logger early so we can log before / during app start!
    if (!Arrays.asList(args).contains("-nolog"))
    {
      Application.logger = new Logger(new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss").format(new Date()));
    }

    // copy arguments so we can access them after we launched the app as we need to initialize the JavaFX application to show error alert dialogs
    Application.args = args;
    Application.launch(args);

    // prepare application termination
    SerialIO.cleanup();
    System.exit(0);
  }
}
