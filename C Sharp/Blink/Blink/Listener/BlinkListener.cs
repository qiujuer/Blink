using Net.Qiujuer.Blink.Core;
using System.Net.Sockets;
namespace Net.Qiujuer.Blink.Listener
{
    /// <summary>
    /// Blink notify listener
    /// </summary>
    public interface BlinkListener
    {       
        /// <summary>
        /// On Receive or Sender error by socket err call this
        /// </summary>
        void OnBlinkDisconnect();
    }
}
