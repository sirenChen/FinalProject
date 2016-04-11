/**
 * connection handler,
 * if connection set up, will use this to handle the process
 */
package ParkingMeterSource.AllHandler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;


public class HandlerConnection implements Handler {
    @Override
    public void handler(SelectionKey key) throws Exception {
        // get the socket channel
        SocketChannel socketChannel = (SocketChannel)key.channel();

        if(socketChannel.isConnectionPending()){
            socketChannel.finishConnect();
        }

        socketChannel.configureBlocking(false);
        HandlerRead handlerRead = new HandlerRead();

        // set up the write handler
        new HandlerWrite().registerChannel(socketChannel);

        // register read event with read handler
        socketChannel.register(key.selector(),SelectionKey.OP_READ,handlerRead);
    }
}
