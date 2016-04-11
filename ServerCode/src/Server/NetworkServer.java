/**
 * set up the network connection
 */
package Server;

import Decrpty.RSADecrypt;
import Message.*;
import ParkingMeter.ParkingMeterManager;
import ParkingMeter.RfidManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NetworkServer {
    private MessageFactory messageFactory;
    public static void main (String args[]) throws Exception {
        //system init
        RSADecrypt.loadKey();
        RfidManager.getInstance().rfidInit();
        ParkingMeterManager.getInstance().meterListInit();

        System.out.println("Network Server: Server Start Good \n");

        new NetworkServer().run();
    }

    public void run () throws Exception{
        //
        messageFactory = new MessageFactory();
        //open a ServerSocketChannel and listen on the port 9999
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverChannel.socket();
        serverSocket.bind(new InetSocketAddress(9999));

        //open a selector
        Selector selector = Selector.open();

        //configure it to unblocking, then you can register this channel to the selector.
        //it will return a SelectionKey contain those two properties
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            //start select
            //from the channel you already register in this selector,
            //if some event you interest happen, it will return there.
//            System.out.println("========");
//            Iterator it1 = selector.keys().iterator();
//            while (it1.hasNext()) {
//                System.out.println(it1.next());
//            }
//            System.out.println("========");

            int n = selector.select();

            if (n == 0) {
                continue;
            }

            //get all the SelectionKey,
            //check all the SelectionKey one by one
            //key include the channel and the event you interested in
            Iterator it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();

                //if the key contain the accept event,
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();

                    System.out.println("NetWork Server: new client connected \n");

                    ClientList.getInstance().add(socketChannel);
                    registerChannel(selector,socketChannel, SelectionKey.OP_READ);
                }

                //if the key contain the read event
                if (key.isReadable()) {
                    readDataFromSocket(key);
                    //ClientList.getInstance().pushBackToAllClients();
                }

                //after process those SelectionKey, remove it and wait it happen again
                it.remove();
            }
        }

    }

    public void registerChannel (Selector selector, SelectableChannel channel, int ops) throws IOException {
        if (channel == null) {
            return;
        }

        channel.configureBlocking(false);
        channel.register(selector, ops);
    }

    public void readDataFromSocket(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        System.out.println("======MESSAGE PROCESS END======");

        try {
            int readNumber;
            ByteBuffer buf = ByteBuffer.allocate(2048);
            //SocketChannel socketChannel = (SocketChannel) key.channel();

            buf.clear();
            readNumber = socketChannel.read(buf);
            buf.flip();

            if (readNumber == -1) {
                ClientList.getInstance().remove(socketChannel);
                socketChannel.close();
                //key.cancel();
            }

                if (buf.remaining() == 0) {
                    System.out.println("Network Server: remaining == 0 \n");
                } else {
                    byte[] a = new byte[buf.remaining()];
                    buf.get(a);
                    String messageString = new String(a);

                    if (!messageString.equals("rr#")) {
                        System.out.println("Network Server -- Message Received: " + messageString + "\n");
                    }

                    /**
                     * get the message data, create the message, and process the message
                     */
                    String MSG[] = messageString.split("#",0);
                    Message message = null;

                    for (int i=0;i<MSG.length;i++) {
                        message = messageFactory.buildMessage(MSG[i], socketChannel);

                        if (message == null) {
                            System.out.println("Network Server: No Message" + "\n");
                        } else {
                            new MeterMessageProcess(message);
                        }
                    }
                }

            System.out.println("======MESSAGE PROCESS END======\n\n");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR!" + "\n");

            ClientList.getInstance().remove(socketChannel);
            System.out.println("Network Server: One Client Leave \n");
            System.out.println("Network Server: Client Size: " +
            ClientList.getInstance().getClientListSize() + "\n");

            key.cancel();
        }
    }
}
