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
        protected readonly SendListener mListener;
        private bool mCanceled;
        private BlinkConn mBlinkConn;

        public SendPacket(int type, SendListener listener)
            : base(type)
        {
            mListener = listener;
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

        /// <summary>
        /// 返回需要发送的头部信息
        /// 当参数错误时，返回Null
        /// 并取消发送
        /// </summary>
        /// <returns></returns>
        public virtual IList<ArraySegment<byte>> GetHeadInfo()
        {
            long length = GetLength();
            if (length <= 0)
                return null;

            IList<ArraySegment<byte>> info = new List<ArraySegment<byte>>();

            byte[] head = new byte[5];

            // Type
            head[0] = (byte)GetType();
            // Length
            byte[] lenBytes = BitConverter.GetBytes((length));
            lenBytes.CopyTo(head, 1);

            info.Add(new ArraySegment<byte>(head));
            return info;
        }

        public abstract Stream GetInputStream();

        public SendPacket SetBlinkConn(BlinkConn blinkConn)
        {
            mBlinkConn = blinkConn;
            return this;
        }

        public void DeliverStart()
        {
            if (mListener != null)
            {
                mListener.OnSendStart();
            }
        }

        public void DeliverProgress(float progress)
        {
            if (mListener != null)
            {
                mListener.OnSendProgress(progress);
            }
        }

        public void DeliverEnd()
        {
            mBlinkConn = null;
            if (mListener != null)
            {
                mListener.OnSendEnd(IsSucceed());
            }
        }
    }
}
