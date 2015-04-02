package net.qiujuer.blink.core;

import java.io.OutputStream;

/**
 * Receive Entity
 */
public abstract class ReceivePacket<T> extends BlinkPacket<T> {
    private final long mId;
    protected OutputStream mOutStream;
    private String mHashCode;

    public ReceivePacket(long id, int type, int len) {
        super(type);
        mId = id;
        mLength = len;
    }

    public long getId() {
        return mId;
    }

    public OutputStream getOutputStream() {
        return mOutStream;
    }

    public void setHashCode(String hashCode) {
        mHashCode = hashCode;
    }

    public String getHashCode() {
        return mHashCode;
    }

    protected abstract void adjustStream();

    protected abstract void adjustPacket();
}
