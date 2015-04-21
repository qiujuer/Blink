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

import net.qiujuer.blink.core.BlinkDelivery;
import net.qiujuer.blink.core.Receiver;
import net.qiujuer.blink.core.Sender;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Async socket adapter to send and receive byte
 */
public class AsyncSocketAdapter implements Sender, Receiver, HandleSelector.HandleCallback {
    private int mBufferSize;
    private AsyncEventArgs mSendArgs;
    private AsyncEventArgs mReceiveArgs;
    private SocketChannel mChannel;
    // Is Disposed
    protected final AtomicBoolean mDisposed = new AtomicBoolean(false);
    // Posting responses.
    private BlinkDelivery mBlinkDelivery;

    public AsyncSocketAdapter(SocketChannel channel, int bufferSize, BlinkDelivery blinkDelivery) throws IOException {
        mChannel = channel;
        mChannel.configureBlocking(false);
        mBufferSize = bufferSize;
        mBlinkDelivery = blinkDelivery;
    }

    @Override
    public int getReceiveBufferSize() {
        return mBufferSize;
    }

    @Override
    public int getSendBufferSize() {
        return mBufferSize;
    }


    @Override
    public boolean receiveAsync(AsyncEventArgs buffer) {
        mReceiveArgs = buffer;
        try {
            return HandleSelector.getInstance().registerReceive(mChannel, this) != null;
        } catch (ClosedChannelException e) {
            return false;
        }
    }

    @Override
    public boolean sendAsync(AsyncEventArgs buffer) {
        mSendArgs = buffer;
        try {
            return HandleSelector.getInstance().registerSend(mChannel, this) != null;
        } catch (ClosedChannelException e) {
            return false;
        }
    }

    @Override
    public void dispose() {
        if (mDisposed.compareAndSet(false, true)) {
            HandleSelector.getInstance().unRegister(mChannel);
            mSendArgs = null;
            mReceiveArgs = null;
            try {
                mChannel.finishConnect();
                mChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BlinkDelivery blinkDelivery = mBlinkDelivery;
            mBlinkDelivery = null;
            if (blinkDelivery != null)
                blinkDelivery.postBlinkDisconnect();
        }
    }

    @Override
    public void handleSend(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        AsyncEventArgs args = mSendArgs;
        if (args != null) {
            try {
                args.send(channel);
            } catch (IOException e) {
                //e.printStackTrace();
                dispose();
            }
        }
    }

    @Override
    public void handleReceive(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        AsyncEventArgs args = mReceiveArgs;
        if (args != null) {
            try {
                args.receive(channel);
            } catch (IOException e) {
                //e.printStackTrace();
                dispose();
            }
        }
    }
}
