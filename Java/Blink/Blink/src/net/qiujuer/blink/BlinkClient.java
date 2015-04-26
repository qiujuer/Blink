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
package net.qiujuer.blink;

import net.qiujuer.blink.core.ExecutorDelivery;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Blink Conn
 * This class is Blink Main class
 * You sen and cancel packet use this
 */
public class BlinkClient extends BlinkConnect {
    private SocketAddress mAddress;

    public BlinkClient() {
        setDelivery(new ExecutorDelivery(null));
    }

    public boolean connect(String ip, int port) {
        return connect(new InetSocketAddress(ip, port));
    }

    public boolean connect(InetSocketAddress address) {
        return connect((SocketAddress) address);
    }

    public boolean connect(SocketAddress address) {
        mAddress = address;
        try {
            SocketChannel channel = SocketChannel.open(mAddress);
            boolean connected = channel.isConnected();
            if (connected) {
                channel.configureBlocking(false);
                start(channel);
            }
            return connected;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reconnect() {
        if (mAddress == null)
            throw new NullPointerException("SocketChannel address is null, you should call connect.");

        dispose();

        return connect(mAddress);
    }
}
