package net.qiujuer.blink.core;

/**
 * Blink Data Packet
 */
public abstract class BlinkPacket<T> extends EntityNode<T> implements HeadNode {
    protected int mType;
    protected int mLength;
    private boolean mSucceed;

    public BlinkPacket(int type) {
        mType = type;
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public int getLength() {
        return mLength;
    }

    public void setSuccess(boolean isSuccess) {
        this.mSucceed = isSuccess;
    }

    public boolean isSucceed() {
        return mSucceed;
    }

    /**
     * Blink Entity Type
     */
    public interface Type {
        int STRING = 0;
        int BYTES = 1;
        int FILE = 2;
    }


}
