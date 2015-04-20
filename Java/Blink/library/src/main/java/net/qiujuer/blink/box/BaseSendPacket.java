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

import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.listener.SendListener;

import java.io.InputStream;

/**
 * The Send base class
 */
public abstract class BaseSendPacket<T> extends SendPacket {
    protected T mEntity;
    protected InputStream mStream;

    public BaseSendPacket(T entity, byte type, SendListener listener) {
        super(type, listener);
        mEntity = entity;
    }


    public T getEntity() {
        return mEntity;
    }

    @Override
    public int read(byte[] buffer, int offset, int count) {
        InputStream stream = mStream;
        if (stream == null)
            return -1;
        try {
            return stream.read(buffer, offset, count);
        } catch (Exception e) {
            return -1;
        }
    }

    protected void closeStream() {
        InputStream stream = mStream;
        mStream = null;
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
