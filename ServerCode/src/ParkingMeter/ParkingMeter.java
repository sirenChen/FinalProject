package ParkingMeter;

/**
 * Meter object
 */
public class ParkingMeter {
    private int ID;
    private char occupy = 'f';
    private char valid = 'f';
    private String rfid = null;
    private long startTime = 0L;

    private MeterListener pushBack = new PushBack();
    private TimeCount timeCount = new TimeCount(this);
    private MeterEvent meterEvent = null;

    public ParkingMeter(int id) {
        this.ID = id;
        meterEvent = new MeterEvent(this);
    }

    public int getID () {
        return this.ID;
    }

    public char getOccupy () {
        return occupy;
    }

    public long getStartTime () {
        return this.startTime;
    }

    public String getRfid () {
        return this.rfid;
    }

    public char getValid() {
        return valid;
    }
    public void setStartTime (Long startTime) {
        this.startTime = startTime;
    }

    /**
     * set occupy
     * if call this function, push back to all users the new meter's status
     * @param occupy
     */
    public void setOccupy(char occupy) {
        this.occupy = occupy;

        timeCount.stopTimer();
        pushBack.update(meterEvent);
        printThisMeter();
    }

    public void setRfid (String rfid) {
        if (this.rfid == null) {
            this.rfid = rfid;
            setValid('t');
        }
    }

    /**
     * set valid
     * if call this function, push back to all users the new meter's status
     * @param valid
     */
    public void setValid(char valid) {
        this.valid = valid;

        if (valid == 't') {
            timeCount.startTimer();
        }

        pushBack.update(meterEvent);
        printThisMeter();
    }



    public boolean isOccupy () {
        if (this.occupy == 'f') {
            return false;
        } else if (this.occupy == 't') {
            return true;
        } else
            return false;
    }

    public void clearAll () {
        this.rfid = null;
        this.valid = 'f';
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

    public void printThisMeter () {
        System.out.println("Meter says: " + this);
    }

    public String toJSON() {
        return "{"
                + "\"ID\"" + ":" + this.ID + ","
                + "\"occupy\"" + ":" + "\"" + this.occupy + "\"" + ","
                + "\"valid\"" + ":" + "\"" + this.valid + "\"" + ","
                + "\"startTime\"" + ":" + "\"" + this.startTime +"\""
                + "}";
    }
}