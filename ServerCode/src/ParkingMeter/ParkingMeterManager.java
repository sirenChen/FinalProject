package ParkingMeter;

import java.util.*;

/**
 * Meter manager
 */
public class ParkingMeterManager {
    Map<Integer, ParkingMeter> meterList = Collections.synchronizedMap(new HashMap<Integer, ParkingMeter>());

    public ParkingMeterManager() {
    }

    public void meterListInit () {
        for (int i=1;i<=200;i++) {
            meterList.put(i,new ParkingMeter(i));
        }
    }

    public int getSize () {
        return meterList.size();
    }

    public ParkingMeter getMeter (int id) {
        return meterList.get(id);
    }

    public Map<Integer, ParkingMeter> getParkingMeterList () {
        return this.meterList;
    }

    /** Print Json and Print itself **/
    public String printJSON () {
        String json = "[";

        for (int i=1;i<=9;i++) {
            json = json + meterList.get(i).toJSON() + ",";
        }

        json = json + meterList.get(10).toJSON();

        json = json + "]";

        return json;
    }

    public void printList(){
        for (int i=1;i<=10;i++) {
            System.out.println(meterList.get(i).toJSON());
        }
    }
    /** print section end **/

    /** Singleton pattern component **/
    public static ParkingMeterManager getInstance () {
        return ParkingMeterManagerI.pmg;
    }

    private static class ParkingMeterManagerI {
        private static ParkingMeterManager pmg = new ParkingMeterManager();
    }
    /** singleton section end **/
}
