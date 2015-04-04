using Net.Qiujuer.Blink.Listener;

namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Receiver delivery interface
    /// </summary>
    public interface IReceiveDelivery
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
        void PostReceiveProgress(ReceivePacket entity, int total, int cur);
    }
}
