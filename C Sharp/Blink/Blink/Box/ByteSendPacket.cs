using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Box
{
    public class ByteSendPacket : BaseSendPacket<byte[]>
    {
        public ByteSendPacket(byte[] entity)
            : this(entity, null)
        {
        }

        public ByteSendPacket(byte[] entity, SendListener listener) :
            base(entity, ReceiveParser.Type.BYTES, listener)
        {
            mLength = mEntity.Length;
        }

        public override Stream GetInputStream()
        {
            return new MemoryStream(mEntity);
        }
    }
}
