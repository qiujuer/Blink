using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    public interface ReceiveDelivery
    {
        /// <summary>
        /// Parses a start response from the receiver.
        /// </summary>
        /// <param name="entity"></param>
        void PostReceiveStart(ReceivePacket entity);

        /// <summary>
        /// Parses a end response from the receiver.
        /// </summary>
        /// <param name="entity">ReceivePacket</param>
        /// <param name="isSuccess">isSuccess</param>
        void PostReceiveEnd(ReceivePacket entity, bool isSuccess);

        /// <summary>
        /// Parses a progress response from the receiver.
        /// </summary>
        /// <param name="entity">ReceivePacket</param>
        /// <param name="progress">Receive progress</param>
        void PostReceiveProgress(ReceivePacket entity, float progress);
    }
}
