using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Tool;
namespace Net.Qiujuer.Blink.Listener.Delivery
{
    /// <summary>
    /// Send delivery interface
    /// </summary>
    public interface ISendDelivery : IDestroy
    {
        /**
         * Parses a progress response from the sender.
         */
        void PostSendProgress(SendPacket entity, float progress);
    }
}
