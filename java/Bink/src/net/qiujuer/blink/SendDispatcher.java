package net.qiujuer.blink;

import java.util.concurrent.BlockingQueue;

/**
 * Provides a thread for performing send dispatch from a queue of BinkConn {@link BlinkConn}.
 */
public class SendDispatcher extends Thread {
    /**
     * The queue of send entity.
     */
    private final BlockingQueue<SendEntity<?>> mQueue;
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

    public SendDispatcher(BlockingQueue<SendEntity<?>> queue,
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
            SendEntity<?> entity;
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
