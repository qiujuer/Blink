package net.qiujuer.blink.box;

import net.qiujuer.blink.core.ReceivePacket;

import java.io.ByteArrayOutputStream;

/**
 * Bytes receive class
 */
public class ByteReceivePacket extends ReceivePacket<byte[]> {

    public ByteReceivePacket(long id, int type, int len) {
        super(id, type, len);
    }


    @Override
    protected void adjustStream() {
        mOutStream = new ByteArrayOutputStream(getLength());
    }

    @Override
    protected void adjustPacket() {
        if (mOutStream != null) {
            mEntity = ((ByteArrayOutputStream) mOutStream).toByteArray();
            mOutStream = null;
        }
    }

}
