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
