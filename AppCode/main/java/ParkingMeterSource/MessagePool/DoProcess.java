/**
 * if the message pool is not empty, process the message
 */
package ParkingMeterSource.MessagePool;

import android.util.Log;

import ParkingMeterSource.MessagePack.Message;

public class DoProcess implements Runnable {
    @Override
    public void run() {
        while(true){
            if (MessagePool.getInstance().isEmpty()==true){
                continue;
            }

            Log.d("thread", "Do process");

            // process the message
            for(Message msg : MessagePool.getInstance().getProcessList()){
                msg.process();
                MessagePool.getInstance().remove(msg);
            }
        }
    }
}
