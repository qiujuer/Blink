
namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Send delivery interface
    /// </summary>
    public interface ISendDelivery
    {
        /**
         * Parses a start response from the sender.
         */
        void PostSendStart(SendPacket entity);

        /**
         * Parses a end response from the sender.
         */
        void PostSendEnd(SendPacket entity, bool isSuccess);

        /**
         * Parses a progress response from the sender.
         */
        void PostSendProgress(SendPacket entity, float progress);
    }
}
