using Net.Qiujuer.Blink;
using Net.Qiujuer.Blink.Box;
using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using System;
using System.Collections.Generic;
using System.Net.Sockets;

namespace Server
{
    class BlinkCallBack : BlinkListener, ReceiveListener
    {
        static List<BlinkCallBack> mBlinkCallBacks = new List<BlinkCallBack>();

        public Socket SocketLink { get; set; }
        public BlinkConn Conn { get; set; }

        public BlinkCallBack(Socket socket)
        {
            SocketLink = socket;
            Conn = Blink.NewConnection(socket,
                2 * 1024 * 1024,
                "D:/",
                Guid.NewGuid().ToString(),
                0.1f,
                this, this);

            mBlinkCallBacks.Add(this);
        }


        public void OnReceiveStart(byte type, long id)
        {
            Console.WriteLine("Receive->start:" + type + " " + id);
        }

        public void OnReceiveProgress(ReceivePacket paket, float progress)
        {
            Console.WriteLine("Receive->progress:" + paket.GetPacketType() + " " + paket.GetId()
                         + " " + paket.GetLength() + " " + progress);
        }

        public void OnReceiveEnd(ReceivePacket paket)
        {
            if (paket.GetPacketType() == BlinkPacket.PacketType.STRING)
                Console.WriteLine("Receive->end: String:"
                        + paket.GetId() + " " + paket.GetLength() + " :"
                        + ((StringReceivePacket)paket).GetEntity());
            else if (paket.GetPacketType() == BlinkPacket.PacketType.BYTES)
                Console.WriteLine("Receive->end: Bytes:"
                        + paket.GetId() + " " + paket.GetLength() + " :"
                        + ((ByteReceivePacket)paket).GetEntity());
            else
                Console.WriteLine("Receive->end: File:"
                        + paket.GetId()
                        + " "
                        + paket.GetLength()
                        + " "
                        + ((FileReceivePacket)paket).GetEntity().FullName
                        + " "
                        + paket.GetHash());
        }


        public void OnBlinkDisconnect()
        {
            Console.WriteLine("BlinkDisconnect");

            this.Conn.Dispose();

            mBlinkCallBacks.Remove(this);
        }
    }
}
