using System;
using System.Net.Sockets;
namespace Net.Qiujuer.Blink.Core
{
    /**
     * Receive entity interface
     */
    public interface Receiver : IDisposable
    {
        /// <summary>
        /// Get receive buffer size
        /// </summary>
        /// <returns>Buffer Size</returns>
        int GetBufferSize();

        /// <summary>
        /// Async receive some data to buffer
        /// </summary>
        /// <param name="e">SocketAsyncEventArgs</param>
        /// <returns>Status</returns>
        bool ReceiveAsync(SocketAsyncEventArgs e);
    }
}
