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
    public class FileSendPacket : BaseSendPacket<FileInfo>
    {
        public FileSendPacket(FileInfo file)
            : this(file, null)
        {
        }

        public FileSendPacket(FileInfo entity, SendListener listener)
            : base(entity, Type.BYTES, listener)
        {
            mLength = (int)mEntity.Length;
        }

        public override Stream GetInputStream()
        {
            try
            {
                return mEntity.OpenRead();
            }
            catch (FileNotFoundException e)
            {
                Console.WriteLine(e.Message);
                return null;
            }
        }
    }
}
