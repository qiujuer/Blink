package net.qiujuer.blink.core;

import android.os.Handler;
import android.os.Looper;

import net.qiujuer.blink.ExecutorDelivery;
import net.qiujuer.blink.SocketAdapter;
import net.qiujuer.blink.box.ByteSendPacket;
import net.qiujuer.blink.box.FileSendPacket;
import net.qiujuer.blink.box.StringSendPacket;
import net.qiujuer.blink.listener.ReceiveListener;
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
    private final PriorityBlockingQueue<SendPacket<?>> mSendQueue =
            new PriorityBlockingQueue<SendPacket<?>>();

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

    /**
     * SendDispatcher to send entity
     */
    private SendDispatcher mSendDispatcher;

    /**
     * ReceiveDispatcher use to receive entity
     */
    private ReceiveDispatcher mReceiveDispatcher;

    /**
     * Create a BlinkConn to IO helper with custom callback
     *
     * @param sender          Sender {@link Sender}
     * @param sendDelivery    SendDelivery {@link SendDelivery}
     * @param receiver        Receiver {@link Receiver}
     * @param receiveDelivery ReceiveDelivery {@link ReceiveDelivery}
     * @param resource        Resource {@link Resource}
     */
    public BlinkConn(Sender sender, SendDelivery sendDelivery, Receiver receiver, ReceiveDelivery receiveDelivery, Resource resource) {
        mSender = sender;
        mReceiver = receiver;
        mResource = resource;

        mSendDelivery = sendDelivery;
        mReceiveDelivery = receiveDelivery;

        // Init this
        init();
    }

    /**
     * Create a BlinkConn to IO helper with main thread callback
     *
     * @param sender   Sender {@link Sender}
     * @param receiver Receiver {@link Receiver}
     * @param resource Resource {@link Resource}
     * @param listener ReceiveListener {@link ReceiveListener}
     */
    public BlinkConn(Sender sender, Receiver receiver, Resource resource, ReceiveListener listener) {
        mSender = sender;
        mReceiver = receiver;
        mResource = resource;

        ExecutorDelivery delivery = new ExecutorDelivery(new Handler(Looper.getMainLooper()), listener);
        mSendDelivery = delivery;
        mReceiveDelivery = delivery;

        // Init this
        init();
    }

    /**
     * Create a BlinkConn to IO helper with main thread callback
     *
     * @param adapter  SocketAdapter {@link SocketAdapter} , a Socket IO
     * @param resource Resource
     * @param listener ReceiveListener
     */
    public BlinkConn(SocketAdapter adapter, Resource resource, ReceiveListener listener) {
        mSender = adapter;
        mReceiver = adapter;
        mResource = resource;

        ExecutorDelivery delivery = new ExecutorDelivery(new Handler(Looper.getMainLooper()), listener);
        mSendDelivery = delivery;
        mReceiveDelivery = delivery;

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

        if (mSender != null)
            mSender.destroySendIO();

        if (mReceiver != null)
            mReceiver.destroyReceiveIO();

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
     * @param entity SendEntity<T> {@link SendPacket}
     * @param <T>    Extends SendEntity
     * @return SendEntity<T>
     */
    public <T> SendPacket<T> send(SendPacket<T> entity) {
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
     * @return FileSendEntity {@link FileSendPacket}
     */
    public FileSendPacket send(File file) {
        return send(file, null);
    }

    /**
     * Send file to queue
     *
     * @param file     File
     * @param listener Callback listener
     * @return FileSendEntity {@link FileSendPacket}
     */
    public FileSendPacket send(File file, SendListener listener) {
        FileSendPacket entity = new FileSendPacket(file, listener);
        send(entity);
        return entity;
    }

    /**
     * Send byte array to queue
     *
     * @param bytes Byte array
     * @return ByteSendEntity {@link ByteSendPacket}
     */
    public ByteSendPacket send(byte[] bytes) {
        return send(bytes, null);
    }

    /**
     * Send byte array to queue
     *
     * @param bytes    Byte array
     * @param listener Callback listener
     * @return ByteSendEntity {@link ByteSendPacket}
     */
    public ByteSendPacket send(byte[] bytes, SendListener listener) {
        ByteSendPacket entity = new ByteSendPacket(bytes, listener);
        send(entity);
        return entity;
    }

    /**
     * Send string to queue
     *
     * @param str String msg
     * @return StringSendEntity {@link StringSendPacket}
     */
    public StringSendPacket send(String str) {
        return send(str, null);
    }

    /**
     * Send string to queue
     *
     * @param str      String msg
     * @param listener Callback listener
     * @return StringSendEntity {@link StringSendPacket}
     */
    public StringSendPacket send(String str, SendListener listener) {
        StringSendPacket entity = null;
        try {
            entity = new StringSendPacket(str, listener);
            send(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * Protected to cancel entity
     * see {@link SendPacket#cancel()}
     *
     * @param entity SendEntity
     * @param <T>    Your Entity
     */
    <T> void cancel(SendPacket<T> entity) {
        // Remove
        synchronized (mSendQueue) {
            mSendQueue.remove(entity);
        }
    }
}
