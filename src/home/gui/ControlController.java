package home.gui;

import home.Application;
import home.gui.elements.RoomControl;
import home.io.SerialIO;
import home.model.Room;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Call me the 'Master of Class-Names'.
 * This class acts as the controller for... our control-UI. Talk about descriptive naming eh?
 *
 * @author Robin Buhlmann
 * @version 0.1
 * @since 2019-12-07
 */
public class ControlController implements Initializable
{
  /**
   * Accordion acting as our scene root.
   */
  @FXML
  private Accordion sroot;

  /**
   * List of all room control elements.
   */
  private ArrayList<RoomControl> roomControllers;

  @Override
  public void initialize(URL ignored0, ResourceBundle ignored1)
  {
    Application.setControl(this);
    this.roomControllers = new ArrayList<>();
  }

  /**
   * Returns the control pane of a room by ID.
   * @param id room ID
   * @return control panel
   */
  public RoomControl getRoomControls(int id)
  {
    return this.roomControllers.get(id);
  }

  /**
   * Disables all room controls.
   * Used when no port is set or the old one was disconnected.
   * @param state enabled (true) | disabled (false)
   */
  public void disableControls(boolean state)
  {
    for (TitledPane config : this.sroot.getPanes())
    {
      config.setDisable(state);
    }
  }

  /**
   * Cleans the current control panels and creates one for each room.
   */
  public void populate()
  {
    this.sroot.getPanes().clear();

    ArrayList<Room> rooms = Application.getModel().getRooms();
    for (Room room : rooms)
    {
      try
      {
        if (room.isManaged())
        {
          // load fxml
          FXMLLoader loader = new FXMLLoader(new File("resources/fxml/elements/room_config.fxml").toURI().toURL());
          TitledPane config = loader.load();
          config.setText(room.getName());

          // inject room into controller
          ((RoomControl) loader.getController()).setRoom(room);

          // SAVE room controller so we can access it
          this.roomControllers.add(loader.getController());

          // add config panel to accordion
          this.sroot.getPanes().add(config);
        }
      }
      catch (IOException e)
      {
        // crap
        e.printStackTrace();
      }
    }

    // disable controls if we have no port set
    this.disableControls(!SerialIO.isSet());

    if (!SerialIO.isSet())
    {
      // warn the user
      DialogManager.info("No port selected", "You need to select a port in order to control your home.\nPlease select a port under \'Connection Properties\' > \'Select Serial Port\'.");
    }
  }
}
