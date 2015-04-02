package net.qiujuer.blink.box;

import net.qiujuer.blink.listener.SendListener;

import java.io.UnsupportedEncodingException;

/**
 * String send class
 */
public class StringSendPacket extends ByteSendPacket {

    public StringSendPacket(String entity) throws UnsupportedEncodingException {
        this(entity, null);
    }

    public StringSendPacket(String entity, SendListener listener) throws UnsupportedEncodingException {
        super(entity.getBytes("UTF-8"), listener);
        mType = Type.STRING;
    }
}
