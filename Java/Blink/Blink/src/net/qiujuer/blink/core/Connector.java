/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/25/2015
 * Changed 04/25/2015
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

import net.qiujuer.blink.async.SelectorFactory;
import net.qiujuer.blink.core.delivery.ConnectDelivery;
import net.qiujuer.blink.core.delivery.ReceiveDelivery;
import net.qiujuer.blink.core.delivery.SendDelivery;
import net.qiujuer.blink.core.listener.ConnectListener;
import net.qiujuer.blink.core.listener.ReceiveListener;
import net.qiujuer.blink.kit.Disposable;

import java.util.UUID;

/**
 * Connector
 */
public abstract class Connector implements Disposable {
    // Default buffer size
    public static final int DEFAULT_BUFFER_SIZE = 4 * 1024 * 1024;
    // Default progress precision
    public static final float DEFAULT_PROGRESS_PRECISION = 0.001F;

    private String mId = UUID.randomUUID().toString();

    private int mBufferSize = DEFAULT_BUFFER_SIZE;
    private float mProgressPrecision = DEFAULT_PROGRESS_PRECISION;

    protected boolean isClosed = true;

    protected Sender mSender;
    protected Receiver mReceiver;

    protected ConnectListener mConnectListener;
    protected ReceiveListener mReceiveListener;

    protected SendDelivery mSendDelivery;
    protected ReceiveDelivery mReceiveDelivery;
    protected ConnectDelivery mConnectDelivery;

    protected PacketFormatter mPacketFormatter;
    protected PacketParser mPacketParser;

    protected SendDispatcher mSendDispatcher;
    protected ReceiveDispatcher mReceiveDispatcher;

    /**
     * Get the connector id
     *
     * @return UUID Id
     */
    public String getId() {
        return mId;
    }

    /**
     * Connector is closed link
     *
     * @return Is Closed
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Set socket buffer size
     *
     * @param bufferSize Socket buffer size
     * @return Connector
     */
    public Connector setBufferSize(int bufferSize) {
        mBufferSize = bufferSize;
        return this;
    }

    /**
     * Get socket buffer size
     *
     * @return Socket buffer size
     */
    public int getBufferSize() {
        return mBufferSize;
    }

    /**
     * Set notify progress's precision
     *
     * @param progressPrecision Progress's precision
     * @return Connector
     */
    public Connector setProgressPrecision(float progressPrecision) {
        this.mProgressPrecision = progressPrecision;
        return this;
    }

    /**
     * Get notify progress's precision
     *
     * @return Progress's precision
     */
    public float getProgressPrecision() {
        return mProgressPrecision;
    }

    /**
     * Set connector sender
     * The sender use send data by socket
     *
     * @param sender Sender
     * @return Connector
     */
    public Connector setSender(Sender sender) {
        mSender = sender;
        return this;
    }

    /**
     * Set connector sender
     * The Receiver use receive data by socket
     *
     * @param receiver Receiver
     * @return Connector
     */
    public Connector setReceiver(Receiver receiver) {
        mReceiver = receiver;
        return this;
    }

    /**
     * Set connector sender
     * The ConnectDelivery use deliver this connector status listener
     *
     * @param connectDelivery ConnectDelivery
     * @return Connector
     */
    public Connector setConnectDelivery(ConnectDelivery connectDelivery) {
        this.mConnectDelivery = connectDelivery;
        return this;
    }

    /**
     * Set connector sender
     * The ReceiveDelivery use deliver receive status listener
     *
     * @param receiveDelivery ReceiveDelivery
     * @return Connector
     */
    public Connector setReceiveDelivery(ReceiveDelivery receiveDelivery) {
        this.mReceiveDelivery = receiveDelivery;
        return this;
    }

    /**
     * Set connector sender
     * The SendDelivery use deliver send status listener
     *
     * @param sendDelivery SendDelivery
     * @return Connector
     */
    public Connector setSendDelivery(SendDelivery sendDelivery) {
        this.mSendDelivery = sendDelivery;
        return this;
    }

