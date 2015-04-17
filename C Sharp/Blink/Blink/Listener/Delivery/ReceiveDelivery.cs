using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using System;

namespace Net.Qiujuer.Blink.Listener.Delivery
{
    /// <summary>
    /// Receiver delivery interface
    /// </summary>
    public interface ReceiveDelivery : IDisposable
    {

        /**
         * Parses a start response from the receiver.
         */
        void PostReceiveStart(ReceivePacket entity);

        /**
         * Parses a end response from the receiver.
         */
        void PostReceiveEnd(ReceivePacket entity, bool isSuccess);

        /**
         * Parses a progress response from the receiver.
         */
        void PostReceiveProgress(ReceivePacket entity, float progress);

        /// <summary>
        /// On Socket Disconnect Post CallBack
        /// </summary>
        void PostBlinkDisconnect();
    }
}
