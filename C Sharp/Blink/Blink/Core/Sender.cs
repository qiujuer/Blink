using System;
using System.Net.Sockets;

namespace Net.Qiujuer.Blink.Core
{
    public interface Sender : IDisposable
    {
        int GetBufferSize();
        bool SendAsync(SocketAsyncEventArgs e);
    }
}
