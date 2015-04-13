
using Net.Qiujuer.Blink.Tool;
using System.Net.Sockets;
namespace Net.Qiujuer.Blink.Core
{
    /**
     * Receive entity interface
     */
    public interface IReceiver : IDestroy
    {
        int GetBufferSize();
        bool ReceiveAsync(SocketAsyncEventArgs e);
    }
}
