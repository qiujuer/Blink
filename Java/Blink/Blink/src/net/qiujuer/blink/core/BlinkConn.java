/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/16/2015
 * Changed 04/19/2015
 * Version 1.0.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.blink.core;

import net.qiujuer.blink.async.AsyncReceiveDispatcher;
import net.qiujuer.blink.async.AsyncSendDispatcher;
import net.qiujuer.blink.async.HandleSelector;
import net.qiujuer.blink.box.ByteSendPacket;
import net.qiujuer.blink.box.FileSendPacket;
import net.qiujuer.blink.box.StringSendPacket;
import net.qiujuer.blink.kit.Disposable;
import net.qiujuer.blink.listener.SendListener;

import java.io.File;

/**
 * Blink Conn
 * This class is Blink Main class
 * You sen and cancel packet use this
 */
public class BlinkConn implements Disposable {
    private final Sender mSender;
    private final Receiver mReceiver;
    private final SendDelivery mSendDelivery;
    private final ReceiveDelivery mReceiveDelivery;
    private final BlinkDelivery mBlinkDelivery;
    private final Resource mResource;
    private final BlinkParser mParser;
    private AsyncSendDispatcher mSendDispatcher;
    private AsyncReceiveDispatcher mReceiveDispatcher;

    /**
     * New BlinkConn to manager socket send and receive
     *
     * @param sender            Sender{@link Sender}
     * @param receiver          Receiver {@link Receiver}
     * @param sendDelivery      Sender delivery use notify progress {@link SendDelivery}
     * @param receiveDelivery   Receiver delivery ues notify start/run/end {@link ReceiveDelivery}
     * @param blinkDelivery     Use notify BlinkConn status {@link BlinkDelivery}
     * @param resource          Manager receive file {@link Resource}
     * @param parser            Receiver receiver parse packet {@link BlinkParser}
     * @param progressPrecision Notify send and receive progress min change value, The scope of 0~1 float value
     */
    public BlinkConn(Sender sender,
                     Receiver receiver,
                     SendDelivery sendDelivery,
                     ReceiveDelivery receiveDelivery,
                     BlinkDelivery blinkDelivery,
                     Resource resource,
                     BlinkParser parser,
                     float progressPrecision) {
        mSender = sender;
        mReceiver = receiver;
        mResource = resource;

        mSendDelivery = sendDelivery;
        mReceiveDelivery = receiveDelivery;
        mBlinkDelivery = blinkDelivery;

        mParser = parser;

        // Init this
        Init(progressPrecision);
    }

    /**
     * Starts the dispatchers in this queue.
     */
    private void Init(float progressPrecision) {
        // Create the cache dispatcher and start it.
        mSendDispatcher = new AsyncSendDispatcher(mSender, mSendDelivery, progressPrecision);

        mReceiveDispatcher = new AsyncReceiveDispatcher(mReceiver, mParser, mReceiveDelivery, progressPrecision);
    }

    /**
     * Get file resource
     *
     * @return Resource
     */
    public Resource GetResource() {
        return mResource;
    }

    /**
     * Send a Entity to queue
     *
     * @param entity SendEntity {@link SendPacket}
     * @return SendEntity
     */
    public SendPacket send(SendPacket entity) {
        entity.setBlinkConn(this);

        mSendDispatcher.send(entity);

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
     * Cancel a packet to send
     *
     * @param packet SendPacket
     */
    void cancel(SendPacket packet) {
        mSendDispatcher.cancel(packet);
    }

    /**
     * Stops the cache and network dispatchers.
     */
    @Override
    public void dispose() {
        if (mSendDelivery != null)
            mSendDelivery.dispose();

        if (mBlinkDelivery != null)
            mBlinkDelivery.dispose();

        if (mSender != null)
            mSender.dispose();

        if (mReceiver != null)
            mReceiver.dispose();

        if (mSendDispatcher != null)
            mSendDispatcher.dispose();

        if (mReceiveDispatcher != null)
            mReceiveDispatcher.dispose();

        // Try dispose HandleSelector
        HandleSelector.tryDispose();
    }
}
