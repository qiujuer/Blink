/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/16/2015
 * Changed 04/25/2015
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

/**
 * Blink Data Packet
 */
public abstract class Packet {
    protected byte mType;
    protected long mLength;

    public Packet(byte type) {
        mType = type;
    }

    public byte getPacketType() {
        return mType;
    }

    public long getLength() {
        return mLength;
    }

    /**
     * On Send or Receive start call this
     *
     * @return Init status
     */
    public abstract boolean startPacket();

    /**
     * On Send or Receive end call this
     */
    public abstract void endPacket();
}
