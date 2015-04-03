
using Net.Qiujuer.Blink.Core;
namespace Net.Qiujuer.Blink.Listener
{
    /// <summary>
    /// Receive notify listener
    /// </summary>
    public interface ReceiveListener
    {
        void OnReceiveStart(int type, long id);

        void OnReceiveProgress(int type, long id, int total, int cur);

        void OnReceiveEnd(ReceivePacket paket);
    }
}
