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

  /**
   * Constructs a new room based on a name and a set of indices.
   * @param name room name
   * @param indices polygon indices
   */
  public Room(String name, int id, ArrayList<Integer> indices, boolean managed)
  {
    this.name = name;
    this.indices = indices;
    this.managed = managed;

    this.id = id;

    this.lights = new ArrayList<>();
  }

  /**
   * A room is a collection of data, namely a list of lights and a temperature.
   * @see Temperature
   * @see Light
   * @param data JSON-Data to construct the room from
   */
  public Room(JSONObject data)
  {
    // get light data from JSON
    JSONArray lights = (JSONArray) data.get("lights");
    this.lights = new ArrayList<>();

    for (int i = 0; i < lights.size(); i++)
    {
      JSONObject light = (JSONObject) lights.get(i);
      JSONArray position = ((JSONArray) light.get("position"));
      this.lights.add(new Light(((Long) light.get("id")).intValue(), (double) position.get(0), (double) position.get(1)));
    }

    JSONArray tPosition = (JSONArray) data.get("temperature");
    if (tPosition != null && tPosition.size() > 0)
    {
      this.temperature = new Temperature(new double[] {(double) tPosition.get(0), (double) tPosition.get(1)});
    }

    // load polygon
    JSONArray polygon = (JSONArray) data.get("indices");
    this.indices = new ArrayList<>();
    for (Object integer : polygon)
    {
      this.indices.add(((Long) integer).intValue());
    }

    this.name = (String) data.get("alt");
    this.managed = Boolean.valueOf((String) data.get("managed"));
    this.id = (this.managed) ? ((Long) data.get("id")).intValue() : -1;
  }
}
