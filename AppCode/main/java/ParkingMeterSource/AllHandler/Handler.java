package ParkingMeterSource.AllHandler;

import java.nio.channels.SelectionKey;

/**
 * Created by root on 16-3-1.
 */
public interface Handler {
    void handler (SelectionKey key) throws Exception;
}
