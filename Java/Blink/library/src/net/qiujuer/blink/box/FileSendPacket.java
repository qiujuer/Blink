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

import net.qiujuer.blink.core.PacketType;
import net.qiujuer.blink.core.listener.SendListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * File send class
 */
public class FileSendPacket extends BaseSendPacket<File> {

    public FileSendPacket(File file) {
        this(file, null);
    }

    public FileSendPacket(File entity, SendListener listener) {
        super(entity, PacketType.FILE, listener);
        mLength = mEntity.length();
    }

    @Override
    public boolean startPacket() {
        try {
            mStream = new FileInputStream(mEntity);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    @Override
    public void endPacket() {
        closeStream();
    }

    @Override
    public byte[] getInfo() {
        try {
            return mEntity.getName().getBytes("UTF-8");
        } catch (Exception e) {
            return null;
        }
    }
}
