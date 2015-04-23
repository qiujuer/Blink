using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Concurrent;
using System.Net.Sockets;

namespace Net.Qiujuer.Blink.Async
{
    /// <summary>
    /// Provides for performing send dispatch from a queue of BinkConn {@link BlinkConn}.
    /// </summary>
    public class AsyncSendDispatcher : AsyncAbsDispatcher
    {
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

        private SendPacket mPacket;
        private long mCursor;
        private long mTotal;


        public AsyncSendDispatcher(Sender sender, SendDelivery delivery, float progressPrecision)
            : base(progressPrecision)
        {
            mQueue = new ConcurrentQueue<SendPacket>();
            mSender = sender;
            mDelivery = delivery;

            // Set Buffer
            SetBuffer(new byte[sender.GetBufferSize()], 0, sender.GetBufferSize());
        }

        /// <summary>
        /// Add a packet to queue to send
        /// </summary>
        /// <param name="packet">SendPacket</param>
        public void Send(SendPacket packet)
        {
            mQueue.Enqueue(packet);

            lock (this)
            {
                if (!mSending)
                {
                    mSending = true;

                    // Send Next
                    SendNext();
                }
            }
        }

        /// <summary>
        /// Delete a packet from queue to cancel
        /// </summary>
        /// <param name="packet">SendPacket</param>
        public void Cancel(SendPacket packet)
        {
            // Wait.....
        }

        /// <summary>
        /// Notify send progress
        /// </summary>
        private void NotifyProgress()
        {
            SendPacket packet = mPacket;
            SendDelivery delivery = mDelivery;

            if (packet != null && delivery != null)
            {
                // Progress
                float progress = (float)mCursor / mTotal;
                // Post Callback
                if (IsNotifyProgress(progress))
                {
                    delivery.PostSendProgress(packet, mProgress);
                }
            }
        }

        /// <summary>
        /// Send next packet
        /// </summary>
        private void SendNext()
        {
            SendPacket packet = mPacket;
            mPacket = null;
            // Notify
            if (packet != null)
            {

                // Set Status
                mStatus = mCursor == mTotal;

                // End
                packet.SetSuccess(mStatus);
                packet.EndPacket();

                // Post End
                mCursor = mTotal;
                NotifyProgress();
            }

            // Take a request from the queue.
            mSending = mQueue.TryDequeue(out packet);

            if (mSending && packet != null)
            {
                // Cancel
                if (packet.IsCanceled())
                {
                    SendNext();
                }
                else
                {
                    // Set Packet
                    mPacket = packet;

                    // Start Send
                    SendHead();
                }
            }
        }

        /// <summary>
        /// Init send data head info
        /// </summary>
        private void SendHead()
        {
            // Init Head
            int size;
            mTotal = mPacket.GetLength();
            if (mTotal <= 0)
                size = 0;
            else
            {
                // Type
                Buffer[0] = mPacket.GetPacketType();

                // Length
                byte[] lenBytes = BitConverter.GetBytes(mTotal);
                lenBytes.CopyTo(Buffer, 1);

                // Info
                short infoLen = mPacket.ReadInfo(Buffer, HeadSize);
                byte[] infoLenBytes = BitConverter.GetBytes(infoLen);
                infoLenBytes.CopyTo(Buffer, HeadSize - 2);

                size = HeadSize + infoLen;
            }
            // Check packet size
            if (size > 0)
            {
                // Init Size
                mCursor = 0;
                mProgress = 0;

                // Post Start
                NotifyProgress();

                // Init the packet
                mPacket.StartPacket();

                // Send Head
                SendAsync(0, size);
            }
            else
            {

                // Send next
                mPacket = null;
                SendNext();
            }
        }

        /// <summary>
        /// Start packet entity
        /// </summary>
        private void SendEntity()
        {
            SendPacket packet = mPacket;
            if (packet != null)
            {
                int count = packet.Read(Buffer, 0, mSender.GetBufferSize());

                mCursor += count;

                // Send
                SendAsync(0, count);


                SendDelivery delivery = mDelivery;
                if (delivery != null)
                {
                    // Progress
                    float progress = (float)mCursor / mTotal;
                    // Post Callback
                    if (IsNotifyProgress(progress))
                        delivery.PostSendProgress(packet, mProgress);
                }
            }

        }

        /// <summary>
        /// Start asynchronous send buffer
        /// </summary>
        /// <param name="offset">buffer offset</param>
        /// <param name="count">send size</param>
        private void SendAsync(int offset, int count)
        {
            if (mDisposed)
                return;

            if (count <= 0)
                SendNext();

            // Set Send Buffer Size
            SetBuffer(offset, count);

            // As soon as the client is connected, post a receive to the connection
            mStatus = mSender.SendAsync(this);
            if (!mStatus)
            {
                OnCompleted(this);
            }
        }

        /// <summary>
        /// On asynchronous send end callback
        /// </summary>
        /// <param name="e">SocketAsyncEventArgs</param>
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
            if (!IsDisposed())
            {
                SendPacket packet = mPacket;
                mPacket = null;

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
