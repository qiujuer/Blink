package net.qiujuer.blink.core;

import net.qiujuer.blink.listener.SendListener;

import java.io.InputStream;

/**
 * Send Entity
 */
public abstract class SendPacket<T> extends BlinkPacket<T> implements Comparable<SendPacket<T>> {
    protected final SendListener mListener;
    private boolean mCanceled;
    private BlinkConn mBlinkConn;
    private Priority mPriority = Priority.NORMAL;

    public SendPacket(int type, T entity, SendListener listener) {
        super(type);
        mEntity = entity;
        mListener = listener;
    }

    public void cancel() {
        mCanceled = true;
        if (mBlinkConn != null) {
            mBlinkConn.cancel(this);
            mBlinkConn = null;
        }
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public abstract InputStream getInputStream();

    public SendPacket<?> setBlinkConn(BlinkConn blinkConn) {
        mBlinkConn = blinkConn;
        return this;
    }

    public void deliverStart() {
        if (mListener != null) {
            mListener.onSendStart();
        }
    }

    public void deliverProgress(int total, int cur) {
        if (mListener != null) {
            mListener.onSendProgress(total, cur);
        }
    }

    public void deliverEnd() {
        mBlinkConn = null;
        if (mListener != null) {
            mListener.onSendEnd(isSucceed());
        }
    }

    /**
     * Priority values. Requests will be processed from higher priorities to
     * lower priorities, in FIFO order.
     */
    public enum Priority {
        LOW, NORMAL, HIGH, IMMEDIATE
    }

    /**
     * Returns the {@link Priority} of this send entity; {@link Priority#NORMAL}
     * by default.
     */
    public Priority getPriority() {
        return mPriority;
    }

    /**
     * Set the send queue priority
     *
     * @param priority Priority {@link Priority}
     */
    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    /**
     * Our comparator sorts from high to low priority, and secondarily by
     * sequence number to provide FIFO ordering.
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(SendPacket<T> other) {
        if (other == null)
            return 0;
        Priority left = this.getPriority();
        Priority right = other.getPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO
        // ordering.
        return left == right ? 0 : right.ordinal() - left.ordinal();
    }

}
