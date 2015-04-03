using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Box
{
    public class StringReceivePacket : BaseReceivePacket<String>
    {
        public StringReceivePacket(long id, int type, int len)
            : base(id, type, len)
        {

        }

        internal override void AdjustStream()
        {
            mOutStream = new MemoryStream(GetLength());
        }

        internal override void AdjustPacket()
        {
            if (mOutStream != null)
            {
                byte[] bytes = new byte[mOutStream.Length];
                mOutStream.Seek(0, SeekOrigin.Begin);
                mOutStream.Read(bytes, 0, bytes.Length);
                mEntity = System.Text.Encoding.UTF8.GetString(bytes, 0, bytes.Length);
                mOutStream = null;
            }
        }
    }
}
