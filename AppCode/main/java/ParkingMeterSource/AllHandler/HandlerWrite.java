/**
 * user this handler to write to the socket, means send data to the server
 */
package ParkingMeterSource.AllHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HandlerWrite {
    // set up a new thread pool for them
    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private static SocketChannel socketChannel = null;

    public HandlerWrite() {
    }

    // register the socket channel
    public void registerChannel (SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * send the data to the server
     * @param send
     * @throws IOException
     */
    public static void sendToServer(String send) throws IOException {
        socketChannel.write(ByteBuffer.wrap(send.getBytes()));
    }

    /**
     * start a new thread to send a data to server
     * @param msg
     */
    public void send(String msg) {
        threadPool.submit(new SendThread(msg));
    }

    static class SendThread implements Runnable {
        String msg;

        public SendThread(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                sendToServer(msg);
            }
            catch (IOException e) {
                e.printStackTrace();
            } finally {
                Thread.currentThread().stop();
            }
        }
    }



}
