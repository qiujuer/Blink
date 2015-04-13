using Net.Qiujuer.Blink.Tool;
using System.Net.Sockets;

namespace Net.Qiujuer.Blink.Core
{
    public interface ISender : IDestroy
    {
        int GetBufferSize();
        bool SendAsync(SocketAsyncEventArgs e);
    }
}
