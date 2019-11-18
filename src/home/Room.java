package home;

public class Room
{
  private Light[] lights;
  private float temperature;

  public void setTemperature(float temperature)
  {
    this.temperature = temperature;
  }

  public float getTemperature()
  {
    return this.temperature;
  }

  // todo: load rooms from files
  public Room(int lights)
  {
    this.lights = new Light[lights];
    this.temperature = 0.0f;
  }

  public Room()
  {
    this(1);
  }
}
