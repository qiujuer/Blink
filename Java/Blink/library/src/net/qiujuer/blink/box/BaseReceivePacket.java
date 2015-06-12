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

import net.qiujuer.blink.core.ReceivePacket;

import java.io.OutputStream;

/**
 * The Receive base class
 */
public abstract class BaseReceivePacket<T> extends ReceivePacket {
    protected OutputStream mStream;
    protected T mEntity;

    public BaseReceivePacket(long id, byte type, long len) {
        super(id, type, len);
    }

    public T getEntity() {
        return mEntity;
    }

    @Override
    public void write(byte[] buffer, int offset, int count) {
        OutputStream stream = mStream;
        if (stream != null) {
            try {
                stream.write(buffer, offset, count);
                stream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void closeStream() {
        OutputStream stream = mStream;
        mStream = null;
        if (stream != null) {
            try {
                stream.flush();
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
