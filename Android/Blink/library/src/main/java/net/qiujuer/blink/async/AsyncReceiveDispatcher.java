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
package net.qiujuer.blink.async;

import net.qiujuer.blink.core.BlinkParser;
import net.qiujuer.blink.core.ReceiveDelivery;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.core.Receiver;
import net.qiujuer.blink.kit.BitConverter;

/**
 * Provides for performing receive dispatch from a queue of BinkConn {@link net.qiujuer.blink.core.BlinkConn}.
 */
public class AsyncReceiveDispatcher extends AsyncEventArgs {
    // Parser the receive data type
    private BlinkParser mParser;
    // Receive Data
    private Receiver mReceiver;
    // Posting responses.
    private ReceiveDelivery mReceiveDelivery;

    private ReceivePacket mReceivePacket;
    private short mSurplusInfoLen;
    private long mSurplusLen;


    public AsyncReceiveDispatcher(Receiver receiver, BlinkParser parser, ReceiveDelivery receiveDelivery, float progressPrecision) {
        // Set Buffer
        super(receiver.getReceiveBufferSize(), progressPrecision);

        mReceiver = receiver;
        mParser = parser;
        mReceiveDelivery = receiveDelivery;

        // Start
        ReceiveAsync(0);
    }


    private void ReceiveAsync(long size) {
        int count;
        if (size > mReceiver.getReceiveBufferSize())
            count = mReceiver.getReceiveBufferSize();
        else if (size <= 0)
            count = HeadSize;
        else
            count = (int) size;

        ReceiveAsync(0, count);
    }

    private void ReceiveAsync(int offset, int count) {
        if (mDisposed.get())
            return;

        // Set post buffer
        setBuffer(offset, count);

        // Post a receive to the connection
        mStatus = mReceiver.receiveAsync(this);
        if (!mStatus) {
            // On sync call
            onCompleted(this);
        }
    }

    private void ReceiveHead(byte[] buffer) {
        mSurplusLen = 0;
        mSurplusInfoLen = 0;
        mProgress = 0;

        byte type = buffer[0];
        long len = BitConverter.toLong(buffer, 1);
        short info = BitConverter.toShort(buffer, HeadSize - 2);

        if (len > 0) {
            // Set Length
            mSurplusLen = len;
            mSurplusInfoLen = info;

            // Parse receive packet
            ReceivePacket packet = mParser.parseReceive(type, len);

            if (packet != null && packet.startPacket()) {
                mReceivePacket = packet;

                // Notify
                ReceiveDelivery delivery = mReceiveDelivery;
                if (delivery != null)
                    delivery.postReceiveStart(packet);

            } else {
                // Set Null
                mReceivePacket = null;
            }
        }

        ReceiveAsync(mSurplusInfoLen > 0 ? mSurplusInfoLen : mSurplusLen);
    }

    private void ReceiveInfo(byte[] buffer, int offset, int count) {
        // Set len
        mSurplusInfoLen -= (short) count;

        if (mSurplusInfoLen > 0) {
            // Receive info
            ReceiveAsync(offset + count, mSurplusInfoLen);
        } else {
            // Set Info
            ReceivePacket packet = mReceivePacket;
            if (packet != null) {
                packet.writeInfo(buffer, 0, offset + count);
            }

            // Receive entity
            ReceiveAsync(mSurplusLen);
        }
    }

    private void ReceiveEntity(byte[] buffer, int offset, int count) {
        // Set len
        mSurplusLen -= count;

        ReceivePacket packet = mReceivePacket;

        if (packet != null) {
            packet.write(buffer, offset, count);

            // Check
            ReceiveDelivery delivery = mReceiveDelivery;
            if (delivery != null) {
                // Notify progress
                float len = packet.getLength();

                // Post Callback
                float progress = (len - mSurplusLen) / len;
                if (isNotifyProgress(progress)) {
                    // Notify
                    delivery.postReceiveProgress(packet, mProgress);
                }
            }

            if (mSurplusLen <= 0) {
                // End
                packet.endPacket();

                // Notify
                if (delivery != null)
                    delivery.postReceiveEnd(packet, mStatus);

                // Set Null
                mReceivePacket = null;
            }
        }

        // Receive next entity
        ReceiveAsync(mSurplusLen);
    }

    @Override
    void onCompleted(AsyncEventArgs e) {
        // Check if the remote host closed the connection
        if (e.getBytesTransferred() > 0) {
            // Receive Entity
            if (mSurplusInfoLen > 0)
                ReceiveInfo(e.getBuffer(), e.getOffset(), e.getBytesTransferred());
            else if (mSurplusLen > 0)
                ReceiveEntity(e.getBuffer(), e.getOffset(), e.getBytesTransferred());
            else {
                if (e.getBytesTransferred() < e.getCount())
                    // Full the head
                    ReceiveAsync(e.getOffset() + e.getBytesTransferred(), e.getCount() - e.getBytesTransferred());
                else
                    // Receive Head
                    ReceiveHead(e.getBuffer());
            }
        } else {
            dispose();
        }
    }

    @Override
    public void dispose() {
        if (mDisposed.compareAndSet(false, true)) {
            super.dispose();

            mParser = null;

            ReceivePacket packet = mReceivePacket;
            mReceivePacket = null;

            Receiver receiver = mReceiver;
            mReceiver = null;

            ReceiveDelivery receiveDelivery = mReceiveDelivery;
            mReceiveDelivery = null;

            if (packet != null && receiveDelivery != null) {
                if (mSurplusLen > 0) {
                    packet.endPacket();
                    receiveDelivery.postReceiveEnd(packet, false);
                }
            }

            if (receiver != null)
                receiver.dispose();
        }
    }
}
