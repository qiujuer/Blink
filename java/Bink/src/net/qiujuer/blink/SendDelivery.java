package net.qiujuer.blink;

/**
 * Send delivery interface
 */
public interface SendDelivery {
    /**
     * Parses a start response from the sender.
     */
    public void postSendStart(SendEntity entity);

    /**
     * Parses a end response from the sender.
     */
    public void postSendEnd(SendEntity entity, boolean isSuccess);

    /**
     * Parses a progress response from the sender.
     */
    public void postSendProgress(SendEntity entity, int total, int cur);
}
