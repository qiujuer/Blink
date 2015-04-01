package net.qiujuer.blink;

import java.io.File;
import java.io.IOException;

/**
 * Disk resource implements Resource{@link Resource}
 */
public class DiskResource implements Resource {
    /**
     * A unique identifier on the Resource clear
     */
    private final String mMark;
    /**
     * The root directory to use for the resource.
     */
    private final File mRootDirectory;

    public DiskResource(File rootDirectory, String mark) throws Exception {
        if (mark == null || mark.trim().length() == 0)
            throw new NullPointerException("Mark is not allow null.");

        mRootDirectory = rootDirectory;
        mMark = mark;

        if (!mRootDirectory.exists()) {
            if (!mRootDirectory.mkdirs()) {
                BlinkLog.e("Unable to create resource dir %s", mRootDirectory.getAbsolutePath());
                throw new Exception("Unable to create resource dir.");
            }
        }

        clear();
    }

    @Override
    public File create(long id) {
        //UUID.randomUUID();
    	File file = new File(mRootDirectory, String.format("%1$s_%2$d", mMark, id));
        
        if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        
        return file;
    }

    @Override
    public boolean rename(File file, String newName) {
        return file.renameTo(new File(mRootDirectory, newName));
    }

    @Override
    public File cut(File oldFile, String newPath) {
        return cut(oldFile, new File(newPath));
    }

    @Override
    public File cut(File oldFile, File newFile) {
        if (oldFile.renameTo(newFile))
            return newFile;
        else
            return oldFile;
    }

    @Override
    public void remove(String name) {
        remove(new File(mRootDirectory, name));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void remove(File file) {
        try {
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void clear() {
        File[] files = mRootDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().contains(mMark))
                    file.delete();
            }
        }
        BlinkLog.d("Resource cleared.");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void clearAll() {
        File[] files = mRootDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        BlinkLog.d("Resource cleared path.");
    }
}
