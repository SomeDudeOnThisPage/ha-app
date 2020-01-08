package home;

import home.gui.CanvasController;
import home.gui.ControlController;
import home.gui.DialogManager;
import home.gui.MainController;
import home.io.CommunicationAPI;
import home.io.SerialAPIListener;
import home.io.SerialIO;
import home.model.House;
import home.model.Light;
import home.model.Room;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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

  /** Current save path to save the model to */
  public static String SAVE;

  /** Logger. */
  private static Logger logger;

  /** The current model the application is using. */
  private static House model;

  /** Main scene controller */
  private static MainController controller;

  /** Canvas sub-scene controller */
  private static CanvasController canvas;

  /** Control sub-scene controller */
  private static ControlController control;

  /**
   * Saves the model to a given SAVE path-file. If path is null the applications' current SAVE path is used.
   * @param path path
   */
  public static synchronized void saveModel(String path)
  {
    if (path == null)
    {
      if (Application.SAVE == null) { DialogManager.error("Cannot SAVE model", "No SAVE path set."); }
      path = Application.SAVE;
    }
    Application.SAVE = path;

    // serialize JSON object
    JSONObject data = new JSONObject();
    data.put("map_size", Application.model.getSize());

    JSONArray jRooms = new JSONArray();
    for (Room room : Application.model.getRooms())
    {
      JSONObject jRoom = new JSONObject();
      JSONArray jRoomLights = new JSONArray();
      JSONArray jRoomPolygon = new JSONArray();
      jRoomPolygon.addAll(room.getIndices());

      for (Light light : room.getLights())
      {
        // light JSON data
        JSONObject jLight = new JSONObject();

        // position array
        JSONArray jLightPosition = new JSONArray();
        jLightPosition.add(light.getPosition()[0]);
        jLightPosition.add(light.getPosition()[1]);

        jLight.put("id", light.getID());
        jLight.put("position", jLightPosition);

        jRoomLights.add(jLight);
      }

      jRoom.put("alt", room.getName());
      jRoom.put("id", room.id());

      jRoom.put("indices", jRoomPolygon);
      jRoom.put("lights", jRoomLights);
      jRoom.put("managed", room.isManaged());

      jRooms.add(jRoom);
    }

    // add room list to json data
    data.put("rooms", jRooms);

    // SAVE json data
    byte[] bData = data.toJSONString().getBytes();
    try (FileOutputStream fos = new FileOutputStream(path + ".jmap"))
    {
      fos.write(bData);
    }
    catch(Exception e)
    {
      Application.debug(e.getMessage());
    }
  }

  public static synchronized void newModel(int size)
  {
    Application.model = new House(size);
  }

  /**
   * Loads a model to be used. This will trigger loading of a new model from a map.json file and a chain of requests to the WSN as values have to be reevaluated.
   * @param data JSON file containing the maps data
   */
  public static synchronized void loadModel(JSONObject data)
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
   * Evaluates command line parameters.
   * @param args command line parameters
   */
  private static void cmd(String[] args)
  {
    if (args != null)
    {
      for (int i = 0; i < args.length; /* lol */ i -= -1)
      {
        switch(args[i])
        {
          case "-debug":
            Application.DEBUG = true;
            continue;
          case "-load":
            try
            {
              Application.SAVE = args[i + 1];
              i++;
            }
            catch(Exception e)
            {
              DialogManager.error("Failed to load map", "Couldn't load map on startup as the \'-load\' parameter was set but not followed by a file path.");
            }
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
   * Sets the current canvas controller.
   * @param canvas controller
   */
  public static synchronized void setCanvas(CanvasController canvas) { Application.canvas = canvas; }

  /**
   * Sets the current control controller.
   * @param control controller
   */
  public static synchronized void setControl(ControlController control) { Application.control = control; }

  /**
   * Returns the current canvas controller.
   * @return controller
   */
  public static CanvasController canvas() { return Application.canvas; }

  /**
   * Returns the current control controller.
   * @return controller
   */
  public static ControlController control() { return Application.control; }

  /**
   * Returns the current main application UI controller.
   * @return controller
   */
  public static MainController controller() { return Application.controller; }

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
   * Sets the text of the status bar in the lower part of the application.
   * @param message message (should be serializable, so ideally use a String...)
   */
  public static void status(Object message)
  {
    Platform.runLater(() -> Application.controller.setStatus(message));
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
    FXMLLoader loader = new FXMLLoader(new File("resources/fxml/app.fxml").toURI().toURL());
    Scene scene = new Scene(loader.load());
    Application.controller = loader.getController();

    scene.getStylesheets().add("materialfx.css");
    scene.getStylesheets().add("application_default.css");

    stage.setTitle(Application.TITLE + " - no port selected");
    stage.setScene(scene);

    // load map if we have a SAVE from command line arguments (-load)
    if (Application.SAVE != null)
    {

    }

    stage.show();
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
