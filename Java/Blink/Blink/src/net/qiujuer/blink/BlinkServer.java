package net.qiujuer.blink;

import net.qiujuer.blink.core.Connector;
import net.qiujuer.blink.core.ExecutorDelivery;
import net.qiujuer.blink.core.listener.ConnectListener;
import net.qiujuer.blink.kit.Disposable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Blink Accept
 */
public class BlinkServer extends Thread implements Disposable {
    private final List<BlinkConnect> mConnectors = new LinkedList<BlinkConnect>();
    private boolean mRun = true;
    private ServerSocketChannel mServer;
    private Selector mSelector;
    private ExecutorService mExecutor;
    private ServerListener mServerListener;
    private ExecutorDelivery mDelivery;

    public BlinkServer(ServerListener listener) {
        mServerListener = listener;
        mExecutor = Executors.newSingleThreadExecutor();
        mDelivery = new ExecutorDelivery(mExecutor);
    }

    public void bind(int port) throws IOException {
        mSelector = Selector.open();
        mServer = ServerSocketChannel.open();
        mServer.configureBlocking(false);
        mServer.socket().bind(new InetSocketAddress(port));
        mServer.register(mSelector, SelectionKey.OP_ACCEPT);

        setName("BlinkServer-Selector-Thread");
        start();
    }

    public ServerSocketChannel getServerSocketChannel() {
        return mServer;
    }

    public List<BlinkConnect> getConnectors() {
        return mConnectors;
    }

    @Override
    public void run() {
        while (mRun) {
            try {
                if (mSelector.select() == 0)
                    continue;

                for (SelectionKey key : mSelector.selectedKeys()) {
                    mSelector.selectedKeys().remove(key);

                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();

                        SocketChannel channel = server.accept();
                        ServerConnect connect = new ServerConnect();

                        // Listener
                        onConnectCreated(connect);

                        connect.setGlobeDelivery(mDelivery);
                        connect.start(channel);

                        synchronized (mConnectors) {
                            mConnectors.add(connect);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose() {
        mRun = false;

        mSelector.wakeup();
        try {
            mSelector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mExecutor.shutdownNow();

        synchronized (mConnectors) {
            for (BlinkConnect conn : mConnectors) {
                conn.dispose();
            }
            mConnectors.clear();
        }
    }

    private void onConnectCreated(ServerConnect connect) {
        ServerListener listener = mServerListener;
        if (listener != null)
            listener.onConnectCreated(connect);
    }

    private void onConnectClosed(ServerConnect connect) {
        synchronized (mConnectors) {
            mConnectors.remove(connect);
        }
        connect.setGlobeDelivery(null);
        ServerListener listener = mServerListener;
        if (listener != null)
            listener.onConnectClosed(connect);
    }

    class ServerConnect extends BlinkConnect {
        @Override
        protected void deliveryConnectClosed() {
            onConnectClosed(this);
            super.deliveryConnectClosed();
        }

        void setGlobeDelivery(ExecutorDelivery delivery) {
            if (mConnectDelivery != null) {
                if (delivery == null && mConnectDelivery == mDelivery) {
                    mConnectDelivery = null;
                    mReceiveDelivery = null;
                    mSendDelivery = null;
                }
                return;
            }
            setDelivery(delivery);
        }
    }

    public interface ServerListener extends ConnectListener {
        void onConnectCreated(Connector connector);
    }
}
