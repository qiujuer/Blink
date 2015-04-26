package net.qiujuer.blink.async;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Blink EventArgs use ByteBuffer {@link ByteBuffer}
 * <p>
 * Use to async socket
 */
public class IoEventArgs {
    private int mCount;
    private int mOffset;
    private int mBytesTransferred;
    private final ByteBuffer mByteBuffer;

    public IoEventArgs(int capacity) {
        mByteBuffer = ByteBuffer.allocate(capacity);
    }

    public byte[] getBuffer() {
        return mByteBuffer.array();
    }

    public void setBuffer(int offset, int count) {
        mCount = count;
        mOffset = offset;
    }

    public int getOffset() {
        return mOffset;
    }

    public int getCount() {
        return mCount;
    }

    public int getBytesTransferred() {
        return mBytesTransferred;
    }

    private void formatBuffer() {
        mByteBuffer.clear();
        mByteBuffer.limit(mOffset + mCount);
        mByteBuffer.position(mOffset);
        mBytesTransferred = 0;
    }

    void send(SocketChannel channel) throws IOException {
        formatBuffer();
        ByteBuffer buffer = mByteBuffer;
        mBytesTransferred = flushChannel(channel, buffer);
        onCompleted(this);
    }

    void receive(SocketChannel channel) throws IOException {
        formatBuffer();
        ByteBuffer buffer = mByteBuffer;
        mBytesTransferred = readChannel(channel, buffer);
        onCompleted(this);
    }

    protected void onCompleted(IoEventArgs e) {

    }

    public static int readChannel(SocketChannel socketChannel, ByteBuffer bb)
            throws IOException {
        int bytesProduced = 0;
        while (bb.hasRemaining()) {
            int len = socketChannel.read(bb);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
            if (len == 0) {
                break;
            }
        }
        return bytesProduced;
    }

    public static int flushChannel(SocketChannel socketChannel, ByteBuffer bb)
            throws IOException {
        int bytesProduced = 0;
        while (bb.hasRemaining()) {
            int len = socketChannel.write(bb);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
            if (len == 0) {
                break;
            }
        }
        return bytesProduced;
    }
}
