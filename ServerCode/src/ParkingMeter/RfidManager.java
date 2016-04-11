/**
 * manage all the RFID and user account
 *
 */
package ParkingMeter;

import java.util.*;

public class RfidManager {
    private Map<String,Rfid> rfidList = Collections.synchronizedMap(new HashMap<String,Rfid>());

    public void rfidInit () {
        rfidList.put("A3076074", new Rfid("A3076074", 1000));
        rfidList.put("F106F555", new Rfid("F106F555", 1000));
        rfidList.put("35BB5F74", new Rfid("35BB5F74", 10));
        rfidList.put("A0606074", new Rfid("A0606074", 10));

        rfidList.put("siren", new Rfid("siren", 10000));
        rfidList.put("xuerong", new Rfid("xuerong", 10000));

        rfidList.put("zzzzzzzz", new Rfid("zzzzzzzz", 999999999));
        rfidList.put("ssssssss", new Rfid("ssssssss", 999999999));
    }

    public RfidManager() {}

    /**
     * determine if has account or not
     * @param account
     * @return
     */
    public boolean hasAccount(String account) {
        return rfidList.containsKey(account);
    }

    /**
     * determine is the account is valid or not
     * @param rfid
     * @return
     */
    public boolean isValid (String rfid) {
        return rfidList.containsKey(rfid) && (rfidList.get(rfid).getRemainingTime() > 0);
    }

    /**
     * determine the password
     * @param rfid
     * @param password
     * @return
     */
    public boolean isValidPassword (String rfid, String password) {
        String rightPassword = rfidList.get(rfid).getPassword();

        return password.equals(rightPassword);
    }

    // change the account time
    public void changeTime (String rfid, long time) {
        rfidList.get(rfid).changeTime(time);
    }

    //get the account remaining time
    public long getRemainingTime (String rfid) {
        return rfidList.get(rfid).getRemainingTime();
    }

    /** Print section **/
    public void printList () {
        for (Rfid rfid : rfidList.values()) {
            System.out.println(rfid);
        }
    }
    /** print section end **/

    /** Singleton pattern component **/
    public static RfidManager getInstance () {
        return RfidManagerInstance.rfidList;
    }

    private static class RfidManagerInstance {
        private static RfidManager rfidList = new RfidManager();
    }
    /** single section end **/


}
