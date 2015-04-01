package net.qiujuer.blink.box;

import net.qiujuer.blink.SendEntity;
import net.qiujuer.blink.listener.SendListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Bytes send class
 */
public class ByteSendEntity extends SendEntity<byte[]> {

    public ByteSendEntity(byte[] entity) {
        this(entity, null);
    }

    public ByteSendEntity(byte[] entity, SendListener listener) {
        super(Type.BYTES, entity, listener);
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(mEntity);
    }


    @Override
    public int getLength() {
        return mEntity.length;
    }

}
