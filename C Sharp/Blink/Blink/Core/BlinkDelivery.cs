using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using System;

namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Receiver delivery interface
    /// </summary>
    public interface BlinkDelivery : IDisposable
    {        

        /// <summary>
        /// On Socket Disconnect Post CallBack
        /// </summary>
        void PostBlinkDisconnect();
    }
}
