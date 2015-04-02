/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 03/31/2015
 * Changed 04/02/2015
 * Version 1.0.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.blink;

import net.qiujuer.blink.core.Resource;

import java.io.File;
import java.io.IOException;

/**
 * Disk resource implements Resource{@link Resource}
 */
public class DiskResource implements Resource<String> {
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

        // Clear this
        clear();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public File create(long id) {
        File file = new File(mRootDirectory, String.format("%1$s_%2$d", mMark, id));

        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
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
        BlinkLog.d("Resource cleared with mark: " + mMark);
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

    @Override
    public String getMark() {
        return mMark;
    }
}
