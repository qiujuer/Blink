/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/16/2015
 * Changed 04/19/2015
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
package net.qiujuer.blink.kit;

import net.qiujuer.blink.core.Resource;

import java.io.File;
import java.io.IOException;

/**
 * Disk resource implements Resource{@link Resource}
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
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


        createPath();
        clear();
    }

    private void createPath() {
        // Create
        if (!mRootDirectory.exists()) {
            if (!mRootDirectory.mkdirs()) {
                BlinkLog.e("Unable to create resource dir %s", mRootDirectory.getAbsolutePath());
            }
        }
    }

    @Override
    public File create(long id) {
        //UUID.randomUUID();
        File file = new File(mRootDirectory, String.format("%1$s_%2$d.blink", mMark, id));

        if (!file.exists())
            createPath();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

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
