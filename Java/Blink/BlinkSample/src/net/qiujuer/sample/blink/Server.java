package net.qiujuer.sample.blink;

import net.qiujuer.blink.Blink;
import net.qiujuer.blink.core.BlinkConn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Test Server
 */
public class Server extends Thread {
    boolean isRun = true;
    ServerSocketChannel mServerSocketChannel;
    Selector mSelector;
    List<BlinkConn> mBlinkConns = new ArrayList<BlinkConn>();
    CallBack mCallBack = new CallBack();
    ExecutorService mExecutor = Executors.newSingleThreadExecutor();


    public void initServer(int port) throws IOException {
        System.out.println("Start Server...");

        mSelector = Selector.open();

        mServerSocketChannel = ServerSocketChannel.open();
        mServerSocketChannel.configureBlocking(false);
        mServerSocketChannel.socket().bind(new InetSocketAddress(port));
        mServerSocketChannel.register(mSelector, SelectionKey.OP_ACCEPT);
    }


    @Override
    public void run() {
        System.out.println("Listen...");
        try {
            while (isRun) {

                if (mSelector.select() == 0)
                    continue;

                Iterator ite = this.mSelector.selectedKeys().iterator();
                while (ite.hasNext()) {
                    SelectionKey key = (SelectionKey) ite.next();
                    ite.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();

                        System.out.println("New accept.");
                        SocketChannel channel = server.accept();

                        System.out.println("Binding to BlinkConn.");
                        BlinkConn conn = Blink.newConnection(channel,
                                2 * 1024 * 1024, "D:/Blink/", "Blink_Server",
                                mExecutor, 0.01f, mCallBack, mCallBack);

                        if (conn != null)
                            mBlinkConns.add(conn);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dispose() throws IOException {
        isRun = false;
        mSelector.wakeup();

        mServerSocketChannel.close();

        mExecutor.shutdownNow();

        for (BlinkConn conn : mBlinkConns) {
            conn.dispose();
        }
    }


    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.initServer(2626);
        server.start();

        Utils.readKey();

        server.dispose();
    }
}
