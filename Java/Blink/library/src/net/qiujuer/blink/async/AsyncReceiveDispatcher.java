/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/16/2015
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

import net.qiujuer.blink.BlinkClient;
import net.qiujuer.blink.core.Connector;
import net.qiujuer.blink.core.PacketFilter;
import net.qiujuer.blink.core.PacketParser;
import net.qiujuer.blink.core.ReceiveDispatcher;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.core.Receiver;
import net.qiujuer.blink.core.delivery.ReceiveDelivery;

/**
 * Provides for performing receive dispatch from a queue of BinkConn {@link BlinkClient}.
 */
public class AsyncReceiveDispatcher extends AsyncDispatcher implements ReceiveDispatcher {
    // Parser the receive data type
    private PacketParser mParser;
    // Receive Data
    private Receiver mReceiver;
    // Posting responses.
    private ReceiveDelivery mReceiveDelivery;
    // Blink Connector
    private Connector mConnector;


    public AsyncReceiveDispatcher(Receiver receiver, PacketParser parser, Connector connector, ReceiveDelivery receiveDelivery) {
        // Set Buffer
        super(receiver.getReceiveBufferSize(), connector.getProgressPrecision());

        mReceiver = receiver;
        mParser = parser;
        mReceiveDelivery = receiveDelivery;
        mConnector = connector;

        // Start
        mParser.setEventArgs(this);
        receiveAsync();
    }

    /**
     * Start async receive
     */
    private void receiveAsync() {
        if (mDisposed.get())
            return;
        // As soon as the client is connected, post a receive to the connection
        mReceiver.receiveAsync(this);
    }

    /**
     * Receive packet by parser
     * Notify send progress
     */
    private void receivePacket() {
        float progress = mParser.parse();
        // Notify
        ReceiveDelivery delivery = mReceiveDelivery;
        if (delivery != null && isNotifyProgress(progress)) {
            if (progress == PacketFilter.STATUS_START) {
                delivery.postReceiveStart(mConnector, mParser.getPacket());
            } else if (progress == PacketFilter.STATUS_END) {
                delivery.postReceiveCompleted(mConnector, mParser.getPacket());
            } else {
                delivery.postReceiveProgress(mConnector, mParser.getPacket(), progress);
            }
        }
    }

    /**
     * On async receive completed
     *
     * @param e IoEventArgs {@link IoEventArgs}
     */
    @Override
    protected void onCompleted(IoEventArgs e) {
        // Check if the remote host closed the connection
        int transferred = e.getBytesTransferred();
        if (transferred > 0) {
            if (transferred < e.getCount()) {
                // Full the head
                e.setBuffer(e.getOffset() + transferred, e.getCount() - transferred);
                receiveAsync();
            } else {
                receivePacket();
                receiveAsync();
            }
        } else {
            dispose();
        }
    }

    @Override
    public void dispose() {
        if (mDisposed.compareAndSet(false, true)) {
            ReceivePacket packet = mParser.getPacket();
            if (packet != null)
                packet.endPacket();
            mParser = null;
            mReceiver = null;
            mReceiveDelivery = null;
        }
    }
}
