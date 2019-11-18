package home;

public class Light
{
  public static final boolean LIGHT_MODE_AUTOMATIC = true;
  public static final boolean LIGHT_MODE_MANUAL = false;

  private boolean enabled;
  private boolean mode;

  public Light()
  {
    // todo: get this from WSN on creation
    this.enabled = false;
    this.mode = LIGHT_MODE_AUTOMATIC;
  }
}