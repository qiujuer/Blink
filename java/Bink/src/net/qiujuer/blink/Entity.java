package net.qiujuer.blink;

/**
 * Blink send and receive datagram entity
 */
public abstract class Entity {
    private int mType;
    private boolean mSucceed;

    public Entity(int type) {
        mType = type;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public void setSuccess(boolean isSuccess) {
        this.mSucceed = isSuccess;
    }

    public boolean isSucceed() {
        return mSucceed;
    }

    public abstract int getLength();

    /**
     * Blink Entity Type
     */
    public interface Type {
        int STRING = 0;
        int BYTES = 1;
        int FILE = 2;
    }
}
