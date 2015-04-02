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

/**
 * Send delivery interface
 */
public interface SendDelivery {
    /**
     * Parses a start response from the sender.
     */
    void postSendStart(SendPacket entity);

    /**
     * Parses a end response from the sender.
     */
    void postSendEnd(SendPacket entity, boolean isSuccess);

    /**
     * Parses a progress response from the sender.
     */
    void postSendProgress(SendPacket entity, int total, int cur);
}
