using System;
using System.Net.Sockets;

namespace Net.Qiujuer.Blink.Core
{
    public interface Sender : IDisposable
    {
        /// <summary>
        /// Get socket send buffer size
        /// </summary>
        /// <returns>Buffer Size</returns>
        int GetBufferSize();

        /// <summary>
        /// Async send some byte
        /// </summary>
        /// <param name="e">SocketAsyncEventArgs</param>
        /// <returns>Status</returns>
        bool SendAsync(SocketAsyncEventArgs e);
    }
}
