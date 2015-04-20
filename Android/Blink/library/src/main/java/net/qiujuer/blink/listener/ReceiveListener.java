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
package net.qiujuer.blink.listener;

import net.qiujuer.blink.core.ReceivePacket;

/**
 * Receive notify listener
 */
public interface ReceiveListener {
    /**
     * On Receiver receive new packet call this
     *
     * @param type Packet Type
     * @param id   Packet Id
     */
    void onReceiveStart(byte type, long id);

    /**
     * Receiver receive packet progress
     *
     * @param packet   ReceivePacket
     * @param progress Receive Progress
     */
    void onReceiveProgress(ReceivePacket packet, float progress);

    /**
     * On Receiver end receive packet call this
     *
     * @param packet ReceivePacket
     */
    void onReceiveEnd(ReceivePacket packet);
}
