using Net.Qiujuer.Blink.Core;
using System;
using System.IO;

namespace Net.Qiujuer.Blink.Box
{
    public abstract class BaseReceivePacket<T> : ReceivePacket
    {
        protected Stream mStream;
        protected T mEntity;

        public BaseReceivePacket(long id, byte type, long len)
            : base(id, type, len)
        {
        }

        public T GetEntity()
        {
            return mEntity;
        }

        internal override void Write(byte[] buffer, int offset, int count)
        {
            Stream stream = mStream;
            if (stream != null)
            {
                try
                {
                    stream.Write(buffer, offset, count);
                    stream.Flush();
                }
                catch (Exception) { }
            }
        }

        protected void CloseStream()
        {
            Stream stream = mStream;
            mStream = null;
            if (stream != null)
            {
                try
                {
                    stream.Flush();
                    stream.Dispose();
                    stream.Close();
                }
                catch (Exception) { }
            }
        }
    }
}
