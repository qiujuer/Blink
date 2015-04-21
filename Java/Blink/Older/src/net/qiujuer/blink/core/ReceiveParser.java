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
package net.qiujuer.blink.core;

import net.qiujuer.blink.box.ByteReceivePacket;
import net.qiujuer.blink.box.FileReceivePacket;
import net.qiujuer.blink.box.StringReceivePacket;

import java.io.File;

/**
 * Blink receive entity parse
 */
public class ReceiveParser {
    private long mId = 0;
    protected Resource mResource;

    public ReceiveParser(Resource resource) {
        mResource = resource;
    }

    public ReceivePacket<?> parseReceive(int type, int len) {
        final long id = ++mId;
        ReceivePacket<?> entity = null;
        switch (type) {
            case BlinkPacket.Type.STRING:
                entity = new StringReceivePacket(id, type, len);
                break;
            case BlinkPacket.Type.BYTES:
                entity = new ByteReceivePacket(id, type, len);
                break;
            case BlinkPacket.Type.FILE:
                File file = mResource.create(id);
                if (file != null)
                    entity = new FileReceivePacket(id, type, len, file);
                break;
        }
        return entity;
    }
}
