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

import net.qiujuer.blink.listener.SendListener;

/**
 * Blink SendPacket
 */
public abstract class SendPacket extends BlinkPacket {
    private final SendListener mListener;
    private boolean mCanceled;
    private BlinkConn mBlinkConn;


    public SendPacket(byte type, SendListener listener) {
        super(type);
        mListener = listener;
    }

    public SendListener getListener() {
        return mListener;
    }

    /**
     * Cancel the packet to send
     * If the packet on sending you can't cancel it
     * But you can cancel sending notify callback
     */
    public void cancel() {
        mCanceled = true;
        if (mBlinkConn != null) {
            mBlinkConn.cancel(this);
            mBlinkConn = null;
        }
    }

    /**
     * Get the packet iscanceled
     *
     * @return Is Canceled
     */
    public boolean isCanceled() {
        return mCanceled;
    }

    /**
     * Set The BlinkConn to Cancel from queue
     *
     * @param blinkConn BlinkConn
     * @return SendPacket
     */
    SendPacket setBlinkConn(BlinkConn blinkConn) {
        mBlinkConn = blinkConn;
        return this;
    }

    /**
     * On Sender send the packet call this to send packet info
     * The bytes in 0~32767 size
     *
     * @param buffer Send buffer
     * @param index  Buffer start index
     * @return Read to buffer count
     */
    public short readInfo(byte[] buffer, int index) {
        return 0;
    }

    /**
     * Sender read same data to send
     *
     * @param buffer Buffer
     * @param offset Buffer offset
     * @param count  Buffer count
     * @return Read to buffer count
     */
    public abstract int read(byte[] buffer, int offset, int count);
}
