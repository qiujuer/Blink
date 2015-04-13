using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using System;
using System.IO;

namespace Net.Qiujuer.Blink.Box
{
    public abstract class BaseSendPacket<T> : SendPacket
    {
        protected T mEntity;
        protected Stream mStream;

        public BaseSendPacket(T entity, int type, SendListener listener)
            : base(type, listener)
        {
            mEntity = entity;
        }

        public T GetEntity()
        {
            return mEntity;
        }

        public override int Read(byte[] buffer, int offset, int count)
        {
            Stream stream = mStream;
            if (stream == null)
                return -1;
            try
            {
                return stream.Read(buffer, offset, count);
            }
            catch (Exception)
            {
                return -1;
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
                    stream.Dispose();
                    stream.Close();
                }
                catch (Exception) { }
            }
        }
    }
}
