using System;
using System.IO;

namespace Net.Qiujuer.Blink.Box
{
    public class ByteReceivePacket : BaseReceivePacket<byte[]>
    {
        public ByteReceivePacket(long id, byte type, long len)
            : base(id, type, len)
        {
        }

        internal override bool StartPacket()
        {
            try
            {
                mStream = new MemoryStream((int)GetLength());
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        internal override void EndPacket()
        {
            if (mStream != null)
            {
                byte[] bytes = new byte[mStream.Length];
                mStream.Seek(0, SeekOrigin.Begin);
                mStream.Read(bytes, 0, bytes.Length);
                mEntity = bytes;

                CloseStream();
            }
        }
    }
}
