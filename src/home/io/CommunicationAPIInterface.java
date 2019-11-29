package home.io;

public abstract class CommunicationAPIInterface {
    public abstract void onLightSwitch(int room, int light, int status);
    public abstract void onLightMode(int room, int light, int status);
    public abstract void onTemperature(int room, float temperature);
    public abstract void onDebug(String message);
    public abstract void deviceStatus(/*Device device*/);
}
