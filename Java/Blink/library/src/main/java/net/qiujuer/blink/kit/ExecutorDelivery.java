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
package net.qiujuer.blink.kit;

import net.qiujuer.blink.core.BlinkDelivery;
import net.qiujuer.blink.core.ReceiveDelivery;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.core.SendDelivery;
import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.listener.BlinkListener;
import net.qiujuer.blink.listener.ReceiveListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Delivers send and receive responses.
 */
public class ExecutorDelivery implements BlinkDelivery, SendDelivery, ReceiveDelivery {
    /**
     * Used for posting responses, typically to the main thread.
     */
    private final boolean isNull;
    private Executor mPoster;
    private BlinkListener mBlinkListener;
    private ReceiveListener mReceiveListener;

    public ExecutorDelivery(Executor executor, BlinkListener blinkListener, ReceiveListener receiveListener) {
        if ((isNull = executor == null)) {
            executor = Executors.newSingleThreadExecutor();
        }

        mPoster = executor;
        mBlinkListener = blinkListener;
        mReceiveListener = receiveListener;
    }

    @Override
    public void postBlinkDisconnect() {
        BlinkListener listener = mBlinkListener;
        if (listener != null)
            mPoster.execute(new BlinkDeliveryRunnable(listener));
    }

    @Override
    public void postSendProgress(SendPacket entity, float progress) {
        if (entity != null && entity.getListener() != null)
            mPoster.execute(new SendDeliveryRunnable(entity, progress));
    }

    @Override
    public void postReceiveStart(ReceivePacket entity) {
        ReceiveListener listener = mReceiveListener;
        if (listener != null && entity != null)
            mPoster.execute(new ReceiveStartDeliveryRunnable(listener, entity));
    }

    @Override
    public void postReceiveEnd(ReceivePacket entity, boolean isSuccess) {
        ReceiveListener listener = mReceiveListener;
        if (listener != null && entity != null) {
            entity.setSuccess(isSuccess);
            mPoster.execute(new ReceiveEndDeliveryRunnable(listener, entity));
        }
    }

    @Override
    public void postReceiveProgress(ReceivePacket entity, float progress) {
        ReceiveListener listener = mReceiveListener;
        if (listener != null && entity != null)
            mPoster.execute(new ReceiveProgressDeliveryRunnable(listener, entity, progress));
    }

    @Override
    public synchronized void dispose() {
        mBlinkListener = null;
        mReceiveListener = null;
        if (isNull && mPoster != null) {
            if (mPoster instanceof ExecutorService)
                ((ExecutorService) mPoster).shutdownNow();
        }
        mPoster = null;
    }


    private class BlinkDeliveryRunnable implements Runnable {
        private BlinkListener listener;

        public BlinkDeliveryRunnable(BlinkListener listener) {
            this.listener = listener;
        }

        public void run() {
            if (listener != null) {
                listener.onBlinkDisconnect();
                listener = null;
            }
        }
    }

    private class ReceiveStartDeliveryRunnable implements Runnable {
        private ReceiveListener listener;
        private ReceivePacket entity;

        public ReceiveStartDeliveryRunnable(ReceiveListener listener, ReceivePacket entity) {
            this.listener = listener;
            this.entity = entity;
        }

        public void run() {
            listener.onReceiveStart(entity.getPacketType(), entity.getId());
            entity = null;
            listener = null;
        }
    }

    private class ReceiveEndDeliveryRunnable implements Runnable {
        private ReceiveListener listener;
        private ReceivePacket entity;

        public ReceiveEndDeliveryRunnable(ReceiveListener listener, ReceivePacket entity) {
            this.listener = listener;
            this.entity = entity;
        }

        public void run() {
            listener.onReceiveEnd(entity);
            entity = null;
            listener = null;
        }
    }

    private class ReceiveProgressDeliveryRunnable implements Runnable {
        private ReceiveListener listener;
        private ReceivePacket entity;
        private float progress;

        public ReceiveProgressDeliveryRunnable(ReceiveListener listener, ReceivePacket entity, float progress) {
            this.listener = listener;
            this.entity = entity;
            this.progress = progress;
        }

        public void run() {
            listener.onReceiveProgress(entity, progress);
            entity = null;
            listener = null;
        }
    }

    private class SendDeliveryRunnable implements Runnable {
        private SendPacket entity;
        private float progress;

        public SendDeliveryRunnable(SendPacket entity, float progress) {
            this.entity = entity;
            this.progress = progress;
        }


        public void run() {
            if (!entity.isCanceled()) {
                entity.getListener().onSendProgress(progress);
            }

            entity = null;
        }
    }

}
