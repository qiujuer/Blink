/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/24/2015
 * Changed 04/24/2015
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
package net.qiujuer.blink.async;

import net.qiujuer.blink.core.PacketFilter;
import net.qiujuer.blink.kit.Disposable;

import java.util.concurrent.atomic.AtomicBoolean;


public abstract class AsyncDispatcher extends IoEventArgs implements Disposable {
    // Is Disposed
    protected final AtomicBoolean mDisposed = new AtomicBoolean(false);
    // Notify progress precision
    protected final float mProgressPrecision;
    // Notify progress
    protected float mProgress = 0;

    public AsyncDispatcher(int capacity, float progressPrecision) {
        super(capacity);
        mProgressPrecision = progressPrecision;
    }

    protected boolean isNotifyProgress(float newProgress) {
        if (newProgress == PacketFilter.STATUS_START) {
            mProgress = 0;
            return true;
        } else if (newProgress == PacketFilter.STATUS_END) {
            mProgress = 1;
            return true;
        } else if ((newProgress - mProgress) > mProgressPrecision) {
            mProgress = newProgress;
            return true;
        } else {
            return false;
        }
    }
}
