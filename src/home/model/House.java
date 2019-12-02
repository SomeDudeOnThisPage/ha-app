package home.model;

import home.Application;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
    try
    {
      return this.rooms[id];
    }
    catch(Exception ignored)
    {
      return null;
    }
  }

  public Room[] getRooms()
  {
    return this.rooms;
  }

  /**
   * A house contains a list of Room-Objects, loaded from a JSON data file.
   * @see Room
   * @param data JSONObject containing the map data
   */
  public House(JSONObject data)
  {
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
