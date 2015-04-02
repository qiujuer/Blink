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
package net.qiujuer.blink.box;

import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.listener.SendListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Bytes send class
 */
public class ByteSendPacket extends SendPacket<byte[]> {

    public ByteSendPacket(byte[] entity) {
        this(entity, null);
    }

    public ByteSendPacket(byte[] entity, SendListener listener) {
        super(Type.BYTES, entity, listener);
        mLength = mEntity.length;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(mEntity);
    }
}