    /**
     * Set connector sender
     * The PacketFormatter use format packet to buffer
     *
     * @param packetFormatter PacketFormatter
     * @return Connector
     */
    public Connector setPacketFormatter(PacketFormatter packetFormatter) {
        this.mPacketFormatter = packetFormatter;
        return this;
    }

    /**
     * Set connector sender
     * The PacketParser use parse buffer to packet
     *
     * @param packetParser PacketParser
     * @return Connector
     */
    public Connector setPacketParser(PacketParser packetParser) {
        this.mPacketParser = packetParser;
        return this;
    }

    /**
     * Set connector sender
     * The SendDispatcher dispatch sender/buffer/packet/formatter to send data to socket.
     *
     * @param sendDispatcher SendDispatcher
     * @return Connector
     */
    public Connector setSendDispatcher(SendDispatcher sendDispatcher) {
        this.mSendDispatcher = sendDispatcher;
        return this;
    }

    /**
     * Set connector ReceiveDispatcher
     * The ReceiveDispatcher dispatch receiver/buffer/packet/parser to receive socket data.
     *
     * @param receiveDispatcher ReceiveDispatcher
     * @return Connector
     */
    public Connector setReceiveDispatcher(ReceiveDispatcher receiveDispatcher) {
        this.mReceiveDispatcher = receiveDispatcher;
        return this;
    }

    /**
     * Set connector ConnectListener
     * The ConnectListener callback connect status
     *
     * @param connectListener ConnectListener
     * @return Connector
     */
    public Connector setConnectListener(ConnectListener connectListener) {
        this.mConnectListener = connectListener;
        return this;
    }

    /**
     * Set connector ReceiveListener
     * The ReceiveListener callback receive status
     *
     * @param receiveListener ReceiveListener
     * @return Connector
     */
    public Connector setReceiveListener(ReceiveListener receiveListener) {
        this.mReceiveListener = receiveListener;
        return this;
    }

    /**
     * Send a packet to send queue
     *
     * @param packet SendEntity {@link SendPacket}
     * @return SendEntity
     */
    public SendPacket send(SendPacket packet) {
        if (mSendDispatcher == null)
            throw new NullPointerException("Connector's SendDispatcher is null.");
        packet.setDispatcher(mSendDispatcher);
        mSendDispatcher.send(packet);
        return packet;
    }

    /**
     * Cancel a packet to send
     *
     * @param packet SendPacket
     */
    void cancel(SendPacket packet) {
        SendDispatcher dispatcher = mSendDispatcher;
        if (dispatcher != null)
            dispatcher.cancel(packet);
    }

    /**
     * Delivery receive start
     *
     * @param packet ReceivePacket
     */
    public void deliveryReceiveStart(ReceivePacket packet) {
        if (mReceiveListener != null)
            mReceiveListener.onReceiveStart(this, packet);
    }

    /**
     * Delivery receive progress
     *
     * @param packet   ReceivePacket
     * @param progress Progress
     */
    public void deliveryReceiveProgress(ReceivePacket packet, float progress) {
        if (mReceiveListener != null)
            mReceiveListener.onReceiveProgress(this, packet, progress);
    }

    /**
     * Delivery receive complete
     *
     * @param packet ReceivePacket
     */
    public void deliveryReceiveCompleted(ReceivePacket packet) {
        if (mReceiveListener != null)
            mReceiveListener.onReceiveCompleted(this, packet);
    }

    /**
     * Delivery connect closed
     */
    public void deliveryConnectClosed() {
        if (mConnectListener != null)
            mConnectListener.onConnectClosed(this);
    }


    /**
     * Stops the cache and network dispatchers.
     */
    @Override
    public void dispose() {
        isClosed = true;

        if (mSender != null)
            mSender.dispose();

        if (mReceiver != null)
            mReceiver.dispose();

        if (mSendDispatcher != null)
            mSendDispatcher.dispose();

        if (mReceiveDispatcher != null)
            mReceiveDispatcher.dispose();

        // Try dispose HandleSelector
        SelectorFactory.tryDispose();
    }
}
