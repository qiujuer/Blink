/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/25/2015
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

import net.qiujuer.blink.core.PacketFormatter;
import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.kit.convert.BitConverter;

/**
 * Async send packet formatter
 */
public class AsyncFormatter extends PacketFormatter {
    protected byte[] mInfoBytes;
    protected short mSurInfoLen;
    protected long mSurEntityLen = 0;
    protected float mTotal = 0;

    @Override
    public float format() {
        SendPacket packet = mPacket;

        if (packet == null)
            return STATUS_NEED;
        else if (mSurInfoLen > 0)
            return formatInfo(mArgs.getBuffer());
        else if (mSurEntityLen > 0)
            return formatEntity(packet, mArgs.getBuffer());
        else
            return formatHead(packet, mArgs.getBuffer());

    }

    @Override
    public void setPacket(SendPacket packet) {
        super.setPacket(packet);
        mTotal = 0;
        mSurEntityLen = 0;
        mSurInfoLen = 0;
    }

    protected float formatHead(SendPacket packet, byte[] buffer) {
        mSurEntityLen = packet.getLength();
        if (mSurEntityLen <= 0) {
            return STATUS_NEED;
        } else {
            // Type
            buffer[0] = packet.getPacketType();
            // Length
            BitConverter.toBytes(mSurEntityLen, buffer, 1);
            // Info
            mInfoBytes = packet.getInfo();
            if (mInfoBytes != null)
                mSurInfoLen = (short) mInfoBytes.length;
            BitConverter.toBytes(mSurInfoLen, buffer, HEAD_SIZE - 2);
            // Total
            mTotal = mSurInfoLen + mSurEntityLen;
            // Set buffer
            setArgBuffer(HEAD_SIZE);
            // Start
            packet.startPacket();
            return STATUS_START;
        }
    }

    protected float formatInfo(byte[] buffer) {
        int count = Math.min(mSurInfoLen, buffer.length);
        System.arraycopy(mInfoBytes, mInfoBytes.length - mSurInfoLen, buffer, 0, count);
        mSurInfoLen -= count;
        if (mSurInfoLen <= 0)
            mInfoBytes = null;
        setArgBuffer(count);

        return ((mTotal - mSurInfoLen - mSurEntityLen) / mTotal);
    }

    protected float formatEntity(SendPacket packet, byte[] buffer) {
        int count = packet.read(buffer, 0, buffer.length);
        if (count > 0) {
            setArgBuffer(count);
            mSurEntityLen -= count;
        }

        // End
        if (mSurEntityLen <= 0) {
            packet.endPacket();
            return STATUS_END;
        } else {
            return ((mTotal - mSurInfoLen - mSurEntityLen) / mTotal);
        }
    }

    protected void setArgBuffer(int count) {
        mArgs.setBuffer(0, count);
    }
}
