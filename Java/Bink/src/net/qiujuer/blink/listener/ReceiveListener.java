package net.qiujuer.blink.listener;

import net.qiujuer.blink.core.ReceivePacket;

/**
 * Receive notify listener
 */
public interface ReceiveListener {
    void onReceiveStart(int type, long id);

    void onReceiveProgress(int type, long id, int total, int cur);

    void onReceiveEnd(ReceivePacket entity);
}
