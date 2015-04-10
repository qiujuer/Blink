
using System;
namespace Net.Qiujuer.Blink.Core
{
    /**
     * Receive entity interface
     */
    public interface IReceiver : IDisposable
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
        bool ReceiveEntity(ReceivePacket entity, IReceiveDelivery delivery);

    }
}
