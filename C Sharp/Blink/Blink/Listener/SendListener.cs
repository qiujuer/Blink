
namespace Net.Qiujuer.Blink.Listener
{
    /// <summary>
    ///  Send notify listener
    /// </summary>
    public interface SendListener
    {
        void OnSendStart();

        void OnSendProgress(float progress);

        void OnSendEnd(bool isSuccess);
    }
}
