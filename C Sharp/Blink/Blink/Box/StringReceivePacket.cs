using System;
using System.IO;

namespace Net.Qiujuer.Blink.Box
{
    public class StringReceivePacket : BaseReceivePacket<String>
    {
        public StringReceivePacket(long id, int type, long len)
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
                mEntity = System.Text.Encoding.UTF8.GetString(bytes, 0, bytes.Length);

                CloseStream();
            }
        }
    }
}
