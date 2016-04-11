/**
 * Read handler, if socket is read to read, use this handler to handle the read event
 */
package ParkingMeterSource.AllHandler;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ParkingMeterSource.MessagePack.Message;
import ParkingMeterSource.MessagePack.MessageFactory;
import ParkingMeterSource.MessagePool.MessagePool;

public class HandlerRead implements Handler {
    @Override
    public void handler(SelectionKey key) throws Exception {
        int readNumer;

        // get the socket channel
        SocketChannel socketChannel = (SocketChannel)key.channel();


        /**
         * read from the socket
         */
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.clear();
        readNumer=socketChannel.read(buffer);
        buffer.flip();

        if(readNumer == -1){
            socketChannel.close();
            key.cancel();
        }

        byte[] bytes = new byte[buffer.remaining()];

        buffer.get(bytes);
        String s = new String(bytes);

        // print to the log
        Log.d("thread","read from socket: " + s);

        // create a message
        Message message = MessageFactory.messageDispatch(s, bytes);
        // add the message to the message pool
        MessagePool.getInstance().add(message);
    }
}
