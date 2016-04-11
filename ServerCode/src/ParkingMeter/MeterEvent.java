package ParkingMeter;

import java.util.EventObject;

/**
 * Created by Siren Chen
 */
public class MeterEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param parkingMeter The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public MeterEvent(ParkingMeter parkingMeter) {
        super(parkingMeter);
    }

    public ParkingMeter getParkMeter () {
        return (ParkingMeter)super.getSource();
    }
}
