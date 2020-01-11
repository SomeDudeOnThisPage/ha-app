package home.util;

import home.Application;
import home.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class JSONCoder
{
  /**
   * Encodes a model into a JSONObject.
   * @param model model
   * @return JSONObject holding the data of the model
   */
  public static synchronized JSONObject toJSON(House model)
  {
    // serialize JSON object
    JSONObject data = new JSONObject();
    data.put("map_size", model.getSize());

    JSONArray jLabels = new JSONArray();
    for (TextLabel label : model.getLabels())
    {
      JSONObject jLabel = new JSONObject();

      JSONArray position = new JSONArray();
      position.add(label.getPosition()[0]);
      position.add(label.getPosition()[1]);

      jLabel.put("position", position);
      jLabel.put("size", label.getSize());
      jLabel.put("text", label.getText());
      jLabel.put("rotation", label.getRotate());

      jLabels.add(jLabel);
    }

    JSONArray jRooms = new JSONArray();
    for (Room room : model.getRooms())
    {
      JSONObject jRoom = new JSONObject();
      JSONArray jRoomLights = new JSONArray();
      JSONArray jRoomPolygon = new JSONArray();
      JSONArray jTemperature = new JSONArray();

      jTemperature.add(room.temperature().getPosition()[0]);
      jTemperature.add(room.temperature().getPosition()[1]);

      jRoomPolygon.addAll(room.getIndices());

      for (Light light : room.getLights())
      {
        // light JSON data
        JSONObject jLight = new JSONObject();

        // position array
        JSONArray jLightPosition = new JSONArray();
        jLightPosition.add(light.getPosition()[0]);
        jLightPosition.add(light.getPosition()[1]);

        jLight.put("id", light.getID());
        jLight.put("position", jLightPosition);

        jRoomLights.add(jLight);
      }

      jRoom.put("alt", room.getName());
      jRoom.put("id", room.id());

      jRoom.put("indices", jRoomPolygon);
      jRoom.put("lights", jRoomLights);
      jRoom.put("managed", room.isManaged());

      jRoom.put("temperature", jTemperature);

      jRooms.add(jRoom);
    }

    // add room list to json data
    data.put("rooms", jRooms);
    data.put("labels", jLabels);

    return data;
  }

  /**
   * Decodes a models' JSON data and constructs a model based on it.
   * @param data JSON
   * @return model
   */
  public static House fromJSON(JSONObject data) throws Exception
  {
    House model = new House(((Long) data.get("map_size")).intValue());

    JSONArray labels = (JSONArray) data.get("labels");
    for (Object label : labels)
    {
      JSONObject jLabel = (JSONObject) label;
      JSONArray position = (JSONArray) jLabel.get("position");
      model.addLabel(new TextLabel((double) position.get(0), (double) position.get(1), (double) jLabel.get("size"), (double) jLabel.get("rotation"), (String) jLabel.get("text")));
    }

    JSONArray rooms = (JSONArray) data.get("rooms");
    for (int i = 0; i < rooms.size(); i++)
    {
      JSONObject jRoom = (JSONObject) rooms.get(i);
      Application.debug("creating room with data " + jRoom);

      // read room indices
      ArrayList<Integer> indices = new ArrayList<>();
      JSONArray polygon = (JSONArray) jRoom.get("indices");
      for (Object index : polygon)
      {
        indices.add(((Long) index).intValue());
      }

      // create room
      Room room = new Room((String) jRoom.get("alt"), ((Long) jRoom.get("id")).intValue(), indices, (boolean) jRoom.get("managed"));

      // add lights to room
      JSONArray jLights = (JSONArray) jRoom.get("lights");
      for (Object jLight : jLights)
      {
        JSONObject light = (JSONObject) jLight;
        JSONArray position = ((JSONArray) light.get("position"));
        room.addLight(new Light(((Long) light.get("id")).intValue(), (double) position.get(0), (double) position.get(1), (String) jRoom.get("alt")));
      }

      JSONArray jTemperaturePosition = (JSONArray) jRoom.get("temperature");
      room.setTemperature(new Temperature(new double[] { (double) jTemperaturePosition.get(0), (double) jTemperaturePosition.get(1) }, (String) jRoom.get("alt")));

      model.addRoom(room);
    }

    return model;
  }
}
