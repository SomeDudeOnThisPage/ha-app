package home.gui.elements;

import home.io.CommunicationAPI;
import home.io.SerialIO;
import home.model.Light;
import home.model.Room;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller for the room config element.
 * @author Robin Buhlmann
 */
public class RoomControl implements Initializable
{
  @FXML
  protected TabPane lightTabPane;

  @FXML
  protected TitledPane sroot;

  @FXML
  protected Slider tempRefSlider;

  @FXML
  protected Label tempRefLabel;

  @FXML
  protected Label tempValueLabel;

  /** The Room this controller is managing. */
  private Room room;

  /** List of LightControllers this manager is managing. */
  private ArrayList<LightControl> lightControllers;

  /**
   * Initializes a list of light controllers.
   * @param ignored0 ignored
   * @param ignored1 ignored
   */
  @Override
  public void initialize(URL ignored0, ResourceBundle ignored1)
  {
    this.lightControllers = new ArrayList<>();

    // controls for the GUI temperature reference slider
    // temperature reference is only outgoing!
    // meaning we do not have to update our controller after setting it!
    tempRefSlider.setOnMouseReleased(e -> {
      tempRefLabel.setText(String.format("%.2f", tempRefSlider.getValue()) + "°C");

      // make API call
      CommunicationAPI.tempReference(this.room.id(), (float) tempRefSlider.getValue());
    });
  }

  /**
   * Sets the temperature display text in the room controls.
   * @param actual temperature
   */
  public void setTemperature(float actual)
  {
    this.tempValueLabel.setText("Current Room Temperature: " + String.format("%.2f", actual) + "°C");
  }

  /**
   * Sets the temperature reference display text in the room controls.
   * @param reference temperature
   */
  public void setTemperatureReference(float reference)
  {
    this.tempRefLabel.setText(String.format("%.02f", reference) + "°C");
    this.tempRefSlider.setValue(reference);
  }

  /**
   * Returns the controls for a specific light.
   * @param id lights' ID
   * @return light object
   */
  public LightControl getLightControls(int id)
  {
    for (LightControl c : this.lightControllers)
    {
      if (c.getLight() == id)
      {
        return c;
      }
    }

    return null;
  }

  /**
   * Used for injection of room object after FXML has been loaded.
   * Also creates the list of LightControl Tabs.
   * @param room room object
   */
  public void setRoom(Room room)
  {
    this.room = room;

    // create light tabs
    ArrayList<Light> lights = this.room.getLights();

    for (Light light : lights)
    {
      try
      {
        // create new tab
        Tab tab = new Tab("Light #" + light.getID());

        // load fxml
        FXMLLoader loader = new FXMLLoader(new File("resources/fxml/elements/light_tab.fxml").toURI().toURL());
        tab.setContent(loader.load());

        // inject room into controller
        ((LightControl) loader.getController()).setLight(this.room.id(), light.getID());

        // add config panel to accordion & our list so we can access it from other systems of the application (namely the IO parts)
        this.lightControllers.add(loader.getController());
        this.lightTabPane.getTabs().add(tab);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}
