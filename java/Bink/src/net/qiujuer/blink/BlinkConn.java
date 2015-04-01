package net.qiujuer.blink;

import net.qiujuer.blink.box.ByteSendEntity;
import net.qiujuer.blink.box.FileSendEntity;
import net.qiujuer.blink.box.StringSendEntity;
import net.qiujuer.blink.listener.SendListener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Blink Connection from socket IOStream
 */
public class BlinkConn {
    /**
     * The queue of sends that are actually going out to the io.
     */
    private final PriorityBlockingQueue<SendEntity<?>> mSendQueue =
            new PriorityBlockingQueue<SendEntity<?>>();

    /**
     * The sender interface for processing sender requests.
     */
    private final Sender mSender;
    /**
     * For posting send responses.
     */
    private final SendDelivery mSendDelivery;

    /**
     * The sender interface for processing sender requests.
     */
    private final Receiver mReceiver;
    /**
     * For posting receive responses.
     */
    private final ReceiveDelivery mReceiveDelivery;
    /**
     * Receive Parser to create entity
     */
    private final Resource mResource;

    private SendDispatcher mSendDispatcher;
    private ReceiveDispatcher mReceiveDispatcher;

    public BlinkConn(Sender sender, SendDelivery sendDelivery, Receiver receiver, ReceiveDelivery receiveDelivery, Resource resource) {
        mSender = sender;
        mSendDelivery = sendDelivery;

        mReceiver = receiver;
        mReceiveDelivery = receiveDelivery;
        mResource = resource;

        // Init this
        init();
    }

    /**
     * Starts the dispatchers in this queue.
     */
    private void init() {
        // Create the cache dispatcher and start it.
        mSendDispatcher = new SendDispatcher(mSendQueue, mSender, mSendDelivery);
        mSendDispatcher.start();

        mReceiveDispatcher = new ReceiveDispatcher(mReceiver, mReceiveDelivery);
        mReceiveDispatcher.start();
    }

    /**
     * Stops the cache and network dispatchers.
     */
    public void destroy() {
        if (mResource != null)
            mResource.clear();

        if (mSendDispatcher != null)
            mSendDispatcher.quit();

        if (mReceiveDispatcher != null)
            mReceiveDispatcher.quit();

    }

    /**
     * Get file resource
     *
     * @return Resource
     */
    public Resource getResource() {
        return mResource;
    }

    /**
     * Send a Entity to queue
     *
     * @param entity SendEntity<T> {@link SendEntity}
     * @param <T>    Extends SendEntity
     * @return SendEntity<T>
     */
    public <T> SendEntity<T> send(SendEntity<T> entity) {
        entity.setBlinkConn(this);

        synchronized (mSendQueue) {
            mSendQueue.add(entity);
        }
        return entity;
    }

    /**
     * Send file to queue
     *
     * @param file File
     * @return FileSendEntity {@link FileSendEntity}
     */
    public FileSendEntity send(File file) {
        return send(file, null);
    }

    /**
     * Send file to queue
     *
     * @param file     File
     * @param listener Callback listener
     * @return FileSendEntity {@link FileSendEntity}
     */
    public FileSendEntity send(File file, SendListener listener) {
        FileSendEntity entity = new FileSendEntity(file, listener);
        send(entity);
        return entity;
    }

    /**
     * Send byte array to queue
     *
     * @param bytes Byte array
     * @return ByteSendEntity {@link ByteSendEntity}
     */
    public ByteSendEntity send(byte[] bytes) {
        return send(bytes, null);
    }

    /**
     * Send byte array to queue
     *
     * @param bytes    Byte array
     * @param listener Callback listener
     * @return ByteSendEntity {@link ByteSendEntity}
     */
    public ByteSendEntity send(byte[] bytes, SendListener listener) {
        ByteSendEntity entity = new ByteSendEntity(bytes, listener);
        send(entity);
        return entity;
    }

    /**
     * Send string to queue
     *
     * @param str String msg
     * @return StringSendEntity {@link StringSendEntity}
     */
    public StringSendEntity send(String str) {
        return send(str, null);
    }

    /**
     * Send string to queue
     *
     * @param str      String msg
     * @param listener Callback listener
     * @return StringSendEntity {@link StringSendEntity}
     */
    public StringSendEntity send(String str, SendListener listener) {
        StringSendEntity entity = null;
        try {
            entity = new StringSendEntity(str, listener);
            send(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return entity;
    }

    <T> void cancel(SendEntity<T> entity) {
        // Remove
        synchronized (mSendQueue) {
            mSendQueue.remove(entity);
        }
    }
}
