/**
 * Use to manage all the meter and provide interfaces to control each meter
 */
package ParkingMeterSource.ParkingMeter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParkingMeterManager {
    Map<Integer, ParkingMeter> meterList = Collections.synchronizedMap(new HashMap<Integer, ParkingMeter>());

    public ParkingMeterManager() {
    }

    /**
     * Init all the meters
     */
    public void create(){
        for(int i = 1;i<=10;i++){
            meterList.put(i, new ParkingMeter(i));
        }
    }

    public void printList(){
        for (int i=1;i<=10;i++) {
            System.out.println(meterList.get(i));
        }
    }

    /**
     * get all meters array list
     * @return
     */
    public ArrayList<ParkingMeter> getArrayList () {
        ArrayList<ParkingMeter> parkingMeters = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            parkingMeters.add(meterList.get(i));
        }

        return parkingMeters;
    }

    /**
     * get available meters array list
     * @return
     */
    public ArrayList<ParkingMeter> getAvailableArrayList() {
        ArrayList<ParkingMeter> parkingMeters = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            if (meterList.get(i).getOccupy() == 'f') {
                parkingMeters.add(meterList.get(i));
            }
        }

        return parkingMeters;
    }

    /**
     * get not authorized meters array list
     * @return
     */
    public ArrayList<ParkingMeter> getNotAuthArrayList () {
        ArrayList<ParkingMeter> parkingMeters = new ArrayList<>();
        for (int i = 1; i<=10; i++) {
            if (meterList.get(i).getValid() == 'f' && meterList.get(i).getOccupy() == 't') {
                parkingMeters.add(meterList.get(i));
            }
        }

        return parkingMeters;
    }

    /**
     * add a parking meter
     * @param key
     * @param parkingMeter
     */
    public void add(Integer key,ParkingMeter parkingMeter){
        meterList.put(key,parkingMeter);
    }

    // return the how many meters
    public int getSize () {
        return meterList.size();
    }

    // print json of the meter information
    public String toJson(){
        String json = "[";
        for (int i = 1 ;i<=9;i++){
            json = json + meterList.get(i).toJSON()+",";
        }
        json = json + meterList.get(10).toJSON()+",";
        json = json+"]";

        return json;
    }


    public void updateMeterList(ParkingMeter parkingMeter){
        ParkingMeter temp = meterList.get(parkingMeter.getID());
        temp.setParkingMeter(parkingMeter);
    }


    /**
     * singleton design pattern
     * @return
     */
    public static ParkingMeterManager getInstance () {
        return ParkingMeterManagerI.pmg;
    }

    private static class ParkingMeterManagerI {
        private static ParkingMeterManager pmg = new ParkingMeterManager();
    }
}
