using Net.Qiujuer.Blink;
using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Kit;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace BlinkTest
{
    class Program
    {
        static bool IsExit;
        static void Main(string[] args)
        {
            for (int i = 0; i <= 50000; i++)
            {
                BlinkLog.I("=========" + i + "==========");
                Thread thread = new Thread(Run);
                thread.Start();

                Thread.Sleep(500);
            }
            BlinkLog.I("=========PRESS ANY KEY TO EXIT==========");
            Console.ReadKey();
            IsExit = true;
        }

        static void Run()
        {
            Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            IPAddress HostIp = IPAddress.Parse("127.0.0.1");
            socket.Connect(HostIp, 2626);

            BlinkConn conn = Blink.NewConnection(socket, 1024 * 1024, "D:/", Guid.NewGuid().ToString(), 0.001f, null, null);

            if (conn != null)
            {
                Console.WriteLine("Test Send String...");
                for (int i = 0; i <= 50; i++)
                {
                    string str = "Test String:" + i;
                    conn.Send(str);
                    Console.WriteLine(str);
                    Thread.Sleep(2);
                    if (IsExit)
                    {
                        conn.Dispose();
                        socket.Shutdown(SocketShutdown.Both);
                        socket.Dispose();
                        socket.Close();
                        return;
                    }
                }
            }
        }
    }
}
