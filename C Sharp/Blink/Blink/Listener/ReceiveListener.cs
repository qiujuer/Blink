using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Listener
{
    /// <summary>
    /// Receive notify listener
    /// </summary>
    public interface ReceiveListener
    {
        /// <summary>
        /// On Receiver receive new packet call this
        /// </summary>
        /// <param name="type">Packet Type</param>
        /// <param name="id">Packet Id</param>
        void OnReceiveStart(byte type, long id);

        /// <summary>
        /// Receiver receive packet progress
        /// </summary>
        /// <param name="packet">ReceivePacket</param>
        /// <param name="progress">Receive Progress</param>
        void OnReceiveProgress(ReceivePacket packet, float progress);

        /// <summary>
        /// On Receiver end receive packet call this
        /// </summary>
        /// <param name="packet">ReceivePacket</param>
        void OnReceiveEnd(ReceivePacket packet);
    }
}
