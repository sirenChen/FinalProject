/**
 * message pool, all the message will add to this pool and process
 */
package ParkingMeterSource.MessagePool;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ParkingMeterSource.MessagePack.Message;

public class MessagePool {
    private List<Message> processList = Collections.synchronizedList(new LinkedList<Message>());

    public void add(Message msg){
        processList.add(msg);
    }

    public void remove(Message msg){
        processList.remove(msg);
    }

    public boolean isEmpty(){
        return processList.isEmpty();
    }

    public List<Message> getProcessList() {
        return processList;
    }

    public static MessagePool getInstance(){
        return Instance.processManager;
    }

    private static class Instance {
        private static MessagePool processManager = new MessagePool();
    }
}
