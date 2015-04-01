package net.qiujuer.blink;

import net.qiujuer.blink.listener.ReceiveListener;

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
    public void postSendStart(SendEntity entity) {
        mPoster.execute(new SendDeliveryRunnable(entity, null, false));
    }

    @Override
    public void postSendEnd(SendEntity entity, boolean isSuccess) {
        entity.setSuccess(isSuccess);
        mPoster.execute(new SendDeliveryRunnable(entity, null, true));
    }

    @Override
    public void postSendProgress(SendEntity entity, int total, int cur) {
        ProgressStatus status = new ProgressStatus(total, cur);
        mPoster.execute(new SendDeliveryRunnable(entity, status, false));
    }

    @Override
    public void postReceiveStart(ReceiveEntity entity) {
        mPoster.execute(new ReceiveDeliveryRunnable(getReceiveListener(), entity, null, false));
    }

    @Override
    public void postReceiveEnd(ReceiveEntity entity, boolean isSuccess) {
        entity.setSuccess(isSuccess);
        mPoster.execute(new ReceiveDeliveryRunnable(getReceiveListener(), entity, null, true));
    }

    @Override
    public void postReceiveProgress(ReceiveEntity entity, int total, int cur) {
        ProgressStatus status = new ProgressStatus(total, cur);
        mPoster.execute(new ReceiveDeliveryRunnable(getReceiveListener(), entity, status, false));
    }

    private class ReceiveDeliveryRunnable implements Runnable {
        private ReceiveListener listener;
        private ReceiveEntity entity;
        private ProgressStatus status;
        private boolean isEnd;

        public ReceiveDeliveryRunnable(ReceiveListener listener, ReceiveEntity entity, ProgressStatus status, boolean isEnd) {
            this.listener = listener;
        	this.entity = entity;
            this.status = status;
            this.isEnd = isEnd;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (status != null)
                listener.onReceiveProgress(entity.getType(), entity.getId(), status.total, status.cur);
            else if (isEnd)
                listener.onReceiveEnd(entity);
            else
                listener.onReceiveStart(entity.getType(), entity.getId());

            entity = null;
            status = null;
            listener = null;
        }
    }


    private class SendDeliveryRunnable implements Runnable {
        private SendEntity entity;
        private ProgressStatus status;
        private boolean isEnd;

        public SendDeliveryRunnable(SendEntity entity, ProgressStatus status, boolean isEnd) {
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
