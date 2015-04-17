using Net.Qiujuer.Blink;
using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Kit;
using Net.Qiujuer.Blink.Listener;
using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace Client
{
    class Program
    {
        static Socket mSocket;
        static BlinkConn mBlinkConn = null;

        static void Main(string[] args)
        {
            BlinkLog.I("=========PRESS ENTER IP eg(127.0.0.1)==========");
            string ip = Console.ReadLine();

            IPAddress hostIp;
            if (!IPAddress.TryParse(ip, out hostIp))
            {
                Console.ReadKey();
                return;
            }

            try
            {
                mSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                mSocket.Connect(hostIp, 2626);
                mBlinkConn = Blink.NewConnection(mSocket, 1024 * 1024, "D:/", Guid.NewGuid().ToString(), null);

            }
            catch (Exception)
            {
                BlinkLog.I("LINK TO: " + hostIp + " ERROR.");
                Console.ReadKey();
                return;
            }

            BlinkLog.I("=========PRESS ENTER 'E' TO EXIT==========");

            SendWhlie();


            if (mBlinkConn != null)
                mBlinkConn.Dispose();
            if (mSocket != null)
            {
                mSocket.Dispose();
                mSocket.Close();
            }
        }

        static void SendWhlie()
        {

            while (true)
            {
                BlinkLog.V("=====Enter same str or file path to send server:=====");

                string str = Console.ReadLine();
                if (str == "E")
                    return;
                Send(str);
            }
        }

        static void Send(string str)
        {
            try
            {
                FileInfo info = new FileInfo(str);
                if (info.Exists)
                {
                    mBlinkConn.Send(info, new SendCallBack());
                }

                else
                {
                    mBlinkConn.Send(str);
                    BlinkLog.I("Send String To Server.");
                }
            }
            catch (Exception e)
            {
                BlinkLog.E(e.Message);
            }
        }
    }
}
