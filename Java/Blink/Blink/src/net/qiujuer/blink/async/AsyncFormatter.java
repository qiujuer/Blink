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
    // Send an Receive packet head size
    public final static int HEAD_SIZE = 11;
    public final static int NEED_PACKET = -1;

    private long mCursor = 0;
    private long mTotal = 0;

    @Override
    public float format() {
        if (mPacket == null)
            return NEED_PACKET;

        if (mTotal == 0) {
            return formatHead(mArgs.getBuffer());
        } else if (mCursor != mTotal) {
            formatEntity(mArgs.getBuffer());
        }
        return mCursor / (float) mTotal;
    }

    @Override
    public void setPacket(SendPacket packet) {
        super.setPacket(packet);
        mTotal = 0;
        mCursor = 0;
    }

    protected float formatHead(byte[] buffer) {
        mTotal = mPacket.getLength();
        if (mTotal <= 0) {
            return NEED_PACKET;
        } else {
            // Type
            buffer[0] = mPacket.getPacketType();
            // Length
            BitConverter.toBytes(mTotal, buffer, 1);
            // Info
            short infoLen = mPacket.readInfo(buffer, HEAD_SIZE);
            BitConverter.toBytes(infoLen, buffer, HEAD_SIZE - 2);
            // Send head
            int headCount = HEAD_SIZE + infoLen;
            // Set buffer
            setArgBuffer(headCount);
            // Start
            mPacket.startPacket();
            return 0;
        }
    }

    protected void formatEntity(byte[] buffer) {
        int count = mPacket.read(buffer, 0, buffer.length);
        if (count > 0) {
            setArgBuffer(count);
            mCursor += count;
        }

        // End
        if (mCursor >= mTotal)
            mPacket.endPacket();
    }

    protected void setArgBuffer(int count) {
        mArgs.setBuffer(0, count);
    }
}
