package home.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Robin Buhlmann
 */
public class Room
{
  private Light[] lights;
  private Temperature temperature;

  private String name;

  private int id;

  private int[] polygonialPointStructure;

  /**
   * Returns the rooms' Temperature object.
   * @see Temperature
   * @return temperature
   */
  public Temperature temperature() { return this.temperature; }

  public int[] getPolygonialPointStructure()
  {
    return this.polygonialPointStructure;
  }

  public int id()
  {
    return this.id;
  }

  public Light getLight(int id)
  {
    try
    {
      return this.lights[id];
    }
    catch(Exception ignored)
    {
      return null;
    }
  }

  public Light[] getLights()
  {
    return this.lights;
  }

  public String getName()
  {
    return this.name;
  }

  public Room(String name, int[] polygons)
  {
    this.name = name;
    this.polygonialPointStructure = polygons;

    this.lights = new Light[0];
  }

  /**
   * A room is a collection of data, namely a list of lights and a temperature.
   * @see Temperature
   * @see Light
   * @param data JSON-Data to construct the room from
   */
  public Room(JSONObject data, int id)
  {
    this.id = id;

    // get light data from JSON
    JSONArray lights = (JSONArray) data.get("lights");
    this.lights = new Light[lights.size()];

    for (int i = 0; i < lights.size(); i++)
    {
      JSONObject light = (JSONObject) lights.get(i);
      JSONArray position = ((JSONArray) light.get("position"));
      this.lights[i] = new Light((double) position.get(0), (double) position.get(1));
    }

    JSONArray tPosition = (JSONArray) data.get("temperature");
    this.temperature = new Temperature(new double[] {(double) tPosition.get(0), (double) tPosition.get(1)});

    // load polygon
    JSONArray polygon = (JSONArray) data.get("polygon");
    this.polygonialPointStructure = new int[polygon.size()];
    for (int i = 0; i < polygon.size(); i++)
    {
      this.polygonialPointStructure[i] = ((Long) polygon.get(i)).intValue();
    }

    this.name = (String) data.get("alt");
  }
}
