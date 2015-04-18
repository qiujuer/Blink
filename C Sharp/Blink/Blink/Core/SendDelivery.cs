using Net.Qiujuer.Blink.Core;
using System;
namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Send delivery interface
    /// </summary>
    public interface SendDelivery : IDisposable
    {
        /// <summary>
        /// Parses a progress response from the sender.
        /// </summary>
        /// <param name="entity">SendPacket</param>
        /// <param name="progress">Send progress</param>
        void PostSendProgress(SendPacket entity, float progress);
    }
}
