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

import net.qiujuer.blink.listener.ReceiveListener;

/**
 * Receiver delivery interface
 */
public abstract class ReceiveDelivery {
    private final ReceiveListener mListener;

    public ReceiveDelivery(ReceiveListener listener) {
        mListener = listener;
    }


    /**
     * Get ReceiveListener
     *
     * @return ReceiveListener
     */
    protected ReceiveListener getReceiveListener() {
        return mListener;
    }

    /**
     * Parses a start response from the receiver.
     */
    public abstract void postReceiveStart(ReceivePacket entity);

    /**
     * Parses a end response from the receiver.
     */
    public abstract void postReceiveEnd(ReceivePacket entity, boolean isSuccess);

    /**
     * Parses a progress response from the receiver.
     */
    public abstract void postReceiveProgress(ReceivePacket entity, int total, int cur);
}
