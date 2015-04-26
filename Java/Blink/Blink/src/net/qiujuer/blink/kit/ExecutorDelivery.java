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
package net.qiujuer.blink.kit;

import net.qiujuer.blink.core.Connector;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.core.delivery.ConnectDelivery;
import net.qiujuer.blink.core.delivery.ReceiveDelivery;
import net.qiujuer.blink.core.delivery.SendDelivery;

import java.util.concurrent.Executor;

/**
 * Delivers send and receive responses.
 */
public class ExecutorDelivery implements ConnectDelivery, SendDelivery, ReceiveDelivery {
    private Executor mPoster;

    public ExecutorDelivery(Executor executor) {
        if ((executor == null)) {
            executor = new Executor() {
                @Override
                public void execute(Runnable command) {
                    command.run();
                }
            };
        }

        mPoster = executor;
    }

    @Override
    public void postSendStart(SendPacket packet) {
        mPoster.execute(new SendStartDeliveryRunnable(packet));
    }

    @Override
    public void postSendProgress(SendPacket packet, float progress) {
        mPoster.execute(new SendProgressDeliveryRunnable(packet, progress));
    }

    @Override
    public void postSendCompleted(SendPacket packet) {
        mPoster.execute(new SendCompletedDeliveryRunnable(packet));
    }

    @Override
    public void postConnectClosed(Connector connector) {
        mPoster.execute(new ConnectDeliveryRunnable(connector));
    }

    @Override
    public void postReceiveStart(Connector connector, ReceivePacket packet) {
        mPoster.execute(new ReceiveStartDeliveryRunnable(connector, packet));
    }

    @Override
    public void postReceiveProgress(Connector connector, ReceivePacket packet, float progress) {
        mPoster.execute(new ReceiveProgressDeliveryRunnable(connector, packet, progress));
    }

    @Override
    public void postReceiveCompleted(Connector connector, ReceivePacket packet) {
        mPoster.execute(new ReceiveCompletedDeliveryRunnable(connector, packet));
    }


    private class ConnectDeliveryRunnable implements Runnable {
        Connector connector;

        public ConnectDeliveryRunnable(Connector connector) {
            this.connector = connector;
        }

        public void run() {
            if (!connector.isClosed()) {
                connector.deliveryConnectClosed();
            }
            connector = null;
        }
    }

    private class ReceiveStartDeliveryRunnable extends ConnectDeliveryRunnable {
        ReceivePacket packet;

        public ReceiveStartDeliveryRunnable(Connector connector, ReceivePacket packet) {
            super(connector);
            this.packet = packet;
        }

        @Override
        public void run() {
            if (!connector.isClosed()) {
                connector.deliveryReceiveStart(packet);
            }
            connector = null;
            packet = null;
        }
    }

    private class ReceiveCompletedDeliveryRunnable extends ConnectDeliveryRunnable {
        ReceivePacket packet;

        public ReceiveCompletedDeliveryRunnable(Connector connector, ReceivePacket packet) {
            super(connector);
            this.packet = packet;
        }

        @Override
        public void run() {
            if (!connector.isClosed()) {
                connector.deliveryReceiveCompleted(packet);
            }
            connector = null;
            packet = null;
        }
    }

    private class ReceiveProgressDeliveryRunnable extends ReceiveStartDeliveryRunnable {
        float progress;

        public ReceiveProgressDeliveryRunnable(Connector connector, ReceivePacket packet, float progress) {
            super(connector, packet);
            this.progress = progress;
        }

        public void run() {
            if (!connector.isClosed()) {
                connector.deliveryReceiveProgress(packet, progress);
            }
            connector = null;
            packet = null;
        }
    }

    private class SendStartDeliveryRunnable implements Runnable {
        SendPacket packet;

        public SendStartDeliveryRunnable(SendPacket packet) {
            this.packet = packet;
        }

        public void run() {
            if (!packet.isCanceled()) {
                packet.deliveryStart();
            }
            packet = null;
        }
    }

    private class SendProgressDeliveryRunnable extends SendStartDeliveryRunnable {
        float progress;

        public SendProgressDeliveryRunnable(SendPacket packet, float progress) {
            super(packet);
            this.progress = progress;
        }

        public void run() {
            if (!packet.isCanceled()) {
                packet.deliveryProgress(progress);
            }
            packet = null;
        }
    }

    private class SendCompletedDeliveryRunnable extends SendStartDeliveryRunnable {
        public SendCompletedDeliveryRunnable(SendPacket packet) {
            super(packet);
        }

        public void run() {
            if (!packet.isCanceled()) {
                packet.deliveryCompleted();
            }
            packet = null;
        }
    }

}
