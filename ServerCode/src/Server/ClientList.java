/**
 * store all the client
 */
package Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientList {
    private List<SocketChannel> clientList = Collections.synchronizedList(new ArrayList<SocketChannel>());
    private ByteBuffer buf = ByteBuffer.allocate(2048);

    public ClientList() {
    }

    // add a client socket
    public synchronized void add (SocketChannel socketChannel) {
        clientList.add(socketChannel);
    }

    // remove a client socket
    public synchronized void remove (SocketChannel socketChannel) {
        clientList.remove(socketChannel);
    }

    public int getClientListSize () {
        return this.clientList.size();
    }

    // push a message to all users
    public synchronized void pushBackToAllClients(byte[] msg) throws IOException {

        for (SocketChannel socketChannel:clientList) {
            if (socketChannel.isConnected()) {
                buf.put(msg);
                buf.flip();
                socketChannel.write(buf);

                buf.clear();
            }
        }
    }

    /**
     * singleton pattern
     * @return
     */
    public static ClientList getInstance () {
        return ClientListInstance.clientList;
    }

    private static class ClientListInstance {
        private static ClientList clientList = new ClientList();
    }
}
