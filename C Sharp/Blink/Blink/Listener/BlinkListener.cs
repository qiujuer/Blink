
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

        void OnReceiveProgress(int type, long id, int total, int cur);

        void OnReceiveEnd(ReceivePacket paket);

        //void OnSocketError(SocketException e);
    }
}
