package home.model;

public class Light
{
  public static final int MODE_AUTOMATIC = 0;
  public static final int MODE_MANUAL = 1;

  private boolean enabled;
  private int mode;

  private double[] position;

  public void on() { this.enabled = true; }
  public void off() { this.enabled = false; }

  public void setMode(int mode) { this.mode = mode; }
  public int getMode() { return this.mode; }

  public double[] getPosition() { return this.position; }

  public Light(double x, double y)
  {
    this.enabled = false;
    this.mode = Light.MODE_AUTOMATIC;

    // todo: inquire state of the light from the WSN

    this.position = new double[] {x, y};
  }
}