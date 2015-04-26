package net.qiujuer.blink;

import net.qiujuer.blink.async.AsyncFormatter;
import net.qiujuer.blink.async.AsyncParser;
import net.qiujuer.blink.async.AsyncReceiveDispatcher;
import net.qiujuer.blink.async.AsyncSendDispatcher;
import net.qiujuer.blink.async.AsyncSocketAdapter;
import net.qiujuer.blink.box.ByteSendPacket;
import net.qiujuer.blink.box.FileSendPacket;
import net.qiujuer.blink.box.StringSendPacket;
import net.qiujuer.blink.core.Connector;
import net.qiujuer.blink.core.listener.SendListener;
import net.qiujuer.blink.kit.ExecutorDelivery;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * This is Box Packet send connector
 * Use SocketChannel to sender and receiver
 */
public abstract class BlinkConnect extends Connector {
    // Default on-disk resource directory.
    private static final String DEFAULT_RESOURCE_DIR = "Blink";
    // Cache path with create file to box
    private File mCachePath = new File("C:/Blink/");
    // SocketChannel use to sender and receiver
    private SocketChannel mChannel;


    /**
     * Starts run with socket channel
     */
    protected void start(SocketChannel channel) throws IOException {
        mChannel = channel;

        ExecutorDelivery delivery = new ExecutorDelivery(null);

        setConnectDelivery(delivery);
        setReceiveDelivery(delivery);
        setSendDelivery(delivery);

        AsyncSocketAdapter socketAdapter = new AsyncSocketAdapter(mChannel, this, mConnectDelivery);
        setSender(socketAdapter);
        setReceiver(socketAdapter);

        setPacketFormatter(new AsyncFormatter());
        setPacketParser(new AsyncParser(getId(), getCachePath(), getBufferSize()));

        setSendDispatcher(new AsyncSendDispatcher(mSender, mSendDelivery, mPacketFormatter, getProgressPrecision()));
        setReceiveDispatcher(new AsyncReceiveDispatcher(mReceiver, mPacketParser, this, mReceiveDelivery));

        isClosed = false;
    }

    /**
     * Get this SocketChannel
     *
     * @return SocketChannel
     */
    public SocketChannel getSocketChannel() {
        return mChannel;
    }


    /**
     * Set connect cache path
     * The path use to create file
     *
     * @param path Cache path
     * @return BoxConnector{link BoxConnector}
     */
    public BlinkConnect setCachePath(File path) {
        if (!path.isDirectory())
            throw new IllegalArgumentException("Path isn't a directory.");
        mCachePath = new File(path, DEFAULT_RESOURCE_DIR);
        return this;
    }

    /**
     * Get Connect cache path
     *
     * @return Cache path
     */
    public File getCachePath() {
        return mCachePath;
    }

    /**
     * Send file to queue
     *
     * @param file File
     * @return FileSendPacket {@link FileSendPacket}
     */
    public FileSendPacket send(File file) {
        return send(file, null);
    }

    /**
     * Send file to queue
     *
     * @param file     File
     * @param listener Callback listener
     * @return FileSendPacket {@link FileSendPacket}
     */
    public FileSendPacket send(File file, SendListener listener) {
        if (!file.exists())
            throw new NullPointerException("Not Find: " + file.getPath());
        FileSendPacket entity = new FileSendPacket(file, listener);
        send(entity);
        return entity;
    }

    /**
     * Send byte array to queue
     *
     * @param bytes Byte array
     * @return ByteSendPacket {@link ByteSendPacket}
     */
    public ByteSendPacket send(byte[] bytes) {
        return send(bytes, null);
    }

    /**
     * Send byte array to queue
     *
     * @param bytes    Byte array
     * @param listener Callback listener
     * @return ByteSendPacket {@link ByteSendPacket}
     */
    public ByteSendPacket send(byte[] bytes, SendListener listener) {
        if (bytes == null)
            throw new NullPointerException("Send bytes can't be null.");
        ByteSendPacket entity = new ByteSendPacket(bytes, listener);
        send(entity);
        return entity;
    }

    /**
     * Send string to queue
     *
     * @param str String msg
     * @return StringSendPacket {@link StringSendPacket}
     */
    public StringSendPacket send(String str) {
        return send(str, null);
    }

    /**
     * Send string to queue
     *
     * @param str      String msg
     * @param listener Callback listener
     * @return StringSendPacket {@link StringSendPacket}
     */
    public StringSendPacket send(String str, SendListener listener) {
        if (str == null)
            throw new NullPointerException("Send string can't be null.");

        StringSendPacket entity = null;
        try {
            entity = new StringSendPacket(str, listener);
            send(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }


    /**
     * Close Channel
     */
    @Override
    public void dispose() {
        super.dispose();
        try {
            mChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
