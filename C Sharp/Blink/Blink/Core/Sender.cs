using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    public interface Sender
    {
        /**
     * Send the entity's information
     *
     * @param entity SendEntity
     * @return Status
     */
        bool SendHead(SendPacket entity);

        /**
         * Send entity
         *
         * @param entity SendEntity
         * @return Status
         */
        bool SendEntity(SendPacket entity, SendDelivery delivery);

        /**
         * Destroy the sender IO stream
         */
        void DestroySendIO();
    }
}
