/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/16/2015
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

import net.qiujuer.blink.core.Connector;
import net.qiujuer.blink.core.Receiver;
import net.qiujuer.blink.core.Sender;
import net.qiujuer.blink.core.delivery.ConnectDelivery;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Async socket adapter to send and receive byte
 */
public class AsyncSocketAdapter implements Sender, Receiver, SelectorFactory.HandleCallback {
    // Is Disposed
    protected final AtomicBoolean mDisposed = new AtomicBoolean(false);
    private IoEventArgs mSendArgs;
    private IoEventArgs mReceiveArgs;
    private SocketChannel mChannel;
    private Connector mConnector;
    // Posting responses.
    private ConnectDelivery mConnectDelivery;

    public AsyncSocketAdapter(SocketChannel channel, Connector connector, ConnectDelivery connectDelivery) throws IOException {
        mChannel = channel;
        mChannel.configureBlocking(false);

        mConnector = connector;
        mConnectDelivery = connectDelivery;
    }

    @Override
    public int getReceiveBufferSize() {
        return mConnector.getBufferSize();
    }

    @Override
    public int getSendBufferSize() {
        return mConnector.getBufferSize();
    }


    @Override
    public boolean receiveAsync(IoEventArgs buffer) {
        if (mDisposed.get())
            return false;
        mReceiveArgs = buffer;
        try {
            return SelectorFactory.getInstance().registerReceive(mChannel, this) != null;
        } catch (ClosedChannelException e) {
            return false;
        }
    }

    @Override
    public boolean sendAsync(IoEventArgs buffer) {
        if (mDisposed.get())
            return false;
        mSendArgs = buffer;
        try {
            return SelectorFactory.getInstance().registerSend(mChannel, this) != null;
        } catch (ClosedChannelException e) {
            return false;
        }
    }

    @Override
    public void dispose() {
        if (mDisposed.compareAndSet(false, true)) {
            SelectorFactory.getInstance().unRegister(mChannel);
            mSendArgs = null;
            mReceiveArgs = null;
            try {
                mChannel.finishConnect();
                mChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ConnectDelivery connectDelivery = mConnectDelivery;
            mConnectDelivery = null;
            if (connectDelivery != null)
                connectDelivery.postConnectClosed(mConnector);

            mConnector = null;
        }
    }

    @Override
    public void handleSend(SelectionKey key) {
        if (mDisposed.get())
            return;
        SocketChannel channel = (SocketChannel) key.channel();
        IoEventArgs args = mSendArgs;
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
        if (mDisposed.get())
            return;
        SocketChannel channel = (SocketChannel) key.channel();
        IoEventArgs args = mReceiveArgs;
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
