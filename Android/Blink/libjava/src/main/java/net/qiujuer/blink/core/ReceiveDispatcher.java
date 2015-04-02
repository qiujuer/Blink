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
