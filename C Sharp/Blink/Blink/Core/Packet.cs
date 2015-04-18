
namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Bink Packet Interface
    /// </summary>
    public interface Packet
    {
        /// <summary>
        /// Get packet type
        /// </summary>
        /// <returns>Type</returns>
        byte GetPacketType();

        /// <summary>
        /// Get packet length
        /// </summary>
        /// <returns>Size</returns>
        long GetLength();

        /// <summary>
        /// Set packet send or receive status
        /// </summary>
        /// <param name="isSuccess">Status</param>
        void SetSuccess(bool isSuccess);

        /// <summary>
        /// Get the packet is succeed
        /// </summary>
        /// <returns>Status</returns>
        bool IsSucceed();
    }
}
