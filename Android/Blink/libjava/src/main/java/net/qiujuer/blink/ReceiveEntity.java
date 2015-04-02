package net.qiujuer.blink;

import net.qiujuer.blink.Entity;

import java.io.OutputStream;

/**
 * Receive Entity
 */
public abstract class ReceiveEntity<T> extends Entity {
	protected T mResult;
	protected OutputStream mOutStream;

	private final long mId;
	private final int mLength;

	private String mHashCode;

	public ReceiveEntity(long id, int type, int len) {
		super(type);
		mId = id;
		mLength = len;
	}

	public abstract void initOutputStream();

	public long getId() {
		return mId;
	}

	@Override
	public int getLength() {
		return mLength;
	}

	public T getResult() {
		return mResult;
	}

	public OutputStream getOutputStream() {
		return mOutStream;
	}

	public abstract void adjustResult();

	public void setHashCode(String hashCode) {
		mHashCode = hashCode;
	}

	public String getHashCode() {
		return mHashCode;
	}
}
