package net.qiujuer.blink.box;

import net.qiujuer.blink.core.ReceivePacket;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * String receive class
 */
public class StringReceivePacket extends ReceivePacket<String> {
    public StringReceivePacket(long id, int type, int len) {
        super(id, type, len);
    }

    @Override
    protected void adjustStream() {
        mOutStream = new ByteArrayOutputStream(getLength());
    }

    @Override
    protected void adjustPacket() {
        if (mOutStream != null) {
            byte[] bytes = ((ByteArrayOutputStream) mOutStream).toByteArray();
            try {
                mEntity = new String(bytes, 0, bytes.length, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mOutStream = null;
        }
    }
}
