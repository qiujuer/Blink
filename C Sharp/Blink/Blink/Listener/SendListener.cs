
namespace Net.Qiujuer.Blink.Listener
{
    /// <summary>
    ///  Send notify listener
    /// </summary>
    public interface SendListener
    {
        void OnSendStart();

        void OnSendProgress(int total, int cur);

        void OnSendEnd(bool isSuccess);
    }
}
