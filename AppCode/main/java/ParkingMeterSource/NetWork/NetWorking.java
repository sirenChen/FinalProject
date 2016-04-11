/**
 * Using java NIO to set up the network connection (TCP)
 * Reactor design pattern
 */
package ParkingMeterSource.NetWork;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import ParkingMeterSource.AllHandler.Handler;
import ParkingMeterSource.AllHandler.HandlerConnection;

/**
 * Created by root on 16-3-1.
 */
public class NetWorking implements Runnable {
    private Selector selector = null;
    private SocketChannel socketChannel = null;
    private int port = 9999;

    // get the socket channel
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public NetWorking() throws IOException {
    }

    /**
     * networking service
     * @throws IOException
     */
    public void service() throws IOException {
        //open socket
        socketChannel = SocketChannel.open();
        // non blocking socket
        socketChannel.configureBlocking(false);

        // open selector
        this.selector = Selector.open();

        // connect to the server
        socketChannel.connect(new InetSocketAddress("52.11.158.228",port));

        // register this socket channel to the selector with the connection handler
        socketChannel.register(selector, SelectionKey.OP_CONNECT,new HandlerConnection());

        while (true){
            // select selector, until something happen
            int n = selector.select();

            if(n == 0 ){
                continue;
            }

            // something happen, get the key set
            Set readkeys = selector.selectedKeys();

            /**
             * get each key, and process use the handler to process
             */
            Iterator it = readkeys.iterator();

            while(it.hasNext()){
                SelectionKey key = null;
                try {
                    key = (SelectionKey)it.next();

                    final Handler handler = (Handler)key.attachment();
                    handler.handler(key);

                    // after process, remove it
                    it.remove();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void run() {
        try {
            service();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
