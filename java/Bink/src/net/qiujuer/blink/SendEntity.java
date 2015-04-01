package net.qiujuer.blink;

import net.qiujuer.blink.listener.SendListener;

import java.io.InputStream;

/**
 * Send Entity
 */
public abstract class SendEntity<T> extends Entity implements
		Comparable<SendEntity<T>> {
	protected final SendListener mListener;
	protected final T mEntity;
	private boolean mCanceled;
	private BlinkConn mBlinkConn;
	private Priority mPriority = Priority.NORMAL;

	public SendEntity(int type, T entity, SendListener listener) {
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

	public SendEntity<?> setBlinkConn(BlinkConn blinkConn) {
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
	 * @param priority
	 *            Priority {@link Priority}
	 */
	public void setPriority(Priority priority) {
		mPriority = priority;
	}

	/**
	 * Our comparator sorts from high to low priority, and secondarily by
	 * sequence number to provide FIFO ordering.
	 */
	@Override
	public int compareTo(SendEntity<T> other) {
		Priority left = this.getPriority();
		Priority right = other.getPriority();

		// High-priority requests are "lesser" so they are sorted to the front.
		// Equal priorities are sorted by sequence number to provide FIFO
		// ordering.
		return left == right ? 0 : right.ordinal() - left.ordinal();
	}

}
