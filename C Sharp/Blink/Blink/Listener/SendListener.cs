
namespace Net.Qiujuer.Blink.Listener
{
    /// <summary>
    ///  Send notify listener
    /// </summary>
    public interface SendListener
    {
        /// <summary>
        /// On sender send the packet call this
        /// On start progress == 0
        /// On end progress ==1
        /// </summary>
        /// <param name="progress">Send progress (0~1)</param>
        void OnSendProgress(float progress);
    }
}
