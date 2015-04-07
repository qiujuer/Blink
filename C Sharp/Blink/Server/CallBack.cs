using Net.Qiujuer.Blink.Box;
using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using Net.Qiujuer.Blink.Tool;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Sample
{
    class CallBack : BlinkListener, Executor
    {
        public void Execute(Runnable command)
        {
            command.Run();
        }

        public void OnReceiveStart(int type, long id)
        {
            Console.WriteLine("Receive->start:" + type + " " + id);
        }

        public void OnReceiveProgress(int type, long id, int total, int cur)
        {
            Console.WriteLine("Receive->progress:" + type + " " + id
                         + " " + total + " " + cur);
        }

        public void OnReceiveEnd(Net.Qiujuer.Blink.Core.ReceivePacket paket)
        {
            if (paket.GetType() == BlinkPacket.Type.STRING)
                Console.WriteLine("Receive->end: String:"
                        + paket.GetId() + " " + paket.GetLength() + " :"
                        + ((StringReceivePacket)paket).GetEntity());
            else if (paket.GetType() == BlinkPacket.Type.BYTES)
                Console.WriteLine("Receive->end: Bytes:"
                        + paket.GetId() + " " + paket.GetLength() + " :"
                        + ((ByteReceivePacket)paket).GetEntity());
            else
                Console.WriteLine("Receive->end: File:"
                        + paket.GetId()
                        + " "
                        + paket.GetLength()
                        + " :"
                        + ((FileReceivePacket)paket).GetEntity()
                        .FullName + " " + paket.GetHashCode());
        }
    }
}
