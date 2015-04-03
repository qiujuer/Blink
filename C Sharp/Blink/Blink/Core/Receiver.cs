
namespace Net.Qiujuer.Blink.Core
{
    /**
     * Receive entity interface
     */
    public interface Receiver
    {
        /**
     * Receive the entity's information
     *
     * @return ReceiveEntity
     */
        ReceivePacket ReceiveHead();


        /**
         * Receive entity
         *
         * @param entity   ReceiveEntity
         * @param delivery ReceiveDelivery
         * @return ReceiveEntity
         */
        bool ReceiveEntity(ReceivePacket entity, ReceiveDelivery delivery);

        /**
         * Destroy the receive IO stream
         */
        void DestroyReceiveIO();
    }
}
