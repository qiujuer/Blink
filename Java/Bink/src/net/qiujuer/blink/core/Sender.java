package net.qiujuer.blink.core;

/**
 * Send entity interface
 */
public interface Sender {

    /**
     * Send the entity's information
     *
     * @param entity SendEntity
     * @return Status
     */
    boolean sendHead(SendPacket entity);

    /**
     * Send entity
     *
     * @param entity SendEntity
     * @return Status
     */
    boolean sendEntity(SendPacket entity, SendDelivery delivery);

    /**
     * Destroy the sender IO stream
     */
    void destroySendIO();
}
