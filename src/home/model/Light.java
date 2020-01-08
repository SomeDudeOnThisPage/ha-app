package home.model;

/**
 * It's a light.
 * The light can be either on or off, and its' mode can be either manual or automatic.
 *
 * @author Robin Buhlmann
 * @since 2019-11-22
 */
public class Light
{
  /**
   * State of a light.
   */
  public enum State { LIGHT_ON, LIGHT_OFF }

  /**
   * Mode of a light.
   */
  public enum Mode { MODE_AUTOMATIC, MODE_MANUAL }

  /**
   * State of the light.
   */
  private State state;

  /**
   * Mode of the light.
   */
  private Mode mode;

  /**
   * Position of the light on the 2D canvas.
   */
  private double[] position;

  private int id;

  /**
   * Sets the state of the light.
   * @param state state
   */
  public void setState(State state) { this.state = state; }

  /**
   * Returns the state of the light.
   * @return state
   */
  public Light.State getState() { return this.state; }

  /**
   * Sets the mode of the light.
   * @param mode mode
   */
  public void setMode(Mode mode) { this.mode = mode; }

  /**
   * Returns the mode of the light.
   * @return mode
   */
  public Mode getMode() { return this.mode; }

  /**
   * Returns the position of the light.
   * @return position
   */
  public double[] getPosition() { return this.position; }

  public int getID()
  {
    return this.id;
  }

  public void setPosition(double x, double y)
  {
    this.position[0] = x;
    this.position[1] = y;
  }

  /**
   * Creates a new Light object.
   * @param x x-position on the 2D canvas
   * @param y y-position on the 2D canvas
   */
  public Light(int id, double x, double y)
  {
    this.id = id;

    this.state = State.LIGHT_OFF;
    this.mode = Mode.MODE_AUTOMATIC;

    this.position = new double[] {x, y};
  }
}