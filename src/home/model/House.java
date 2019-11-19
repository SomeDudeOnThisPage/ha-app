package home.model;

import home.Application;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

public class House
{
  private Room[] rooms;

  /**
   * Returns a room based on its' ID.
   * @see Room
   * @param id id of the room, this is determined by the place of the room in the JSON array of the map file
   * @return room
   */
  public Room getRoom(int id)
  {
    return rooms[id];
  }

  /**
   * A house contains a list of Room-Objects, loaded from a JSON data file.
   * @see Room
   * @param map JSON file containing the map data
   * @throws Exception file not found exception & JSON errors
   */
  public House(File map) throws Exception
  {
    Application.debug("creating new model from map \'" + map.getPath() + "\'");

    // load from json
    JSONObject data = (JSONObject) new JSONParser().parse(new FileReader(map));

    // create rooms
    JSONArray rooms = (JSONArray) data.get("rooms");
    this.rooms = new Room[rooms.size()];

    for (int i = 0; i < rooms.size(); i++)
    {
      JSONObject roomData = (JSONObject) rooms.get(i);
      Application.debug("creating room with data " + roomData);
      this.rooms[i] = new Room(roomData);
    }
  }
}
