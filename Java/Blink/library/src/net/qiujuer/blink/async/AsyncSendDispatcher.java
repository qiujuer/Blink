/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/16/2015
 * Changed 04/26/2015
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
package net.qiujuer.blink.async;

import net.qiujuer.blink.BlinkClient;
import net.qiujuer.blink.core.PacketFilter;
import net.qiujuer.blink.core.PacketFormatter;
import net.qiujuer.blink.core.SendDispatcher;
import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.core.Sender;
import net.qiujuer.blink.core.delivery.SendDelivery;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides for performing send dispatch from a queue of BinkConn {@link BlinkClient}.
 */
public class AsyncSendDispatcher extends AsyncDispatcher implements SendDispatcher {
    // The queue of send entity.
    private final Queue<SendPacket> mQueue;
    // The sender interface for processing sender requests.
    private Sender mSender;
    // Posting send responses.
    private SendDelivery mDelivery;
    // Used for telling us to die.
    private final AtomicBoolean mSending = new AtomicBoolean(false);
    // Formatter packet to args buffer
    private PacketFormatter mFormatter;
    // Send packet
    private SendPacket mPacket;


    public AsyncSendDispatcher(Sender sender, SendDelivery delivery, PacketFormatter formatter, float progressPrecision) {
        // Set Buffer
        super(sender.getSendBufferSize(), progressPrecision);

        mQueue = new ConcurrentLinkedQueue<SendPacket>();
        mSender = sender;
        mDelivery = delivery;

        mFormatter = formatter;
        mFormatter.setEventArgs(this);
    }

    /**
     * Add a packet to queue to send
     *
     * @param packet SendPacket
     */
    @Override
    public void send(SendPacket packet) {
        synchronized (mQueue) {
            mQueue.offer(packet);
        }

        if (mSending.compareAndSet(false, true)) {
            Thread thread = new Thread("Blink-AsyncSendDispatcher-StartSendThread") {
                @Override
                public void run() {
                    sendPacket();
                }
            };
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Delete a packet from queue to cancel
     *
     * @param packet SendPacket
     */
    @Override
    public void cancel(SendPacket packet) {
        synchronized (mQueue) {
            mQueue.remove(packet);
        }
    }

    /**
     * Notify send progress
     */
    private void notifyProgress(float progress) {
        SendDelivery delivery = mDelivery;
        SendPacket packet = mPacket;

        if (!mDisposed.get()
                && delivery != null
                && packet != null
                && isNotifyProgress(progress)) {
            if (progress == PacketFilter.STATUS_START)
                delivery.postSendStart(packet);
            else if (progress == PacketFilter.STATUS_END)
                delivery.postSendCompleted(packet);
            else
                delivery.postSendProgress(packet, progress);
        }
    }


    /**
     * Send packet by format to buffer
     */
    private void sendPacket() {
        float progress = mFormatter.format();
        if (progress == PacketFilter.STATUS_NEED) {
            SendPacket packet = takePacket();
            mFormatter.setPacket(packet);
            if (packet != null) {
                sendPacket();
            }
        } else {
            notifyProgress(progress);

            // Next
            if (progress == PacketFilter.STATUS_END)
                mFormatter.setPacket(takePacket());

            sendAsync();
        }
    }

    /**
     * Send next packet
     */
    private SendPacket takePacket() {
        // Check
        mSending.set(!mQueue.isEmpty());
        if (mSending.get()) {
            // Pool a request from the queue.
            SendPacket packet = mQueue.poll();

            // Cancel
            if (packet.isCanceled()) {
                return takePacket();
            } else {
                mPacket = packet;
            }
        } else {
            mPacket = null;
        }
        return mPacket;
    }

    /**
     * Start asynchronous send buffer
     */
    private void sendAsync() {
        if (mDisposed.get())
            return;
        // As soon as the client is connected, post a receive to the connection
        mSender.sendAsync(this);
    }

    /**
     * On asynchronous send end callback
     *
     * @param e IoEventArgs {@link IoEventArgs}
     */
    @Override
    protected void onCompleted(IoEventArgs e) {
        // Check if the remote host closed the connection
        int transferred = e.getBytesTransferred();
        if (transferred > 0) {
            if (transferred < e.getCount()) {
                e.setBuffer(e.getOffset() + transferred, e.getCount() - transferred);
                sendAsync();
            } else {
                sendPacket();
            }
        } else {
            dispose();
        }
    }

    public void dispose() {
        if (mDisposed.compareAndSet(false, true)) {

            SendPacket packet = mPacket;
            if (packet != null)
                packet.endPacket();

            mFormatter.setPacket(null);
            mFormatter = null;
            mPacket = null;
            mDelivery = null;
            mSender = null;
            // Clear
            mQueue.clear();
        }
    }
}
