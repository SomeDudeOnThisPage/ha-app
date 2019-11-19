package home.model;

public class Temperature
{
  private float reference;
  private float value;

  public void setReference(float reference) { this.reference = reference; }
  public float getReference() { return this.reference; }

  public void set(float value) { this.value = value; }
  public float get() { return this.value; }

  public Temperature(float reference, float value)
  {
    this.reference = reference;
    this.value = value;
  }

  public Temperature()
  {
    this(0.0f, 0.0f);
  }
}
