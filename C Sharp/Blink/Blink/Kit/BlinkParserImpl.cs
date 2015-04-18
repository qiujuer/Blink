using Net.Qiujuer.Blink.Box;
using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Kit
{

    public class BlinkParserImpl : BlinkParser
    {
        private long mId = 0;
        protected Resource mResource;

        public BlinkParserImpl(Resource resource)
        {
            mResource = resource;
        }

        public ReceivePacket ParseReceive(byte type, long len)
        {
            long id = ++mId;
            ReceivePacket packet = null;
            switch (type)
            {
                case BlinkPacket.PacketType.STRING:
                    packet = new StringReceivePacket(id, type, len);
                    break;
                case BlinkPacket.PacketType.BYTES:
                    packet = new ByteReceivePacket(id, type, len); ;
                    break;
                case BlinkPacket.PacketType.FILE:
                    String file = mResource.Create(id);
                    if (file != null)
                        packet = new FileReceivePacket(id, type, len, file);
                    break;
            }
            return packet;
        }
    }
}
