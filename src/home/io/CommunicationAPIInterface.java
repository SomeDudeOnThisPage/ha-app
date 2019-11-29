package home.io;

public abstract class CommunicationAPIInterface {
    public abstract void onLightSwitch(int roomid, int lightid, int status);
    public abstract void onLightMode(int roomid, int lightid, int status);
    public abstract void onTemperature(int roomid, float temperature);
    public abstract void onDebug(String message);
    public abstract void deviceStatus(/*Device device*/);
}
