using Net.Qiujuer.Blink.Listener;
using System;
using System.IO;
using System.Text;

namespace Net.Qiujuer.Blink.Box
{
    public class FileSendPacket : BaseSendPacket<FileInfo>
    {
        public FileSendPacket(FileInfo file)
            : this(file, null)
        {
        }

        public FileSendPacket(FileInfo entity, SendListener listener)
            : base(entity, PacketType.FILE, listener)
        {
            mLength = mEntity.Length;
        }

        internal override bool StartPacket()
        {
            try
            {
                mStream = mEntity.OpenRead();
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        internal override void EndPacket()
        {
            CloseStream();
        }

        public override short ReadInfo(byte[] buffer, int index)
        {
            try
            {
                byte[] bytes = Encoding.UTF8.GetBytes(mEntity.Name);
                bytes.CopyTo(buffer, index);
                return Convert.ToInt16(bytes.Length);
            }
            catch (Exception)
            {
                return 0;
            }
        }
    }
}
