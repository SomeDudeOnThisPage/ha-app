package home.model;

import home.Application;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A House contains a set of rooms.
 *
 * @author Robin Buhlmann
 * @since 2019-11-22
 */
public class House
{
  /**
   * List of all rooms in the house.
   */
  private ArrayList<Room> rooms;

  private ArrayList<TextLabel> labels;

  /**
   * Size of the map in NxN index dimensions.
   */
  private int size;

  public int getSize()
  {
    return this.size;
  }

  public ArrayList<TextLabel> getLabels()
  {
    return this.labels;
  }

  /**
   * Returns a room based on its' ID.
   * @see Room
   * @param id id of the room, this is determined by the place of the room in the JSON array of the map file
   * @return room
   */
  public Room getRoom(int id)
  {
    for (Room room : this.rooms)
    {
      if (room.id() == id)
      {
        return room;
      }
    }
    return null;
  }

  /**
   * Returns a list of all rooms.
   * @return rooms
   */
  public ArrayList<Room> getRooms()
  {
    return this.rooms;
  }

  /**
   * Adds a new room to the list.
   * @param room room
   */
  public void addRoom(Room room)
  {
    this.rooms.add(room);
  }

  /**
   * Removes a room to the list.
   * @param room room
   */
  public void removeRoom(Room room)
  {
    this.rooms.remove(room);
  }

  public void addLabel(TextLabel label)
  {
    this.labels.add(label);
  }

  /**
   * Constructor without JSON.
   */
  public House(int size)
  {
    this.rooms = new ArrayList<>();
    this.size = size;

    this.labels = new ArrayList<>();
  }
}
