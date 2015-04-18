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
    public interface BlinkParser
    {
        /// <summary>
        /// Parse receiver receive data
        /// </summary>
        /// <param name="type">Data type</param>
        /// <param name="len">Data Len</param>
        /// <returns>ReceivePacket</returns>
        ReceivePacket ParseReceive(byte type, long len);
    }
}
