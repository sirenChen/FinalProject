/**
 * message factory, create different message type
 */
package ParkingMeterSource.MessagePack;

import android.util.Log;

public class MessageFactory {
    public static Message messageDispatch(String inMessage, byte[] orginMessage){
        // request message
        if(inMessage.substring(0,2).equals("re")) {
            Log.d("thread", String.valueOf(inMessage.length()));
            return new RequestMessage(inMessage.substring(2));
        }
        // time check message
        else if (inMessage.substring(0,2).equals("ct")) {
            return new TimeCheckMessage(inMessage, orginMessage);
        }
        // update message
        else {
            Log.d("thread", "update message");
            return new UpdateMessage(inMessage, orginMessage);
        }
    }
}
