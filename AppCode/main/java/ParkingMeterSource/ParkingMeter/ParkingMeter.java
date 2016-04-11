/**
 * meter object
 */
package ParkingMeterSource.ParkingMeter;

import android.util.Log;

public class ParkingMeter {
    private int ID;
    private char occupy = 'f';
    private char valid = 'f';
    private String rfid = null;
    private long startTime = 0L;

    public ParkingMeter(int ID, char occupy, char valid, long startTime) {
        this.ID = ID;
        this.occupy = occupy;
        this.valid = valid;
        this.startTime = startTime;
    }

    public ParkingMeter(int ID) {
        this.ID = ID;
    }


    /*****Interface to Android**************************************/



    /**************************************************************/
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public char getOccupy() {
        return occupy;
    }

    public void setOccupy(char occupy) {
        this.occupy = occupy;
    }

    public char getValid() {
        return valid;
    }

    public void setValid(char valid) {
        this.valid = valid;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public void setTime (long time) {
        this.startTime = time;
    }

    public long getStartTime() {
        return startTime;
    }
    /***********************************************************/

    public void setParkingMeter(ParkingMeter parkingMeter){

        this.ID = parkingMeter.ID;
        this.occupy = parkingMeter.occupy;
        this.valid = parkingMeter.valid;
        this.rfid = parkingMeter.rfid;
        this.startTime = parkingMeter.startTime;

       updateAction();

        Log.d("thread", "set parking meter");
    }


    public void updateAction () {
        Display.updateMeterList(toString());
    }






    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final ParkingMeter other = (ParkingMeter) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ParkingMeter{" +
                "ID=" + ID +
                ", occupy=" + occupy +
                ", valid=" + valid +
                ", rfid=" + rfid +
                '}';
    }

    public String toJSON() {
        return "{"
                + "\"ID\"" + ":" + this.ID + ","
                + "\"occupy\"" + ":" + "\"" + this.occupy + "\"" + ","
                + "\"valid\"" + ":" + "\"" + this.valid + "\""
                + "}";
    }
}
