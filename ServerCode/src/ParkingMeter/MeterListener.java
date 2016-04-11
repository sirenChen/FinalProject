package ParkingMeter;

import Server.ClientList;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Meter listener, listen to meter
 */
public interface MeterListener extends EventListener {
    void update(MeterEvent meterEvent);
}

class PushBack implements MeterListener{

    /**
     * get all the meter information, push back to user
     * @param meterEvent
     */
    @Override
    public void update(MeterEvent meterEvent) {
        byte[] time;
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);

        ParkingMeter parkingMeter = meterEvent.getParkMeter();
        long startTime = parkingMeter.getStartTime();
        byteBuffer.putLong(0, startTime);
        time = byteBuffer.array();

        // meter status
        byte[] bytes = new byte[11];
        bytes[0] = (byte) (parkingMeter.getID());
        bytes[1] = (byte) (parkingMeter.getOccupy());
        bytes[2] = (byte) (parkingMeter.getValid());

        // meter time
        for (int i = 3; i <=10; i++) {
            bytes[i] = time[i-3];
        }

        // push back
        try {
            ClientList.getInstance().pushBackToAllClients(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * use this class as the timer
 */
class TimeCount implements MeterListener {
    private ParkingMeter parkingMeter;
    Timer timer = null;
    private long startTime; //ms
    private long endTime;   //ms

    public TimeCount (ParkingMeter parkingMeter) {
        this.parkingMeter = parkingMeter;
    }

    @Override
    public void update(MeterEvent meterEvent) {
        parkingMeter = meterEvent.getParkMeter();
    }

    /**
     * start timer after user make payment
     */
    public void startTimer () {
        long remainingTime = RfidManager.getInstance().getRemainingTime(parkingMeter.getRfid());

        startTime = new Date().getTime();
        parkingMeter.setStartTime(startTime);

        timer = new Timer();
        TimerTask timerTask = new TimerExpire();
        timer.schedule(timerTask, remainingTime*1000);

        System.out.println("Listener: time start \n");
    }

    /**
     * stop timer when user is leaving
     */
    public void stopTimer () {
        if (parkingMeter.getRfid() != null) {
            endTime = new Date().getTime();

            System.out.println("Listener: total time" + (endTime - startTime) + "\n");

            updateRFID();
            parkingMeter.clearAll();
            timer.cancel();
        }
    }

    /**
     * update user's balance
     */
    public void updateRFID () {
        RfidManager.getInstance().changeTime(parkingMeter.getRfid(), (endTime - startTime)/1000);
        startTime = 0;
        endTime  =  0;
        parkingMeter.setStartTime(0L);
    }

    /**
     * set the meter's valid to 'f', when meter expired
     */
    class TimerExpire extends TimerTask {
        @Override
        public void run() {
            parkingMeter.setValid('f');
        }
    }

}

