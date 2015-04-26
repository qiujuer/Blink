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
package net.qiujuer.blink;

import net.qiujuer.blink.core.ReceiveDelivery;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.core.SendDelivery;
import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.core.listener.ReceiveListener;

import java.util.concurrent.Executor;

/**
 * Delivers send and receive responses.
 */
public class ExecutorDelivery extends ReceiveDelivery implements SendDelivery {
    /**
     * Used for posting responses, typically to the main thread.
     */
    private final Executor mPoster;

    public ExecutorDelivery(Executor executor, ReceiveListener listener) {
        super(listener);
        mPoster = executor;
    }

    @Override
    public void postSendStart(SendPacket entity) {
        mPoster.execute(new SendDeliveryRunnable(entity, null, false));
    }

    @Override
    public void postSendEnd(SendPacket entity, boolean isSuccess) {
        entity.setSuccess(isSuccess);
        mPoster.execute(new SendDeliveryRunnable(entity, null, true));
    }

    @Override
    public void postSendProgress(SendPacket entity, int total, int cur) {
        ProgressStatus status = new ProgressStatus(total, cur);
        mPoster.execute(new SendDeliveryRunnable(entity, status, false));
    }

    @Override
    public void postReceiveStart(ReceivePacket entity) {
        mPoster.execute(new ReceiveDeliveryRunnable(getReceiveListener(), entity, null, false));
    }

    @Override
    public void postReceiveEnd(ReceivePacket entity, boolean isSuccess) {
        entity.setSuccess(isSuccess);
        mPoster.execute(new ReceiveDeliveryRunnable(getReceiveListener(), entity, null, true));
    }

    @Override
    public void postReceiveProgress(ReceivePacket entity, int total, int cur) {
        ProgressStatus status = new ProgressStatus(total, cur);
        mPoster.execute(new ReceiveDeliveryRunnable(getReceiveListener(), entity, status, false));
    }

    private class ReceiveDeliveryRunnable implements Runnable {
        private ReceiveListener listener;
        private ReceivePacket entity;
        private ProgressStatus status;
        private boolean isEnd;

        public ReceiveDeliveryRunnable(ReceiveListener listener, ReceivePacket entity, ProgressStatus status, boolean isEnd) {
            this.listener = listener;
            this.entity = entity;
            this.status = status;
            this.isEnd = isEnd;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (listener != null) {
                if (status != null)
                    listener.onReceiveProgress(entity.getType(), entity.getId(), status.total, status.cur);
                else if (isEnd)
                    listener.onReceiveEnd(entity);
                else
                    listener.onReceiveStart(entity.getType(), entity.getId());
            }

            entity = null;
            status = null;
            listener = null;
        }
    }


    private class SendDeliveryRunnable implements Runnable {
        private SendPacket entity;
        private ProgressStatus status;
        private boolean isEnd;

        public SendDeliveryRunnable(SendPacket entity, ProgressStatus status, boolean isEnd) {
            this.entity = entity;
            this.status = status;
            this.isEnd = isEnd;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (!entity.isCanceled()) {
                if (status != null)
                    entity.deliverProgress(status.total, status.cur);
                else if (isEnd)
                    entity.deliverEnd();
                else
                    entity.deliverStart();
            }

            entity = null;
            status = null;
        }
    }

    /**
     * Send progress status
     */
    private class ProgressStatus {
        int total;
        int cur;

        ProgressStatus(int total, int cur) {
            this.total = total;
            this.cur = cur;
        }
    }
}
