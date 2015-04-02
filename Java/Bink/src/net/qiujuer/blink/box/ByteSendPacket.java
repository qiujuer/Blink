package net.qiujuer.blink.box;

import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.listener.SendListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Bytes send class
 */
public class ByteSendPacket extends SendPacket<byte[]> {

    public ByteSendPacket(byte[] entity) {
        this(entity, null);
    }

    public ByteSendPacket(byte[] entity, SendListener listener) {
        super(Type.BYTES, entity, listener);
        mLength = mEntity.length;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(mEntity);
    }
}
