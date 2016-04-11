package ParkingMeterSource.MessagePack;

/**
 * Message class
 */
public abstract class Message {
    String MSG = null;

    public Message(String MSG) {
        this.MSG = MSG;
    }

    public void process(){}
}
