package home.io;

import home.model.Temperature;

public abstract class CommunicationAPIInterface {
    //INGOING
    public abstract void onLightSwitch(int roomid, int lightid, int status);
    public abstract void onLightMode(int roomid, int lightid, int status);
    public abstract void onTemperature(int roomid, float temperature);
    public abstract void onDebug(String message);

    public abstract void deviceStatus(/*Device device*/);

    //OUTGOING
    public void setLight(int roomid, int lightid, int status){
        //serialize
    }
    public void setLightMode(int roomid, int lightid, int mode){
        //serialize
    }
    public void tempReference(int roomid, Temperature temp){
        //serialize
    }

}
