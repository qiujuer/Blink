using Net.Qiujuer.Blink.Core;
using System.Net.Sockets;
namespace Net.Qiujuer.Blink.Listener
{
    /// <summary>
    /// Receive notify listener
    /// </summary>
    public interface BlinkListener
    {
        void OnReceiveStart(int type, long id);

        void OnReceiveProgress(ReceivePacket paket, float progress);

        void OnReceiveEnd(ReceivePacket paket);

        void OnBlinkDisconnect();
    }
}
