package net.qiujuer.blink.core;

import net.qiujuer.blink.box.ByteReceivePacket;
import net.qiujuer.blink.box.FileReceivePacket;
import net.qiujuer.blink.box.StringReceivePacket;

import java.io.File;

/**
 * Blink receive entity parse
 */
public class ReceiveParser {
    private long mId = 0;
    protected Resource mResource;

    public ReceiveParser(Resource resource) {
        mResource = resource;
    }

    public ReceivePacket<?> parseReceive(int type, int len) {
        final long id = ++mId;
        ReceivePacket<?> entity = null;
        switch (type) {
            case BlinkPacket.Type.STRING:
                entity = new StringReceivePacket(id, type, len);
                break;
            case BlinkPacket.Type.BYTES:
                entity = new ByteReceivePacket(id, type, len);
                break;
            case BlinkPacket.Type.FILE:
                File file = mResource.create(id);
                if (file != null)
                    entity = new FileReceivePacket(id, type, len, file);
                break;
        }
        return entity;
    }
}
