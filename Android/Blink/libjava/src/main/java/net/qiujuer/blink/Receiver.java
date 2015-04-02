package net.qiujuer.blink;

/**
 * Receive entity interface
 */
public interface Receiver {

    /**
     * Receive the entity's information
     *
     * @return ReceiveEntity
     */
    ReceiveEntity<?> receiveHead();


    /**
     * Receive entity
     *
     * @param entity   ReceiveEntity
     * @param delivery ReceiveDelivery
     * @return ReceiveEntity
     */
    boolean receiveEntity(ReceiveEntity<?> entity, ReceiveDelivery delivery);

    /**
     * Destroy the receive IO stream
     */
    void destroyReceiveIO();
}
