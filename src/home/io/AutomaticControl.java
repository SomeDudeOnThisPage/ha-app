package home.io;

import home.model.House;
import home.model.Room;


public class AutomaticControl {

    public void process(String data){
        String[] parts = data.split(" ");

        if (parts[6].charAt(0) == 'T') {
            AutomaticControl.heating(parts[5], parts[6].substring(1, 2));
        }
        else if (parts[6].charAt(0) == 'L') {
            //AutomaticControl.lights(parts[5], parts[6]);
        }
        else if (parts[6].charAt(0) == 'F') {
            //AutomaticControl.fan(parts[5], parts[6]);
        }
        else
            System.out.println("no valid Data");

    }

    private static void heating(String rID, String temperature){

        int r = Integer.parseInt(rID);
        Room room = House.getRoom(r);

        int t = Integer.parseInt(temperature);
        if (t <= 20){
            AutomaticControl.heatingON(room);
        }
        if (t >= 24){
            AutomaticControl.heatingOFF(room);
        }
    }

    private void lights(String lights, float brightness){

    }

    private void fan(String temperature, String faninstruction){}

    public void heatingON(Room r){
        //wsn?
    }
    public void heatingOFF(Room r){
        //wsn?
    }
}
