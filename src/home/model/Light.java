package home.model;

public class Light
{
  public static final boolean LIGHT_MODE_AUTOMATIC = true;
  public static final boolean LIGHT_MODE_MANUAL = false;

  private boolean enabled;
  private boolean mode;

  private double[] position;

  public void on() { this.enabled = true; }
  public void off() { this.enabled = false; }

  public void setMode(boolean mode) { this.mode = mode; }
  public boolean getMode() { return this.mode; }

  public double[] getPosition() { return this.position; }

  public Light(double x, double y)
  {
    this.enabled = false;
    this.mode = LIGHT_MODE_AUTOMATIC;

    // todo: inquire state of the light from the WSN

    this.position = new double[] {x, y};
  }
}