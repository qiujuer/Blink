using Net.Qiujuer.Blink.Listener;
using System;
using System.IO;

namespace Net.Qiujuer.Blink.Box
{
    public class ByteSendPacket : BaseSendPacket<byte[]>
    {
        public ByteSendPacket(byte[] entity)
            : this(entity, null)
        {
        }

        public ByteSendPacket(byte[] entity, SendListener listener) :
            base(entity, PacketType.BYTES, listener)
        {
            mLength = mEntity.Length;
        }

        internal override bool StartPacket()
        {
            try
            {
                mStream = new MemoryStream(mEntity);
                return true;
            }
            catch (ArgumentOutOfRangeException)
            {
                return false;
            }
        }

        internal override void EndPacket()
        {
            CloseStream();
        }
    }
}
