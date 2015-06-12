/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/19/2015
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
package net.qiujuer.sample.blink;

import net.qiujuer.blink.box.ByteReceivePacket;
import net.qiujuer.blink.box.FileReceivePacket;
import net.qiujuer.blink.box.StringReceivePacket;
import net.qiujuer.blink.core.Connector;
import net.qiujuer.blink.core.PacketType;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.core.listener.ConnectListener;
import net.qiujuer.blink.core.listener.ReceiveListener;

import java.util.Arrays;

/**
 * Call Back status
 */
public class CallBack implements ReceiveListener, ConnectListener {

    @Override
    public void onReceiveStart(Connector connector, ReceivePacket packet) {
        System.out.println("Receive->start:" + packet.getId() + " " + packet.getPacketType());
    }

    @Override
    public void onReceiveProgress(Connector connector, ReceivePacket packet, float progress) {
        System.out.println("Receive->progress:" + progress);
    }

    @Override
    public void onReceiveCompleted(Connector connector, ReceivePacket packet) {
        if (packet.getPacketType() == PacketType.STRING)
            System.out.println("Receive->end: String:"
                    + packet.getId() + " " + packet.getLength() + " :"
                    + ((StringReceivePacket) packet).getEntity());
        else if (packet.getPacketType() == PacketType.BYTES)
            System.out.println("Receive->end: Bytes:"
                    + packet.getId() + " " + packet.getLength() + " :"
                    + Arrays.toString(((ByteReceivePacket) packet).getEntity()));
        else
            System.out.println("Receive->end: File:"
                    + packet.getId()
                    + " "
                    + packet.getLength()
                    + " :"
                    + ((FileReceivePacket) packet).getEntity()
                    .getPath() + " " + packet.getHash());
    }

    @Override
    public void onConnectClosed(Connector connector) {
        System.out.println("A BlinkDisconnect.");
    }
}
