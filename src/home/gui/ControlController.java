package home.gui;

import home.Application;
import home.model.Room;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

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
  /** URL for our config FXML element. Initialized statically once. Done in a one-liner for convenience... */
  private static URL element_room_config; static { try { ControlController.element_room_config = new File("resources/fxml/elements/room_config.fxml").toURI().toURL(); } catch (MalformedURLException e) { e.printStackTrace(); } }

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

    for (Room room : Application.getModel().getRooms())
    {
      try
      {
        TitledPane tp = FXMLLoader.load(element_room_config);
        tp.setText(room.getName());
        this.sroot.getPanes().add(tp);
      }
      catch (IOException e)
      {
        Application.debug("could not load room_config.fxml", Level.SEVERE);
        e.printStackTrace();
      }
    }
  }
}
