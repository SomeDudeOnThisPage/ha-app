package home.model;

/**
 * @author Robin Buhlmann
 */
public class Light
{
  public enum State { LIGHT_ON, LIGHT_OFF }
  public enum Mode { MODE_AUTOMATIC, MODE_MANUAL }

  private State state;
  private Mode mode;

  private double[] position;

  public void setState(State state) { this.state = state; }
  public Light.State getState() { return this.state; }

  public void setMode(Mode mode) { this.mode = mode; }
  public Mode getMode() { return this.mode; }

  public double[] getPosition() { return this.position; }

  public Light(double x, double y)
  {
    this.state = State.LIGHT_OFF;
    this.mode = Mode.MODE_AUTOMATIC;

    // todo: inquire state of the light from the WSN

    this.position = new double[] {x, y};
  }
}