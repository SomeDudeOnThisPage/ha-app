package home;

import home.io.SerialIO;
import home.model.House;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Application extends javafx.application.Application
{
  private static boolean DEBUG = false;
  private static String[] args;

  public static Stage STAGE;

  /**
   * The current model the application is using.
   */
  private static House model;

  /**
   * Sets the model to be used. This will trigger loading of a new model from a map.json file and a chain of requests to the WSN as values have to be reevaluated.
   * @param data JSON file containing the maps data
   */
  public static synchronized void setModel(File data)
  {
    // todo: clear current model (if there is one) from GUI
    try
    {
      Application.model = new House(data);
    }
    catch (Exception e)
    {
      // todo: error handling with alerts or whatever
      e.printStackTrace();
    }
  }

  // todo: probably use a real logger
  public static void debug(Object message)
  {
    if (Application.DEBUG)
    {
      System.out.println("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "]" + ":[DEBUG] " + message.toString());
    }
  }

  @Override
  public void start(Stage stage) throws Exception
  {
    Application.STAGE = stage;

    // evaluate command line parameters
    Application.cmd(Application.args);

    // log additional command line parameters
    String output = (args == null) ? "without additional parameters" : "with additional parameters: " + String.join(", ", Application.args);
    Application.debug("starting application " + output);

    // initialize serial communication
    SerialIO.initialize();

    // do some tests
    SerialIO.write("Hello World\n\r");

    // load main program stage from FXML
    Parent root = FXMLLoader.load(new File("resources/fxml/app.fxml").toURI().toURL());
    stage.setTitle("Home Automation");

    Scene scene = new Scene(root);
    scene.getStylesheets().add("/stylesheets/application_default.css");

    stage.setScene(scene);
    stage.show();
  }

  /**
   * Application Application update loop
   */
  private void update()
  {

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
          default:
            Application.debug("found illegal command line parameter \'" + args[i] + "\'");

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

  public static void main(String[] args)
  {
    // copy arguments so we can access them after we launched the app as we need to initialize the JavaFX application to show error alert dialogs
    Application.args = args;
    Application.launch(args);

    // prepare application termination
    SerialIO.cleanup();
    System.exit(0);
  }
}
