package home.gui;

import com.fazecast.jSerialComm.SerialPort;
import home.Application;
import home.io.SerialIO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class is used to control the main application windows' elements.
 * Further, more specific functionality is outsourced to CanvasController and
 * ControlController (great name I know) in order to keep classes small and not
 * stuff five-hundred methods for UI-Control into one class.
 * This class mostly deals with the handlers for the menu elements.
 *
 * @see CanvasController
 *
 * @author Robin Buhlmann
 * @version 0.1
 * @since 2019-11-20
 */
public class MainController implements Initializable
{
  // not using nested controllers anymore as we need to access them from other parts of the application.
  // they now set themselves on initialization as static members of the Application class.
  // as we only ever have one canvas and one control subscene, this should suffice, but it isn't the cleanest way of doing things...
  // @FXML private CanvasController canvasController;
  // @FXML private ControlController controlController;

  @FXML
  private Menu menu_SelectSerialPort;

  @Override
  public void initialize(URL url, ResourceBundle resources)
  {
    this.menu_SelectSerialPort.getItems().clear();

    SerialPort[] ports = SerialIO.getPorts();

    // construct menu items for all ports and append them to menu_SelectSerialPort
    for (SerialPort port : ports)
    {
      MenuItem item = new MenuItem(port.getDescriptivePortName() + " @ " + port.getSystemPortName());
      item.setUserData(port);

      // set our current port on action handler
      item.setOnAction(e -> {
        Application.STAGE.setTitle(Application.TITLE + " @ " + ((SerialPort) item.getUserData()).getSystemPortName());
        SerialIO.setPort((SerialPort) item.getUserData());
      });

      // add the menu item to the port list
      this.menu_SelectSerialPort.getItems().add(item);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Handlers for: menu > File
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @FXML
  protected void menu_onLoadFloorPlan() throws Exception
  {
    // simple JavaFX file chooser to load our map
    DirectoryChooser selector = new DirectoryChooser();
    selector.setTitle("Select Directory...");
    selector.setInitialDirectory(new File("resources/maps"));
    final File directory = selector.showDialog(Application.STAGE);

    if (directory != null && directory.isDirectory())
    {
      // validate folder to contain necessary map files
      for (final File file : Objects.requireNonNull(directory.listFiles()))
      {
        if (!file.isDirectory())
        {
          if (file.getName().equals("map.json"))
          {
            Application.debug("creating new model from map \'" + file.getPath() + "\'");

            // load from json
            JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(file));

            // update model and controllers
            Application.setModel(json);
            Application.canvas().setView(file.getParentFile().getPath(), json);
            Application.control().populate();
            return;
          }
        }
      }

      // seems like we didn't find a map.json in the selected directory
      // create error dialog
      Application.debug("cannot load floor plan from directory \'" + directory.getPath() + "\' - no map.json file found");

      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Unable to load Floor Plan");
      alert.setContentText("Could not load floor plan from \'" + directory.getPath() + "\' - no map.json file was found.\nPlease try again with a valid directory.");
      alert.showAndWait();
    }
  }

  @FXML
  protected void menu_onQuit()
  {
    Platform.exit();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Handlers for: menu > Connection Properties
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @FXML
  protected void menu_onRefreshPortsList()
  {
    // not very clean code, but our init method only populates the ports list anyway...
    this.initialize(null, null);
  }

  @FXML
  protected void menu_onDisconnect()
  {
    SerialIO.cleanup();
    Application.STAGE.setTitle(Application.TITLE + " - no port selected");
  }
}
