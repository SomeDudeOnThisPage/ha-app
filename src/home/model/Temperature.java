package home.model;

/**
 * @author Robin Buhlmann
 */
public class Temperature
{
  private float reference;
  private float value;

  private double[] position;

  public void setReference(float reference) { this.reference = reference; }
  public float getReference() { return this.reference; }

  public void set(float value) { this.value = value; }
  public float get() { return this.value; }

  public double[] getPosition()
  {
    return this.position;
  }

  public Temperature(float reference, float value, double[] position)
  {
    this.reference = reference;
    this.value = value;

    this.position = position;
  }

  public Temperature(double[] position)
  {
    this(0.0f, 0.0f, position);
  }
}
