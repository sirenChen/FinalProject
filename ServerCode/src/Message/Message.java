package Message;

import Decrpty.RSADecrypt;
import ParkingMeter.ParkingMeterManager;
import ParkingMeter.RfidManager;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * different message type
 * different message must override process function
 * main program will call each message process function
 */


public abstract class Message {
    String MSG = null;
    SocketChannel socketChannel = null;

    public Message(String MSG) {
        this.MSG = MSG;
    }

    public Message(String MSG, SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.MSG = MSG;
    }

    public abstract void messageProcess();
}

/**
 * RFID message from ZigBee
 * update RFID information
 */
class RFIDMessage extends Message{
    public RFIDMessage(String MSG) {
        super(MSG);
    }

    @Override
    public void messageProcess() {
        System.out.println("RFIDMessage :" + MSG + "\n");
        rfidProcess(MSG.charAt(2), MSG.substring(3,11));
    }

    public void rfidProcess (int id, String rfid) {
        if (RfidManager.getInstance().isValid(rfid)) {
            ParkingMeterManager.getInstance().getMeter(id).setRfid(rfid);

            RfidManager.getInstance().printList();
        } else {
            System.out.println("RFIDMessage: RFID not valid" + "\n");
        }
    }
}

/**
 * ADC message from ZigBee
 * update occupy of the meter
 */
class ADCMessage extends Message{
    public ADCMessage(String MSG) {
        super(MSG);
    }

    @Override
    public void messageProcess() {
        System.out.println("ADCMessage :" + MSG + "\n");
        adcProcess((int) MSG.charAt(2), MSG.charAt(3));
    }

    public void adcProcess (int id, char occupy) {
        ParkingMeterManager.getInstance().getMeter(id).setOccupy(occupy);
    }
}

/**
 * cellphone payment
 * do decryption
 */
class CellPayMessage extends Message {
    public CellPayMessage(String MSG) {
        super(MSG);
    }

    @Override
    public void messageProcess() {
        System.out.println("CellPayMessage :" + MSG + "\n");

        String MSGs[] = MSG.split("@", 0);

        int id = MSGs[0].charAt(2);
        String userName = MSGs[1];

        byte[] temp = RSADecrypt.parseHexStrToByte(MSGs[2]);
        String passWord = new String(RSADecrypt.decrypt(temp));

        System.out.println(passWord);

        determineValid(userName, passWord, id);
    }

    public void determineValid (String userName, String password, int id) {
        if (RfidManager.getInstance().isValid(userName)) {
            if (RfidManager.getInstance().isValidPassword(userName, password)) {
                if (ParkingMeterManager.getInstance().getMeter(id).isOccupy()) {
                    ParkingMeterManager.getInstance().getMeter(id).setRfid(userName);
                    System.out.println("CellPayMessage: cell pay good" + "\n");
                }
            } else {
                System.out.println("CellPayMessage: password bad" + "\n");
            }

        } else {
            System.out.println("CellPayMessage: user name bad" + "\n");
        }
    }
}

/**
 * cellphone APP require all the meter's status,
 * push back to the specific user
 * push back data format: json
 */
class ListMessage extends Message {
    ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

    public ListMessage(String MSG, SocketChannel socketChannel) {
        super(MSG, socketChannel);
    }


    @Override
    public void messageProcess() {
        System.out.println("ListMessage: " + MSG + "\n");
        String json = "";
        json = ParkingMeterManager.getInstance().printJSON();
        json = "re" + json;

        byteBuffer.clear();
        byteBuffer.put(json.getBytes());
        byteBuffer.flip();

        try {
            socketChannel.write(byteBuffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * balance checking from user's APP
 * push back to the specific user's APP
 */
class TimeCheckMessage extends Message {

    public TimeCheckMessage(String MSG, SocketChannel socketChannel) {
        super(MSG, socketChannel);
    }

    @Override
    public void messageProcess() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        System.out.println("Time Check Message: " + MSG + "\n");

        String MSGs[] = MSG.split("@", 0);
        String account = MSGs[1];
        long remainTime;

        if (RfidManager.getInstance().hasAccount(account)) {
            remainTime = RfidManager.getInstance().getRemainingTime(account);
        } else {
            remainTime = 123456789;
        }

        byte temp[] = {'c','t'};

        byteBuffer.clear();
        byteBuffer.put(temp);
        byteBuffer.putLong(remainTime);
        byteBuffer.flip();

        try {
            socketChannel.write(byteBuffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class RequestMessage extends Message {
    public RequestMessage(String MSG) {
        super(MSG);
    }

    @Override
    public void messageProcess() {
        //System.out.println("rr");
    }
}
