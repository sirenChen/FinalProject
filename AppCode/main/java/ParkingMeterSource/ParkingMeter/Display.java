/**
 * use this class to update the UI thread
 */
package ParkingMeterSource.ParkingMeter;

import android.os.Handler;
import android.util.Log;

import java.util.LinkedList;

public class Display {
    private static LinkedList<Handler> handlers = new LinkedList<>();

    // all the UI need to update
    public static void registerhandler (Handler myHandler) {
        Log.d("thread", "register handler");
        handlers.add(myHandler);
    }

    // update meter list
    public static void updateMeterList(String meterStaute) {
        Log.d("thread", "update meter list");
        for (Handler handler : handlers) {
            handler.obtainMessage(1,meterStaute).sendToTarget();
        }
    }

    // update UI when check account balance
    public static void updateCheckTime(String remainTime) {
        for (Handler handler : handlers) {
            handler.obtainMessage(2, remainTime).sendToTarget();
        }
    }
}
