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
package net.qiujuer.blink.core.listener;

import net.qiujuer.blink.core.SendPacket;

/**
 * Send notify listener
 */
public interface SendListener {

    /**
     * On start send the packet call
     *
     * @param packet SendPacket
     */
    void onSendStart(SendPacket packet);


    /**
     * On the packet send progress changed call
     *
     * @param packet   SendPacket
     * @param progress Progress (0~1)
     */
    void onSendProgress(SendPacket packet, float progress);

    /**
     * On send completed call
     *
     * @param packet SendPacket
     */
    void onSendCompleted(SendPacket packet);
}
