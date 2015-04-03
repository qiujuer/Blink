using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Sample
{
    class Program
    {
        static void Main(string[] args)
        {
            Socket LinkSocket = null;
            Socket serverSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                IPAddress HostIp = IPAddress.Any;
                IPEndPoint iep = new IPEndPoint(HostIp, 2626);
                serverSocket.Bind(iep);
            }
            catch (Exception)
            {

            }
            while (true)
            {
                try
                {
                    serverSocket.Listen(10);
                    LinkSocket = serverSocket.Accept();
                    BlinkConn conn = Utils.bindBlink(LinkSocket);
                    conn.GetResource().ClearAll();
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                }
            }
        }
    }
}
