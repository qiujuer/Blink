using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener.Delivery;
using System;
using System.Collections.Concurrent;
using System.Net.Sockets;

namespace Net.Qiujuer.Blink.Async
{
    /// <summary>
    /// Provides a thread for performing send dispatch from a queue of BinkConn {@link BlinkConn}.
    /// </summary>
    public class SendDispatcher : SocketAsyncEventArgs
    {
        private const int HeadSize = 11;
        /// <summary>
        /// The queue of send entity.
        /// </summary>
        private readonly ConcurrentQueue<SendPacket> mQueue;

        /// <summary>
        /// The sender interface for processing sender requests.
        /// </summary>
        private Sender mSender;

        /// <summary>
        /// Posting send responses.
        /// </summary>
        private SendDelivery mDelivery;

        /// <summary>
        /// Used for telling us to die.
        /// </summary>
        private volatile bool mSending = false;
        private volatile bool mDisposed = false;

        private SendPacket mSendPacket;
        private long mCursor;
        private long mTotal;
        private float mProgress;
        private bool mSendStatus;


        public SendDispatcher(Sender sender, SendDelivery delivery)
        {
            mQueue = new ConcurrentQueue<SendPacket>();
            mSender = sender;
            mDelivery = delivery;

            // Set Buffer
            SetBuffer(new byte[sender.GetBufferSize()], 0, sender.GetBufferSize());
        }

        public void Send(SendPacket entity)
        {
            mQueue.Enqueue(entity);

            if (!mSending)
            {
                mSending = true;

                // Send Next
                SendNext();
            }
        }

        private void SendAsync(int offset, int count)
        {
            if (mDisposed)
                return;

            if (count <= 0)
                SendNext();

            // Set Send Buffer Size
            SetBuffer(offset, count);

            // As soon as the client is connected, post a receive to the connection
            mSendStatus = mSender.SendAsync(this);
            if (!mSendStatus)
            {
                OnCompleted(this);
            }
        }

        private void SendNext()
        {
            // Set Status
            mSendStatus = mCursor == mTotal;


            SendPacket packet = mSendPacket;
            // Set Null
            mSendPacket = null;
            // Notity
            if (packet != null)
            {
                // End
                packet.EndPacket();

                // Post End
                SendDelivery delivery = mDelivery;
                if (delivery != null)
                {
                    packet.SetSuccess(mSendStatus);
                    delivery.PostSendProgress(packet, 1);
                }

                packet = null;
            }

            // Init Size
            mCursor = 0;
            mTotal = 0;

            // Take a request from the queue.
            mSending = mQueue.TryDequeue(out packet);

            if (mSending && packet != null)
            {
                // Cancel
                if (packet.IsCanceled())
                {
                    SendNext();
                }

                // Set Packet
                mSendPacket = packet;

                // Post Start
                SendDelivery delivery = mDelivery;
                if (delivery != null)
                    delivery.PostSendProgress(packet, 0);

                // Init the packet
                packet.StartPacket();

                // Send
                mSendStatus = SendHead(packet);

            }
        }

        private bool SendHead(SendPacket entity)
        {
            mTotal = entity.GetLength();
            if (mTotal <= 0)
                return false;

            // Type
            Buffer[0] = (byte)entity.GetType();

            // Length
            byte[] lenBytes = BitConverter.GetBytes(mTotal);
            lenBytes.CopyTo(Buffer, 1);

            // Info
            short infoLen = entity.ReadInfo(Buffer, HeadSize);
            byte[] infoLenBytes = BitConverter.GetBytes(infoLen);
            infoLenBytes.CopyTo(Buffer, HeadSize - 2);

            SendAsync(0, HeadSize + infoLen);

            return true;
        }

        private void SendEntity()
        {
            SendPacket packet = mSendPacket;
            if (packet != null)
            {
                int count = packet.Read(Buffer, 0, mSender.GetBufferSize());

                mCursor += count;

                // Send
                SendAsync(0, count);

                // Progress
                float progress = (float)Math.Round((float)mCursor / mTotal, 2);
                // Post Callback
                if (mProgress != progress)
                {
                    mProgress = progress;
                    SendDelivery delivery = mDelivery;
                    if (delivery != null)
                        delivery.PostSendProgress(packet, mProgress);
                }
            }
        }

        protected override void OnCompleted(SocketAsyncEventArgs e)
        {
            base.OnCompleted(e);

            // Check if the remote host closed the connection
            if (e.LastOperation == SocketAsyncOperation.Send
                && e.BytesTransferred > 0
                && e.SocketError == SocketError.Success)
            {
                if (e.BytesTransferred < e.Count)
                    SendAsync(e.Offset + e.BytesTransferred, e.Count - e.BytesTransferred);
                else if (mCursor != mTotal)
                    SendEntity();
                else
                    SendNext();
            }
            else
            {
                Dispose();
            }
        }

        public new void Dispose()
        {
            if (!mDisposed)
            {
                mDisposed = true;

                SendPacket packet = mSendPacket;
                mSendPacket = null;

                SendDelivery delivery = mDelivery;
                mDelivery = null;

                Sender sender = mSender;
                mSender = null;

                if (packet != null && delivery != null)
                {
                    if (mCursor < mTotal)
                    {
                        packet.EndPacket();
                        packet.SetSuccess(false);
                        delivery.PostSendProgress(packet, 1);
                    }
                }

                if (sender != null)
                    sender.Dispose();
                try
                {
                    SetBuffer(null, 0, 0);
                }
                catch (Exception) { }

                // Clear
                while (mQueue.TryDequeue(out packet)) { }

                base.Dispose();
            }

        }
    }
}
