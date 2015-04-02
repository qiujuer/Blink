package net.qiujuer.blink.core;

/**
 * Receive entity interface
 */
public interface Receiver {

    /**
     * Receive the entity's information
     *
     * @return ReceiveEntity
     */
    ReceivePacket<?> receiveHead();


    /**
     * Receive entity
     *
     * @param entity   ReceiveEntity
     * @param delivery ReceiveDelivery
     * @return ReceiveEntity
     */
    boolean receiveEntity(ReceivePacket<?> entity, ReceiveDelivery delivery);

    /**
     * Destroy the receive IO stream
     */
    void destroyReceiveIO();
}
