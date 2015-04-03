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
    /// Blink receive packet parse
    /// </summary>
    public class ReceiveParser
    {
        private long mId = 0;
        protected Resource mResource;

        public ReceiveParser(Resource resource)
        {
            mResource = resource;
        }

        public ReceivePacket ParseReceive(int type, int len)
        {
            long id = ++mId;
            ReceivePacket packet = null;
            switch (type)
            {
                case Type.STRING:
                    packet = new StringReceivePacket(id, type, len);
                    break;
                case Type.BYTES:
                    packet = new ByteReceivePacket(id, type, len); ;
                    break;
                case Type.FILE:
                    String file = mResource.Create(id);
                    if (file != null)
                        packet = new FileReceivePacket(id, type, len, file);
                    break;
            }
            return packet;
        }

        public static class Type
        {
            public const int STRING = 0;
            public const int BYTES = 1;
            public const int FILE = 2;
        }
    }
}
