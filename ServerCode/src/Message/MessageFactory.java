package Message;

import java.nio.channels.SocketChannel;

/**
 * Message Factory
 * Create different type of message
 */
public class MessageFactory {
    public Message buildMessage(String message, SocketChannel socketChannel) {

        // data length must longer than 2
        if (message.trim().length() >= 2) {
            // ADC message from ZigBee
            if (message.trim().length() == 4 && message.trim().substring(0, 2).equals("aa")) {
                return new ADCMessage(message);
            }
            // RFID message from ZigBee
            else if (message.trim().length() == 11 && (message.trim().substring(0, 2)).equals("af")) {
                return new RFIDMessage(message);
            }
            // cellphone payment message from user's APP
            else if (message.trim().substring(0, 2).equals("cp")) {
                return new CellPayMessage(message);
            }
            // balance check message from user's APP
            else if (message.trim().substring(0, 2).equals("ct")) {
                return new TimeCheckMessage(message, socketChannel);
            }
            // long live TCP connection keeper from ZigBee
            else if (message.trim().length() == 2 && message.trim().substring(0, 2).equals("rr")) {
                return new RequestMessage(message);
            }
            // require all meter status from user's APP
            else if (message.trim().length() == 2 && message.trim().substring(0, 2).equals("cc")) {
                return new ListMessage(message, socketChannel);
            }
            else {
                System.out.println("Message Factory: WRONG MSG TYPE: " + message + "\n");
                return null;
            }
        }
        System.out.println("Message Factory: MEG length less than 2" + message + "\n");
        return null;
    }
}
