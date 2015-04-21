/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 03/31/2015
 * Changed 04/02/2015
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
package net.qiujuer.blink.core;

/**
 * Provides a thread for performing receive dispatch from a socket of BinkConn
 * {@link BlinkConn}.
 */
public class ReceiveDispatcher extends Thread {
    /**
     * The sender interface for processing sender requests.
     */
    private final Receiver mReceiver;
    /**
     * For posting receive responses.
     */
    private final ReceiveDelivery mDelivery;
    /**
     * Used for telling us to die.
     */
    private volatile boolean mQuit = false;

    public ReceiveDispatcher(Receiver receiver, ReceiveDelivery delivery) {
        mReceiver = receiver;
        mDelivery = delivery;
    }

    /**
     * Forces this dispatcher to quit immediately. If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        while (!mQuit) {
            ReceivePacket<?> entity;
            try {
                // Receive head
                entity = mReceiver.receiveHead();
                if (entity == null) {
                    sleepSomeTime();
                    continue;
                }

                // Adjust Stream
                entity.adjustStream();

                // Post Start
                mDelivery.postReceiveStart(entity);

                // Receive entity
                boolean status = mReceiver.receiveEntity(entity, mDelivery);

                // Adjust Result value form stream
                entity.adjustPacket();

                // Post End
                mDelivery.postReceiveEnd(entity, status);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sleepSomeTime();
            }
        }
    }

    private void sleepSomeTime() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
