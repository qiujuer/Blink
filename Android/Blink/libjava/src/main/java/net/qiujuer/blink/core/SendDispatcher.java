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

import java.util.concurrent.BlockingQueue;

/**
 * Provides a thread for performing send dispatch from a queue of BinkConn {@link BlinkConn}.
 */
public class SendDispatcher extends Thread {
    /**
     * The queue of send entity.
     */
    private final BlockingQueue<SendPacket<?>> mQueue;
    /**
     * The sender interface for processing sender requests.
     */
    private final Sender mSender;
    /**
     * For posting send responses.
     */
    private final SendDelivery mDelivery;
    /**
     * Used for telling us to die.
     */
    private volatile boolean mQuit = false;

    public SendDispatcher(BlockingQueue<SendPacket<?>> queue,
                          Sender sender, SendDelivery delivery) {
        mQueue = queue;
        mSender = sender;
        mDelivery = delivery;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        while (true) {
            SendPacket<?> entity;
            try {
                // Take a request from the queue.
                entity = mQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                if (entity.isCanceled()) {
                    continue;
                }
                // Post Start
                mDelivery.postSendStart(entity);

                // Send
                boolean status = mSender.sendHead(entity) && mSender.sendEntity(entity, mDelivery);

                // Post End
                mDelivery.postSendEnd(entity, status);

            } catch (Exception e) {
                e.printStackTrace();
                //mDelivery.postSendError();
            }
        }
    }
}
