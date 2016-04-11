package ParkingMeter;

/**
 * RFID object
 */
public class Rfid {
    private String rfid = null;
    private String password = "123";
    private long remainingTime = 0;


    public Rfid(String rfid, long remainingTime) {
        this.rfid = rfid;
        this.remainingTime = remainingTime;
    }

    public void setTime (long time) {
        this.remainingTime = time;
    }

    public void changeTime (long time) {
        this.remainingTime = remainingTime - time;
    }

    public long getRemainingTime () {
        return this.remainingTime;
    }

    public String getPassword () {
        return this.password;
    }

    @Override
    public String toString() {
        return "Rfid{" +
                "rfid='" + rfid + '\'' +
                ", remainingTime=" + remainingTime +
                '}';
    }
}
