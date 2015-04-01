package net.qiujuer.blink.box;

import net.qiujuer.blink.ReceiveEntity;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * String receive class
 */
public class StringReceiveEntity extends ReceiveEntity<String> {
    public StringReceiveEntity(long id, int type, int len) {
        super(id, type, len);
    }

    @Override
    public void initOutputStream() {
        mOutStream = new ByteArrayOutputStream(getLength());
    }

    @Override
    public void adjustResult() {
        if (mOutStream != null) {
            byte[] bytes = ((ByteArrayOutputStream) mOutStream).toByteArray();
            try {
                mResult = new String(bytes, 0, bytes.length, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mOutStream = null;
        }
    }
}
