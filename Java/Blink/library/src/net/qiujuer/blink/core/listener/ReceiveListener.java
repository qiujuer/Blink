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
package net.qiujuer.blink.core.listener;

import net.qiujuer.blink.core.Connector;
import net.qiujuer.blink.core.ReceivePacket;

/**
 * Receive notify listener
 */
public interface ReceiveListener {
    /**
     * On Receiver receive new packet call this
     *
     * @param connector Connector
     * @param packet    ReceivePacket
     */
    void onReceiveStart(Connector connector, ReceivePacket packet);

    /**
     * Receiver receive packet progress
     *
     * @param connector Connector
     * @param packet    ReceivePacket
     * @param progress  Receive Progress
     */
    void onReceiveProgress(Connector connector, ReceivePacket packet, float progress);

    /**
     * On Receiver end receive packet call this
     *
     * @param connector Connector
     * @param packet    ReceivePacket
     */
    void onReceiveCompleted(Connector connector, ReceivePacket packet);
}
