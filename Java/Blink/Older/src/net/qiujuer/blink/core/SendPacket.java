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

import net.qiujuer.blink.core.listener.SendListener;

import java.io.InputStream;

/**
 * Send Entity
 */
public abstract class SendPacket<T> extends BlinkPacket<T> implements Comparable<SendPacket<T>> {
    protected final SendListener mListener;
    private boolean mCanceled;
    private BlinkConn mBlinkConn;
    private Priority mPriority = Priority.NORMAL;

    public SendPacket(int type, T entity, SendListener listener) {
        super(type);
        mEntity = entity;
        mListener = listener;
    }

    public void cancel() {
        mCanceled = true;
        if (mBlinkConn != null) {
            mBlinkConn.cancel(this);
            mBlinkConn = null;
        }
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public abstract InputStream getInputStream();

    public SendPacket<?> setBlinkConn(BlinkConn blinkConn) {
        mBlinkConn = blinkConn;
        return this;
    }

    public void deliverStart() {
        if (mListener != null) {
            mListener.onSendStart();
        }
    }

    public void deliverProgress(int total, int cur) {
        if (mListener != null) {
            mListener.onSendProgress(total, cur);
        }
    }

    public void deliverEnd() {
        mBlinkConn = null;
        if (mListener != null) {
            mListener.onSendEnd(isSucceed());
        }
    }

    /**
     * Priority values. Requests will be processed from higher priorities to
     * lower priorities, in FIFO order.
     */
    public enum Priority {
        LOW, NORMAL, HIGH, IMMEDIATE
    }

    /**
     * Returns the {@link Priority} of this send entity; {@link Priority#NORMAL}
     * by default.
     */
    public Priority getPriority() {
        return mPriority;
    }

    /**
     * Set the send queue priority
     *
     * @param priority Priority {@link Priority}
     */
    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    /**
     * Our comparator sorts from high to low priority, and secondarily by
     * sequence number to provide FIFO ordering.
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(SendPacket<T> other) {
        if (other == null)
            return 0;
        Priority left = this.getPriority();
        Priority right = other.getPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO
        // ordering.
        return left == right ? 0 : right.ordinal() - left.ordinal();
    }

}
