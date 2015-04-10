using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Box
{
    public class FileReceivePacket : BaseReceivePacket<FileInfo>
    {
        public FileReceivePacket(long id, int type, long len, String path)
            : base(id, type, len)
        {
            mEntity = new FileInfo(path);
        }

        internal override void AdjustStream()
        {
            // do....
            mOutStream = mEntity.OpenWrite();

        }

        internal override void AdjustPacket()
        {
            mOutStream.Close();
            mOutStream = null;
        }
    }
}
