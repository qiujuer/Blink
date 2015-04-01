package net.qiujuer.blink.box;

import net.qiujuer.blink.ReceiveEntity;

import java.io.ByteArrayOutputStream;

/**
 * Bytes receive class
 */
public class ByteReceiveEntity extends ReceiveEntity<byte[]> {

    public ByteReceiveEntity(long id, int type, int len) {
        super(id, type, len);
    }


    @Override
    public void initOutputStream() {
        mOutStream = new ByteArrayOutputStream(getLength());
    }

    @Override
    public void adjustResult() {
        if (mOutStream != null) {
            mResult = ((ByteArrayOutputStream) mOutStream).toByteArray();
            mOutStream = null;
        }
    }

}
