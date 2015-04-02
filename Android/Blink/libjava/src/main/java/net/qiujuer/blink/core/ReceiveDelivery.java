package net.qiujuer.blink.core;

import net.qiujuer.blink.listener.ReceiveListener;

/**
 * Receiver delivery interface
 */
public abstract class ReceiveDelivery {
    private final ReceiveListener mListener;

    public ReceiveDelivery(ReceiveListener listener) {
        mListener = listener;
    }


    /**
     * Get ReceiveListener
     *
     * @return ReceiveListener
     */
    protected ReceiveListener getReceiveListener() {
        return mListener;
    }

    /**
     * Parses a start response from the receiver.
     */
    public abstract void postReceiveStart(ReceivePacket entity);

    /**
     * Parses a end response from the receiver.
     */
    public abstract void postReceiveEnd(ReceivePacket entity, boolean isSuccess);

    /**
     * Parses a progress response from the receiver.
     */
    public abstract void postReceiveProgress(ReceivePacket entity, int total, int cur);
}
