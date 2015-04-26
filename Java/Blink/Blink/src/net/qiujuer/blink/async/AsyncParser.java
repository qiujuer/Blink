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
package net.qiujuer.blink.async;

import net.qiujuer.blink.box.ByteReceivePacket;
import net.qiujuer.blink.box.FileReceivePacket;
import net.qiujuer.blink.box.StringReceivePacket;
import net.qiujuer.blink.core.PacketParser;
import net.qiujuer.blink.core.PacketType;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.kit.convert.BitConverter;

import java.io.File;
import java.io.IOException;

/**
 * Asynchronous packet parser
 */
public class AsyncParser extends PacketParser {
    // A unique identifier on the Resource clear
    private final String mMark;
    // The root directory to use for the resource.
    private final File mPath;
    // Receive buffer size
    private int mBufferSize;
    // Receive packet id
    private long mId = 0;

    protected byte[] mInfoBytes;
    // Size
    protected short mSurInfoLen;
    protected long mSurEntityLen;
    protected float mTotal;


    public AsyncParser(String mark, File path) {
        mMark = mark;
        mPath = path;
    }

    @Override
    public float parse() {

        if (mSurInfoLen > 0) {
            // Receive Entity
            return parseInfo(mArgs.getBuffer(), 0, mArgs.getOffset() + mArgs.getBytesTransferred());
        } else if (mSurEntityLen > 0) {
            // Receive Info
            return parseEntity(mArgs.getBuffer(), 0, mArgs.getOffset() + mArgs.getBytesTransferred());
        } else {
            // Receive Head
            return parseHead(mArgs.getBuffer());
        }
    }

    @Override
    public void setEventArgs(IoEventArgs args) {
        super.setEventArgs(args);
        mBufferSize = args.getBuffer().length;
        setArgBuffer(HEAD_SIZE);
    }

    protected float parseHead(byte[] buffer) {
        byte type = buffer[0];
        long len = BitConverter.toLong(buffer, 1);

        if (len > 0) {
            // Set Length
            mSurEntityLen = len;
            mSurInfoLen = BitConverter.toShort(buffer, HEAD_SIZE - 2);
            mTotal = mSurInfoLen + mSurEntityLen;

            if (mSurInfoLen > 0)
                mInfoBytes = new byte[mSurInfoLen];

            // Parse receive packet
            ReceivePacket packet = parseType(type, len);

            if (packet != null && packet.startPacket()) {
                mPacket = packet;
            } else {
                // Set Null
                mPacket = null;
            }
        }

        setArgBuffer(mSurInfoLen > 0 ? mSurInfoLen : mSurEntityLen);

        return STATUS_START;
    }

    protected float parseInfo(byte[] buffer, int offset, int count) {
        // Copy
        System.arraycopy(buffer, offset, mInfoBytes, mInfoBytes.length - mSurInfoLen, count);
        // Set len
        mSurInfoLen -= count;
        if (mSurInfoLen <= 0) {
            // Set Info
            ReceivePacket packet = mPacket;
            if (packet != null) {
                packet.setInfo(mInfoBytes);
            }
            mInfoBytes = null;
            // Receive entity
            setArgBuffer(mSurEntityLen);
        } else {
            // Receive entity
            setArgBuffer(mSurInfoLen);
        }
        return (mTotal - mSurInfoLen - mSurEntityLen) / mTotal;
    }

    protected float parseEntity(byte[] buffer, int offset, int count) {
        // Set len
        mSurEntityLen -= count;

        ReceivePacket packet = mPacket;
        if (packet != null) {
            packet.write(buffer, offset, count);
        }

        // Receive next entity
        setArgBuffer(mSurEntityLen);

        // Progress
        if (mSurEntityLen <= 0) {
            if (packet != null) {
                packet.endPacket();
            }
            return STATUS_END;
        } else
            return (mTotal - mSurInfoLen - mSurEntityLen) / mTotal;
    }

    protected void setArgBuffer(long size) {
        int count;
        if (size > mBufferSize)
            count = mBufferSize;
        else if (size <= 0)
            count = HEAD_SIZE;
        else
            count = (int) size;

        // Set post buffer
        mArgs.setBuffer(0, count);
    }

    protected ReceivePacket parseType(byte type, long len) {
        long id = ++mId;
        ReceivePacket packet = null;
        switch (type) {
            case PacketType.STRING:
                packet = new StringReceivePacket(id, type, len);
                break;
            case PacketType.BYTES:
                packet = new ByteReceivePacket(id, type, len);
                break;
            case PacketType.FILE:
                File file = create(id);
                if (file != null)
                    packet = new FileReceivePacket(id, type, len, file);
                break;
        }
        return packet;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected File create(long id) {
        File file = new File(mPath, String.format("%1$s_%2$d.blink", mMark, id));
        try {
            if (!mPath.exists()) {
                // Create path
                if (!mPath.mkdirs()) {
                    throw new IOException("Unable to create resource dir" + mPath.getAbsolutePath());
                }
            }
            // Create file
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
