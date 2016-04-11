/**
 * server return the balance of an account
 */
package ParkingMeterSource.MessagePack;

import android.util.Log;

import java.nio.ByteBuffer;

import ParkingMeterSource.ParkingMeter.Display;

public class TimeCheckMessage extends Message {
    byte[] originMessage;

    public TimeCheckMessage(String MSG, byte[] originMessage) {
        super(MSG);
        this.originMessage = originMessage;
    }

    @Override
    public void process() {
        byte[] bytes = new byte[8];
        ByteBuffer temp = ByteBuffer.allocate(8);

        /**
         * convert long type to the date and time
         */
        for (int i = 0; i < 8; i++) {
            bytes[i] = originMessage[i+2];
        }
        temp.put(bytes, 0, bytes.length);
        temp.flip();
        long remainTime = temp.getLong();

        /**
         * update the UI thread
         */
        if (remainTime == 123456789) {
            Display.updateCheckTime("RFID/UserName do not exit");
        } else {
            Display.updateCheckTime("Remaining Time: " + String.valueOf(remainTime));
        }

        Log.d("thread", "time remain: " + String.valueOf(remainTime));
    }
}
