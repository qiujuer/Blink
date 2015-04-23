/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/16/2015
 * Changed 04/23/2015
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

import net.qiujuer.blink.core.SendDelivery;
import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.core.Sender;
import net.qiujuer.blink.kit.BitConverter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides for performing send dispatch from a queue of BinkConn {@link net.qiujuer.blink.core.BlinkConn}.
 */
public class AsyncSendDispatcher extends AsyncEventArgs {
    // The queue of send entity.
    private final Queue<SendPacket> mQueue;
    // The sender interface for processing sender requests.
    private Sender mSender;
    // Posting send responses.
    private SendDelivery mDelivery;
    // Used for telling us to die.
    private final AtomicBoolean mSending = new AtomicBoolean(false);

    private SendPacket mPacket;
    private long mCursor;
    private long mTotal;


    public AsyncSendDispatcher(Sender sender, SendDelivery delivery, float progressPrecision) {
        // Set Buffer
        super(sender.getSendBufferSize(), progressPrecision);

        mQueue = new ConcurrentLinkedQueue<SendPacket>();
        mSender = sender;
        mDelivery = delivery;

    }

    /**
     * Add a packet to queue to send
     *
     * @param packet SendPacket
     */
    public void send(SendPacket packet) {
        synchronized (mQueue) {
            mQueue.offer(packet);
        }

        if (mSending.compareAndSet(false, true)) {
            // Send Next
            sendNext();
        }
    }

    /**
     * Delete a packet from queue to cancel
     *
     * @param packet SendPacket
     */
    public void cancel(SendPacket packet) {
        synchronized (mQueue) {
            mQueue.remove(packet);
        }
    }

    /**
     * Notify send progress
     */
    private void notifyProgress() {
        SendPacket packet = mPacket;
        SendDelivery delivery = mDelivery;

        if (!mDisposed.get() && packet != null && delivery != null) {
            // Progress
            float progress = (float) mCursor / mTotal;
            // Post Callback
            if (isNotifyProgress(progress)) {
                delivery.postSendProgress(packet, mProgress);
            }
        }
    }

    /**
     * Send next packet
     */
    private void sendNext() {
        SendPacket packet = mPacket;
        mPacket = null;
        // Notify
        if (packet != null) {

            // Set Status
            mStatus = mCursor == mTotal;

            // End
            packet.setSuccess(mStatus);
            packet.endPacket();

            // Post End
            mCursor = mTotal;
            notifyProgress();
        }

        // Check
        mSending.set(!mQueue.isEmpty());
        if (mSending.get()) {

            // Pool a request from the queue.
            packet = mQueue.poll();

            // Cancel
            if (packet.isCanceled()) {
                sendNext();
            } else {
                // Set Packet
                mPacket = packet;

                // Start Send
                sendHead();
            }
        }
    }

    /**
     * Init send data head info
     */
    private void sendHead() {
        // Init Head
        final int size;
        mTotal = mPacket.getLength();
        if (mTotal <= 0)
            size = 0;
        else {
            byte[] bytes = getBuffer();

            // Type
            bytes[0] = mPacket.getPacketType();

            // Length
            BitConverter.toBytes(mTotal, bytes, 1);

            // Info
            short infoLen = mPacket.readInfo(bytes, HeadSize);
            BitConverter.toBytes(infoLen, bytes, HeadSize - 2);

            size = HeadSize + infoLen;
        }
        // Check packet size
        if (size > 0) {
            // Init Size
            mCursor = 0;
            mProgress = 0;
            // Post Start
            notifyProgress();
            // Init the packet
            mPacket.startPacket();
            // Send Head
            sendAsync(0, size);
        } else {
            // Send next
            mPacket = null;
            sendNext();
        }
    }

    /**
     * Start packet entity
     */
    private void sendEntity() {
        SendPacket packet = mPacket;
        if (packet != null) {
            // Buffer
            byte[] bytes = getBuffer();

            int count = packet.read(bytes, 0, mSender.getSendBufferSize());

            mCursor += count;

            // Send
            sendAsync(0, count);

            // Notify
            notifyProgress();
        }
    }

    /**
     * Start asynchronous send buffer
     *
     * @param offset buffer offset
     * @param count  send size
     */
    private void sendAsync(int offset, int count) {
        if (mDisposed.get())
            return;

        if (count <= 0)
            sendNext();

        // Set Send Buffer Size
        setBuffer(offset, count);

        // As soon as the client is connected, post a receive to the connection
        mStatus = mSender.sendAsync(this);
        if (!mStatus) {
            onCompleted(this);
        }
    }

    /**
     * On asynchronous send end callback
     *
     * @param e AsyncEventArgs
     */
    @Override
    protected void onCompleted(AsyncEventArgs e) {
        super.onCompleted(e);

        // Check if the remote host closed the connection
        if (e.getBytesTransferred() > 0) {
            if (e.getBytesTransferred() < e.getCount())
                sendAsync(e.getOffset() + e.getBytesTransferred(), e.getCount() - e.getBytesTransferred());
            else if (mCursor != mTotal)
                sendEntity();
            else
                sendNext();
        } else {
            dispose();
        }
    }

    public void dispose() {
        if (mDisposed.compareAndSet(false, true)) {

            super.dispose();

            SendPacket packet = mPacket;
            mPacket = null;

            SendDelivery delivery = mDelivery;
            mDelivery = null;

            Sender sender = mSender;
            mSender = null;

            if (packet != null && delivery != null) {
                if (mCursor < mTotal) {
                    packet.endPacket();
                    packet.setSuccess(false);
                    delivery.postSendProgress(packet, 1);
                }
            }

            if (sender != null)
                sender.dispose();

            // Clear
            mQueue.clear();

        }

    }
}
