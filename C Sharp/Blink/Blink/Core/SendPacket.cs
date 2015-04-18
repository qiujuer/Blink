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

        public SendPacket(byte type, SendListener listener)
            : base(type)
        {
            Listener = listener;
        }

        /// <summary>
        /// Cancel the packet to send
        /// If the packet on sending you can't cancel it
        /// But you can cancel sending notify callback
        /// </summary>
        public void Cancel()
        {
            mCanceled = true;
            if (mBlinkConn != null)
            {
                mBlinkConn.Cancel(this);
                mBlinkConn = null;
            }
        }

        /// <summary>
        /// Get the packet iscanceled
        /// </summary>
        /// <returns>Is Canceled</returns>
        public bool IsCanceled()
        {
            return mCanceled;
        }

        /// <summary>
        /// Set The BlinkConn to Cancel from queue
        /// </summary>
        /// <param name="blinkConn">BlinkConn</param>
        /// <returns>SendPacket</returns>
        public SendPacket SetBlinkConn(BlinkConn blinkConn)
        {
            mBlinkConn = blinkConn;
            return this;
        }

        /// <summary>
        /// On Sender send the packet call this to send packet info
        /// The bytes in 0~32767 size
        /// </summary>
        /// <param name="buffer">Send buffer</param>
        /// <param name="index">Buffer start index</param>
        /// <returns>Read to buffer count</returns>
        internal virtual short ReadInfo(byte[] buffer, int index)
        {
            return 0;
        }

        /// <summary>
        /// Sender read some data to send
        /// </summary>
        /// <param name="buffer">Buffer</param>
        /// <param name="offset">Buffer offset</param>
        /// <param name="count">Buffer count</param>
        /// <returns>Read to buffer count</returns>
        internal abstract int Read(byte[] buffer, int offset, int count);

    }
}
