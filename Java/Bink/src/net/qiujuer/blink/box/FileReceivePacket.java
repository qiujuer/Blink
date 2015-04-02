package net.qiujuer.blink.box;

import net.qiujuer.blink.core.ReceivePacket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * File receive class
 */
public class FileReceivePacket extends ReceivePacket<File> {
    public FileReceivePacket(long id, int type, int len, File file) {
        super(id, type, len);
        mEntity = file;
    }

    @Override
    protected void adjustStream() {
        try {
            mOutStream = new FileOutputStream(mEntity);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void adjustPacket() {
        mOutStream = null;
    }
}
