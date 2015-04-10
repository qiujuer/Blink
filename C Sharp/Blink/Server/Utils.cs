using Net.Qiujuer.Blink;
using Net.Qiujuer.Blink.Box;
using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using Net.Qiujuer.Blink.Tool;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Sample
{
    static class Utils
    {
        public static BlinkConn bindBlink(Socket socket)
        {
            // Create a async thread to callback listener
            CallBack callback = new CallBack();
            return Blink.newConnection(socket, 4 * 1024 * 1024, "D:/Blink/", Guid.NewGuid().ToString(), callback);
        }
    }
}
