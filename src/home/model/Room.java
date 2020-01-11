package home.model;

import home.gui.DialogManager;
import home.gui.elements.FloorPlan;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * It's a room. Four walls, maybe some lights... have you never seen a room before?
 *
 * @author Robin Buhlmann
 * @since 2019-11-22
 */
public class Room
{
  /**
   * List of all lights in the room.
   */
  private ArrayList<Light> lights;

  /**
   * Temperature of the room.
   */
  private Temperature temperature;

  /**
   * Alt of the room.
   */
  private String name;

  /**
   * Numerical ID of the room.
   */
  private int id;

  private boolean managed;

  /**
   * Polygon of the room as indices. How exactly this is drawn depends on the grid of the FloorPlan used.
   * @see FloorPlan
   */
  private ArrayList<Integer> indices;

  /**
   * Returns the rooms' Temperature object.
   * @see Temperature
   * @return temperature
   */
  public Temperature temperature() { return this.temperature; }

  /**
   * Returns a rooms' polygons grid indices.
   * @return indices
   */
  public ArrayList<Integer> getIndices()
  {
    return this.indices;
  }

  /**
   * Returns a rooms' ID.
   * @return id
   */
  public int id()
  {
    return this.id;
  }

  public boolean isManaged()
  {
    return this.managed;
  }

  /**
   * Returns a specific Light in the room.
   * @param id light ID
   * @return light
   */
  public Light getLight(int id)
  {
    try
    {
      for (Light light: this.lights)
      {
        if (light.getID() == id)
        {
          return light;
        }
      }

      return null;
    }
    catch(Exception ignored)
    {
      return null;
    }
  }

  /**
   * Returns a list of all Lights in the room.
   * @return lights
   */
  public ArrayList<Light> getLights()
  {
    return this.lights;
  }

  public void addLight(Light light) throws Exception
  {
    if (this.getLight(light.getID()) != null)
    {
      DialogManager.error("cannot add light", "light with id #" + light.getID() + " already exists in room \'" + this.name + "\'.");
      throw new Exception("light with id #" + light.getID() + " already exists in room \'" + this.name + "\'.");
    }

    this.lights.add(light);
  }

  /**
   * Returns the name of the room.
   * @return name
   */
  public String getName()
  {
    return this.name;
  }

  public void setTemperature(Temperature temperature)
  {
    this.temperature = temperature;
  }

  /**
   * Constructs a room based on a name, an id, a set of indices and whether the room is managed or not.
   * @param name room name
   * @param id id
   * @param indices polygon indices
   * @param managed whether the room is managed or not
   */
  public Room(String name, int id, ArrayList<Integer> indices, boolean managed)
  {
    this.name = name;
    this.indices = indices;
    this.managed = managed;

    this.temperature = new Temperature(new double[] {0.0, 0.0}, this.name);

    this.id = id;

    this.lights = new ArrayList<>();
  }

  /**
   * Constructs a room based on a name, an id, a set of indices, whether it is managed or not and a list of lights.
   * @param name name
   * @param id id
   * @param indices list of polygon indices
   * @param managed whether the room is managed or not
   * @param lights list of lights
   */
  public Room(String name, int id, ArrayList<Integer> indices, boolean managed, ArrayList<Light> lights)
  {
    this(name, id, indices, managed);
    this.lights.addAll(lights);
  }
}
