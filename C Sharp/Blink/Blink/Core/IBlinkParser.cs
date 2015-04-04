using Net.Qiujuer.Blink.Box;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Blink parse interface
    /// </summary>
    public interface IBlinkParser
    {
        ReceivePacket ParseReceive(int type, int len);
    }
}
