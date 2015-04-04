using Net.Qiujuer.Blink;
using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Sample
{
    class Program
    {
        static Socket mServer;
        static List<BlinkConn> mBlinkConn = new List<BlinkConn>();
        static Thread mThread;
        static bool IsExit = false;

        static void Main(string[] args)
        {

            mServer = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                IPAddress HostIp = IPAddress.Any;
                IPEndPoint iep = new IPEndPoint(HostIp, 2626);
                BlinkLog.I("Start Server Socket...");
                mServer.Bind(iep);
                mServer.Listen(26);


                mThread = new Thread(Run);
                mThread.Start();

            }
            catch (Exception)
            {
                BlinkLog.E("Start Server Error.");
            }


            BlinkLog.I("=========PRESS ANY KEY TO EXIT==========");
            Console.ReadKey();

            IsExit = true;

            if (mThread != null)
            {
                mThread.Interrupt();
            }

            foreach (var b in mBlinkConn)
            {
                b.Destroy();
            }
            mServer.Dispose();
            mServer.Close();

        }

        static void Run()
        {
            while (!IsExit)
            {
                try
                {
                    BlinkLog.I("Server Socket Accept...");
                    Socket socket = mServer.Accept();
                    BlinkLog.V("New Client Socket.");
                    BlinkConn conn = Utils.bindBlink(socket);
                    conn.GetResource().ClearAll();
                    mBlinkConn.Add(conn);
                    BlinkLog.V("Socket To BlinkConn OK.");
                }
                catch (Exception e)
                {
                    BlinkLog.E(e.ToString());
                }
            }
        }
    }
}
