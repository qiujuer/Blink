using Net.Qiujuer.Blink.Listener;
using System;
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
        private Priority mPriority = Priority.NORMAL;

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

        public void DeliverProgress(int total, int cur)
        {
            if (mListener != null)
            {
                mListener.OnSendProgress(total, cur);
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

        /**
         * Priority values. Requests will be processed from higher priorities to
         * lower priorities, in FIFO order.
         */
        public enum Priority
        {
            LOW, NORMAL, HIGH, IMMEDIATE
        }

        /**
         * Returns the {@link Priority} of this send entity; {@link Priority#NORMAL}
         * by default.
         */
        public Priority GetPriority()
        {
            return mPriority;
        }

        /**
         * Set the send queue priority
         *
         * @param priority Priority {@link Priority}
         */
        public void SetPriority(Priority priority)
        {
            mPriority = priority;
        }

        /**
         * Our comparator sorts from high to low priority, and secondarily by
         * sequence number to provide FIFO ordering.
         */
        public int compareTo(SendPacket other)
        {
            if (other == null)
                return 0;
            Priority left = this.GetPriority();
            Priority right = other.GetPriority();

            // High-priority requests are "lesser" so they are sorted to the front.
            // Equal priorities are sorted by sequence number to provide FIFO
            // ordering.
            //return left == right ? 0 : right.ordinal() - left.ordinal();
            return 0;
        }
    }
}
