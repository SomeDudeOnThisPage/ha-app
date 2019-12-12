package home.gui;

import home.Application;
import home.gui.elements.RoomControlPane;
import home.model.Room;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;

import java.net.URL;
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
  /** Accordion acting as our scene root. */
  @FXML private Accordion sroot;

  @Override
  public void initialize(URL ignored0, ResourceBundle ignored1)
  {
    Application.setControl(this);
  }

  public void populate()
  {
    // why the hell does every JavaFX object use different methods of representing their children????!?!?!?
    this.sroot.getPanes().removeAll();

    Room[] rooms = Application.getModel().getRooms();
    for (int i = 0; i < rooms.length; i++)
    {
      this.sroot.getPanes().add(new RoomControlPane(rooms[i], i));
    }
  }
}
