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
package net.qiujuer.blink.core;

/**
 * Receive Packet
 */
public abstract class ReceivePacket extends Packet {
    private final long mId;
    private String mHash;

    public ReceivePacket(long id, byte type, long len) {
        super(type);
        mId = id;
        mLength = len;
    }

    public long getId() {
        return mId;
    }

    /**
     * Set Packet Hash Code
     *
     * @param hashCode HashCode
     */
    public void setHash(String hashCode) {
        mHash = hashCode;
    }

    /**
     * Get Packet Hash Code
     *
     * @return HashCode
     */
    public String getHash() {
        return mHash;
    }

    /**
     * On Receiver receive same info buffer call this
     *
     * @param buffer Info data
     * @param offset Buffer offset
     * @param count  Buffer Count
     */
    public void writeInfo(byte[] buffer, int offset, int count) {
    }

    /**
     * Receiver write buffer
     *
     * @param buffer Buffer
     * @param offset Buffer offset
     * @param count  Buffer Count
     */
    public abstract void write(byte[] buffer, int offset, int count);
}
