package net.qiujuer.blink.listener;

/**
 * Send notify listener
 */
public interface SendListener {
    void onSendStart();

    void onSendProgress(int total, int cur);

    void onSendEnd(boolean isSuccess);
}