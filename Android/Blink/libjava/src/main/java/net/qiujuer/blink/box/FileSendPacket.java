package net.qiujuer.blink.box;

import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.listener.SendListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * File send class
 */
public class FileSendPacket extends SendPacket<File> {

    public FileSendPacket(File file) {
        this(file, null);
    }

    public FileSendPacket(File entity, SendListener listener) {
        super(Type.FILE, entity, listener);
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(mEntity);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public int getLength() {
        return (int) mEntity.length();
    }
}
