/**
 * receive some meter's status,
 */
package ParkingMeterSource.MessagePack;

import android.util.Log;

import java.nio.ByteBuffer;

import ParkingMeterSource.ParkingMeter.ParkingMeter;
import ParkingMeterSource.ParkingMeter.ParkingMeterManager;

public class UpdateMessage extends Message {
    private byte[] originMessage;

    public UpdateMessage(String MSG, byte[] originMessage) {
        super(MSG);
        this.originMessage = originMessage;
        Log.d("thread", "Update Message Created");
    }

    @Override
    public void process() {
        byte[] bytes = new byte[8];
        ByteBuffer temp = ByteBuffer.allocate(8);

        /**
         * update time
         */
        for (int i = 0; i < 8; i++) {
            bytes[i] = originMessage[i+3];
        }

        temp.put(bytes, 0, bytes.length);
        temp.flip();
        long time = temp.getLong();

        // update meter
        ParkingMeter parkingMeter = new ParkingMeter((int)MSG.charAt(0),MSG.charAt(1),MSG.charAt(2), time);
        ParkingMeterManager.getInstance().updateMeterList(parkingMeter);
    }
}
