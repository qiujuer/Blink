using Net.Qiujuer.Blink.Core;
using System;
namespace Net.Qiujuer.Blink.Listener.Delivery
{
    /// <summary>
    /// Send delivery interface
    /// </summary>
    public interface SendDelivery : IDisposable
    {
        /**
         * Parses a progress response from the sender.
         */
        void PostSendProgress(SendPacket entity, float progress);
    }
}
