/**
 * server return all the meter status, use those information to create a request message
 */
package ParkingMeterSource.MessagePack;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedList;
import java.util.List;

import ParkingMeterSource.ParkingMeter.ParkingMeter;
import ParkingMeterSource.ParkingMeter.ParkingMeterManager;

public class RequestMessage extends Message {


    public RequestMessage(String MSG) {
        super(MSG);
        Log.d("thread", "Request Message Created");
    }

    @Override
    public void process() {
        Log.d("thread", "Request Message Processed");

        /**
         * use the json to analysis the data from the server
         * get the parking meter list
         */
        Gson gson = new Gson();
        List<ParkingMeter> temp = gson.fromJson(MSG,new TypeToken<LinkedList<ParkingMeter>>(){}.getType());

        // update the parking meter list
        for (ParkingMeter parkingMeter : temp){
            ParkingMeterManager.getInstance().updateMeterList(parkingMeter);
        }

        Log.d("thread", "Request Message Process Finished");
    }
}
