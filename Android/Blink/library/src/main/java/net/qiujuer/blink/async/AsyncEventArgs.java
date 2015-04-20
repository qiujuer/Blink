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

import net.qiujuer.blink.kit.Disposable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a buffer use ByteBuffer {@link ByteBuffer}
 * <p/>
 * Use to async socket
 */
public class AsyncEventArgs implements Disposable {
    // Send an Receive packet head size
    public final static int HeadSize = 11;
    // Is Disposed
    protected final AtomicBoolean mDisposed = new AtomicBoolean(false);
    // Notify progress precision
    protected final float mProgressPrecision;
    // Notify progress
    protected float mProgress = 0;
    // Send or Receive status
    protected boolean mStatus = true;

    private int mCount;
    private int mOffset;
    private int mBytesTransferred;
    private final ByteBuffer mByteBuffer;

    public AsyncEventArgs(int capacity, float progressPrecision) {
        mByteBuffer = ByteBuffer.allocate(capacity);
        mProgressPrecision = progressPrecision;
    }

    public byte[] getBuffer() {
        return mByteBuffer.array();
    }

    public void setBuffer(int offset, int count) {
        mCount = count;
        mOffset = offset;
    }

    public int getOffset() {
        return mOffset;
    }

    public int getCount() {
        return mCount;
    }

    public int getBytesTransferred() {
        return mBytesTransferred;
    }

    private void formatBuffer() {
        mByteBuffer.clear();
        mByteBuffer.limit(mOffset + mCount);
        mByteBuffer.position(mOffset);
    }

    void send(SocketChannel channel) throws IOException {
        formatBuffer();
        ByteBuffer buffer = mByteBuffer;
        mBytesTransferred = 0;
        mBytesTransferred = channel.write(buffer);
        onCompleted(this);
    }

    void receive(SocketChannel channel) throws IOException {
        formatBuffer();
        ByteBuffer buffer = mByteBuffer;
        mBytesTransferred = 0;
        mBytesTransferred = channel.read(buffer);
        onCompleted(this);
    }

    void onCompleted(AsyncEventArgs e) {

    }

    protected boolean isNotifyProgress(float newProgress) {
        if ((newProgress - mProgress) > mProgressPrecision) {
            mProgress = newProgress;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void dispose() {
        mByteBuffer.clear();
    }
}
