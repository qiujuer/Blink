package net.qiujuer.blink.box;

import net.qiujuer.blink.SendEntity;
import net.qiujuer.blink.listener.SendListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * File send class
 */
public class FileSendEntity extends SendEntity<File> {

    public FileSendEntity(File file) {
        this(file, null);
    }

    public FileSendEntity(File entity, SendListener listener) {
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
