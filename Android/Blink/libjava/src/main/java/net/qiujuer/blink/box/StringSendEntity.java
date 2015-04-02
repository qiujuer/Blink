package net.qiujuer.blink.box;

import net.qiujuer.blink.listener.SendListener;

import java.io.UnsupportedEncodingException;

/**
 * String send class
 */
public class StringSendEntity extends ByteSendEntity {

    public StringSendEntity(String entity) throws UnsupportedEncodingException {
        this(entity, null);
    }

    public StringSendEntity(String entity, SendListener listener) throws UnsupportedEncodingException {
        super(entity.getBytes("UTF-8"), listener);
        setType(Type.STRING);
    }
}
