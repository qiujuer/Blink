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
package net.qiujuer.blink.box;

import net.qiujuer.blink.kit.convert.HashConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * File receive class
 */
public class FileReceivePacket extends BaseReceivePacket<File> {
    private String mFileName;
    private MessageDigest mMd5Verification = null;

    public FileReceivePacket(long id, byte type, long len, File file) {
        super(id, type, len);
        mEntity = file;
    }

    @Override
    public boolean startPacket() {

        // Init Md5
        try {
            mMd5Verification = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Init Stream
        try {
            mStream = new FileOutputStream(mEntity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void endPacket() {
        closeStream();

        // Set Name
        String fileName = mFileName;
        mFileName = null;
        if (fileName != null) {
            try {
                File newFile = new File(mEntity.getParent(), fileName);
                if (mEntity.renameTo(newFile))
                    mEntity = newFile;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Hash
        MessageDigest hash = mMd5Verification;
        mMd5Verification = null;
        if (hash != null) {
            setHash(HashConverter.toMd5(hash.digest()));
        }
    }

    @Override
    public void write(byte[] buffer, int offset, int count) {
        super.write(buffer, offset, count);

        // Hash
        MessageDigest hash = mMd5Verification;
        if (hash != null)
            mMd5Verification.update(buffer, offset, count);

    }

    @Override
    public void writeInfo(byte[] buffer, int offset, int count) {
        try {
            mFileName = new String(buffer, offset, count, "UTF-8");
        } catch (Exception e) {
            mFileName = null;
        }
    }
}
