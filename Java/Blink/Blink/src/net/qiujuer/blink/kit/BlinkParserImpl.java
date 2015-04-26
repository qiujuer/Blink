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

import net.qiujuer.blink.box.ByteReceivePacket;
import net.qiujuer.blink.box.FileReceivePacket;
import net.qiujuer.blink.box.StringReceivePacket;
import net.qiujuer.blink.core.BlinkParser;
import net.qiujuer.blink.core.PacketType;
import net.qiujuer.blink.core.ReceivePacket;

import java.io.File;


public class BlinkParserImpl implements BlinkParser {
    private long mId = 0;
    protected Resource mResource;

    public BlinkParserImpl(Resource resource) {
        mResource = resource;
    }

    @Override
    public ReceivePacket parseReceive(byte type, long len) {
        long id = ++mId;
        ReceivePacket packet = null;
        switch (type) {
            case PacketType.STRING:
                packet = new StringReceivePacket(id, type, len);
                break;
            case PacketType.BYTES:
                packet = new ByteReceivePacket(id, type, len);
                break;
            case PacketType.FILE:
                File file = mResource.create(id);
                if (file != null)
                    packet = new FileReceivePacket(id, type, len, file);
                break;
        }
        return packet;
    }
}
