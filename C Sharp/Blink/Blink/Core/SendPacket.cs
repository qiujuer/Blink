using Net.Qiujuer.Blink.Listener;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    public abstract class SendPacket : BlinkPacket
    {
        public SendListener Listener { get; private set; }
        private bool mCanceled;
        private BlinkConn mBlinkConn;

        public SendPacket(int type, SendListener listener)
            : base(type)
        {
            Listener = listener;
        }

        public void Cancel()
        {
            mCanceled = true;
            if (mBlinkConn != null)
            {
                mBlinkConn.Cancel(this);
                mBlinkConn = null;
            }
        }

        public bool IsCanceled()
        {
            return mCanceled;
        }

        public virtual short ReadInfo(byte[] buffer, int index)
        {
            return 0;
        }

        public abstract int Read(byte[] buffer, int offset, int count);

        public SendPacket SetBlinkConn(BlinkConn blinkConn)
        {
            mBlinkConn = blinkConn;
            return this;
        }
    }
}
