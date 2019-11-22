package home.io;

public class AutomaticControl {
    public static void heating(String temperature){
        int t = Integer.parseInt(temperature);
        if (t <= 20){
            AutomaticControl.heatingON();
        }
        if (t >= 24){
            AutomaticControl.heatingOFF();
        }
    }

    public static void lights(String lights, float brightness){

    }
    public static void fan(String temperature, String faninstruction){}

    public static void heatingON(){}
    public static void heatingOFF(){}
}
