package net.qiujuer.blink.box;

import net.qiujuer.blink.ReceiveEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * File receive class
 */
public class FileReceiveEntity extends ReceiveEntity<File> {
    public FileReceiveEntity(long id, int type, int len, File file) {
        super(id, type, len);
        mResult = file;
    }

    @Override
    public void initOutputStream() {
        try {
            mOutStream = new FileOutputStream(mResult);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void adjustResult() {
        mOutStream = null;
    }
}
