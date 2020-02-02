package home.gui;

import com.fazecast.jSerialComm.SerialPort;
import home.Application;
import home.io.CommunicationAPI;
import home.io.SerialIO;
import home.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
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

  /**
   * Loading pane set visible when we're loading.
   */
  @FXML
  private BorderPane appLoader;

  /**
   * Main content pane.
   */
  @FXML
  private BorderPane appContent;

  /**
   * Serial port menu option.
   */
  @FXML
  private Menu menu_SelectSerialPort;

  /**
   * Room removal menu option.
   */
  @FXML
  private Menu menu_removeRoom;

  /**
   * Status label.
   */
  @FXML
  private Label status;

  /**
   * Controller init.
   * @param url ignored
   * @param resources ignored
   */
  @Override
  public void initialize(URL url, ResourceBundle resources)
  {
    this.appLoader.setVisible(false);

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

  /**
   * Populates the remove room menu with a reference to all rooms in a given model.
   * @param model model
   */
  public void menu_populateRemoveRoomMenuItem(House model)
  {
    this.menu_removeRoom.getItems().clear();

    for (Room room : model.getRooms())
    {
      MenuItem item =  new MenuItem("[" + room.id() + "]" + room.getName());
      item.setUserData(room);

      item.setOnAction(e -> {
        boolean delete = DialogManager.confirm("Delete '" + room.getName() + "'", "Are you sure you want to delete the Room '" + room.getName() + "'?\nThis cannot be undone.");
        if (delete)
        {
          Application.getModel().removeRoom((Room) item.getUserData());
          this.menu_removeRoom.getItems().remove(item);

          for (Light light : room.getLights())
          {
            Application.canvas().removeInteractable(light);
          }

          Application.canvas().removeInteractable(room.temperature());

          // repopulate controller!
          Application.control().populate();

          // redraw canvas!
          Application.canvas().getView().draw();
        }
      });

      this.menu_removeRoom.getItems().add(item);
    }
  }

  /**
   * Sets the status label.
   * @param message message
   */
  public void setStatus(Object message)
  {
    this.status.setText(message.toString());
  }

  /**
   * Makes the loading pane visible and disables the main app content.
   * @param loading loading
   */
  public void setLoading(boolean loading)
  {
    this.appLoader.setVisible(loading);
    this.appContent.setDisable(loading);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Handlers for: menu > File
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @FXML
  protected void menu_onNewFloorPlan()
  {
    Pair<String, Integer> result = DialogManager.mapificator();

    if (result == null)
    {
      return;
    }

    Application.newModel(result.getValue());
    Application.saveModel(Application.SAVE_DIRECTORY + result.getKey() + ".jmap");
    Application.canvas().populate();
    Application.control().populate();

    // refresh room list yada yada...
    this.menu_populateRemoveRoomMenuItem(Application.getModel());

    Application.status("Created a new Floor Plan!");
  }

  @FXML
  protected void menu_onLoadFloorPlan() throws Exception
  {
    // simple JavaFX file chooser to load our map
    FileChooser selector = new FileChooser();
    selector.setTitle("Select Save File...");
    selector.setInitialDirectory(new File(Application.SAVE_DIRECTORY));

    selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("jmap files (*.jmap)", "*.jmap"));

    File map = selector.showOpenDialog(Application.STAGE);

    if (map != null && !map.isDirectory())
    {
      Application.debug("creating new model from map '" + map.getPath() + "'");

      // load from json
      JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(map));

      try
      {
        // update model and controllers
        Application.loadModel(json);
        Application.canvas().populate();
        Application.control().populate();

        Application.status("Loaded model from " + map + "!");
        Application.SAVE = map.getName();

        // refresh room list yada yada...
        this.menu_populateRemoveRoomMenuItem(Application.getModel());
      }
      catch (Exception e)
      {
        DialogManager.error("Failed to load model", e.getMessage());
        Application.debug("Failed to load model: " + e.getMessage());
      }

      return;
    }

    // seems like we didn't find a map.json in the selected directory
    Application.debug("cannot load floor plan - no file or invalid file found");
    DialogManager.error("Unable to load Floor Plan", "Could not load floor plan - no file or invalid file found\nPlease try again with a valid map file.");
  }

  @FXML
  protected void menu_onSave()
  {
    if (Application.getModel() == null)
    {
      DialogManager.error("could not save map", "no map is loaded at the current time");
      return;
    }

    if (Application.SAVE == null)
    {
      // no current save path, show name selection dialog (same functionality as File > Save As)
      this.menu_onSaveAs();
    }
    else
    {
      // save on current handle
      Application.saveModel(null);
    }

    Application.status("Model saved as '" + Application.SAVE + "'!");
  }

  @FXML
  protected void menu_onSaveAs()
  {
    if (Application.getModel() == null)
    {
      DialogManager.error("could not save map", "no map is loaded at the current time");
      return;
    }

    // query new handle
    // simple JavaFX file chooser to save our map
    FileChooser selector = new FileChooser();
    selector.setTitle("Select Save File...");
    selector.setInitialDirectory(new File(Application.SAVE_DIRECTORY));

    selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("jmap files (*.jmap)", "*.jmap"));

    File save = selector.showSaveDialog(Application.STAGE);

    if (save != null)
    {
      // save on new handle
      Application.saveModel(save.getPath());
      Application.status("Model saved as '" + Application.SAVE + "'!");
    }
  }

  @FXML
  protected void menu_onQuit()
  {
    Platform.exit();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Handlers for: menu > Layout
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @FXML
  public void menu_onNewRoom()
  {
    if (Application.canvas().isDrawing())
    {
      DialogManager.error("Cannot create new room", "Please finish your current drawing first");
      return;
    }

    if (Application.getModel() != null)
    {
      try
      {
        Pair<String, Pair<Integer, Boolean>> result = DialogManager.roomificator();
        if (result != null)
        {
          // make ID = -1 if the room isn't managed
          int id = (result.getValue().getValue()) ? result.getValue().getKey() : -1;
          Application.canvas().startDraw(result.getKey(), id, result.getValue().getValue());
        }
      }
      catch(Exception e)
      {
        DialogManager.error("Could not create room", e.getMessage());
      }
    }
    else
    {
      DialogManager.error("No Floor Plan loaded", "Load a Floor Plan via 'File > Load Floor Plan' or create a new one via 'File > New Floor Plan'.");
    }
  }

  @FXML
  public void menu_onNewLight()
  {
    if (Application.getModel() != null)
    {
      int[] data = DialogManager.lightificator(Application.getModel());
      if (data == null) { return; }

      // draw light
      Application.status("[SHIFT] + [LMB] to place the light. [ENTER] once you're happy with the lights' position.");

      double pos = Application.canvas().getView().getWidth() / 2.0;

      Light light = new Light(data[1], pos, pos, Application.getModel().getRoom(data[0]).getName());
      try
      {
        Application.getModel().getRoom(data[0]).addLight(light);
        Application.canvas().addInteractable(light);
        Application.control().populate();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      DialogManager.error("No Floor Plan loaded", "Load a Floor Plan via 'File > Load Floor Plan' or create a new one via 'File > New Floor Plan'.");
    }
  }

  @FXML
  public void menu_onNewTemperature()
  {
    if (Application.getModel() != null)
    {
      double pos = Application.canvas().getView().getWidth() / 2.0;

      int roomID = DialogManager.temperatureficator(Application.getModel());

      if (roomID != -1)
      {
        Temperature temperature = Application.getModel().getRoom(roomID).temperature();

        // check if the rooms' temperature element is already added to the element pane
        for (Node element : Application.canvas().getInteractables())
        {
          if (element == temperature)
          {
            DialogManager.info("Couldn't create temperature display.", "Check if the room already has a temperature display. If so, use that or remove it first.");
            return;
          }
        }

        Application.getModel().getRoom(roomID).temperature().getPosition()[0] = pos;
        Application.getModel().getRoom(roomID).temperature().getPosition()[1] = pos;
        Application.canvas().addInteractable(temperature);
      }
      else
      {
        DialogManager.info("Couldn't create temperature display.", "Check if the room already has a temperature display. If so, use that or remove it first.");
      }
    }
    else
    {
      DialogManager.error("No Floor Plan loaded", "Load a Floor Plan via 'File > Load Floor Plan' or create a new one via 'File > New Floor Plan'.");
    }
  }

  @FXML
  public void menu_onNewLabel()
  {
    if (Application.getModel() != null)
    {
      double pos = Application.canvas().getView().getWidth() / 2.0;

      TextLabel label = new TextLabel(pos, pos, 50.0, 0.0, "[SHIFT] + [RMB] to edit...");
      Application.getModel().addLabel(label);
      Application.canvas().addInteractable(label);
    }
    else
    {
      DialogManager.error("No Floor Plan loaded", "Load a Floor Plan via 'File > Load Floor Plan' or create a new one via 'File > New Floor Plan'.");
    }
  }

  @FXML @Deprecated
  protected void menu_onRemoveLight()
  {

  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Handlers for: menu > Connection Properties
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @FXML
  protected void menu_onInitializeModel()
  {
    if (SerialIO.isSet())
    {
      CommunicationAPI.init();
      this.setLoading(true);

      // todo: 5 second timer for answer from WSN
    }
    else
    {
      DialogManager.error("no connection", "Please connect to a serial port to start initialization.");
    }
  }

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
    Application.control().disableControls(true);
  }
}