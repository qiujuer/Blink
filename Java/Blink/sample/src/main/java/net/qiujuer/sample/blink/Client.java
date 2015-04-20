package net.qiujuer.sample.blink;

import net.qiujuer.blink.Blink;
import net.qiujuer.blink.core.BlinkConn;
import net.qiujuer.blink.listener.SendListener;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Blink Client
 */
public class Client {
    private Selector mSelector;
    private BlinkConn mBlinkConn;

    public void initClient(String ip, int port) throws IOException {
        System.out.println("Start Client.");

        mSelector = Selector.open();

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(ip, port));

        channel.register(mSelector, SelectionKey.OP_CONNECT);
    }

    public void connect() throws IOException {
        System.out.println("Client Connect...");
        while (true) {

            if (mSelector.select() == 0)
                continue;

            Iterator ite = this.mSelector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key = (SelectionKey) ite.next();
                ite.remove();

                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key
                            .channel();

                    // Link
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }

                    System.out.println("Client Connected.");

                    // Blink
                    mBlinkConn = Blink.newConnection(channel, null, null);
                    System.out.println("Client Bind BlinkConn.");

                    // End
                    key.interestOps(key.readyOps() & ~SelectionKey.OP_CONNECT);
                    key.cancel();

                    mSelector.close();

                    return;
                }
            }
        }
    }

    private void send() {
        if (mBlinkConn == null)
            return;
        System.out.println("Client Sending...");

        System.out.println("Send Some String.");
        for (int i = 0; i <= 50; i++) {
            mBlinkConn.send("Blink String:" + i);
        }

        System.out.println("Send Some Bytes.");
        mBlinkConn.send(new byte[]{1, 1, 0, 0});
        mBlinkConn.send(new byte[]{1, 1, 1, 0, 1});

        System.out.println("Send A File.(D:/Data.txt)");
        mBlinkConn.send(new File("D:/Data.txt"), new SendListener() {
            @Override
            public void onSendProgress(float progress) {
                System.out.println("Send File Progress:" + progress);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.initClient("localhost", 2626);
        client.connect();
        client.send();
        Utils.readKey();
        client.mBlinkConn.dispose();
    }

}
