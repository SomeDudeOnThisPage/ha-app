package home.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Room
{
  private Light[] lights;
  private Temperature temperature;

  /**
   * Returns the rooms' Temperature object.
   * @see Temperature
   * @return temperature
   */
  public Temperature temperature() { return this.temperature; }

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
    this.lights = new Light[lights.size()];

    for (int i = 0; i < lights.size(); i++)
    {
      JSONObject light = (JSONObject) lights.get(i);
      JSONArray position = ((JSONArray) light.get("position"));
      this.lights[i] = new Light((double) position.get(0), (double) position.get(1));
    }

    this.temperature = new Temperature();
    // todo: request current temperature settings from WSN, for now just set reference and actual to zero...
  }
}
