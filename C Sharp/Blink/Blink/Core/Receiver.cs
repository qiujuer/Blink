using System;
using System.Net.Sockets;
namespace Net.Qiujuer.Blink.Core
{
    /**
     * Receive entity interface
     */
    public interface Receiver : IDisposable
    {
        int GetBufferSize();
        bool ReceiveAsync(SocketAsyncEventArgs e);
    }
}
