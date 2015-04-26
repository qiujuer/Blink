package net.qiujuer.blink.core;

import net.qiujuer.blink.async.IoEventArgs;

/**
 * Blink io filter use to packet with buffer
 * Format packet to IoEventArgs {@link IoEventArgs}
 */
public abstract class PacketFilter {
    // Send an Receive packet head size
    public final static int HEAD_SIZE = 11;
    public final static int STATUS_NEED = -1;
    public final static int STATUS_START = 2;
    public final static int STATUS_END = 4;

    protected IoEventArgs mArgs;

    public void setEventArgs(IoEventArgs args) {
        mArgs = args;
    }
}
