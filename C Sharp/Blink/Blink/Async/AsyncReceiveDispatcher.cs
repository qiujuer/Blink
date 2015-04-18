using Net.Qiujuer.Blink.Core;
using System;
using System.Net.Sockets;

namespace Net.Qiujuer.Blink.Async
{
    /// <summary>
    /// Provides for performing receive dispatch from a queue of BinkConn {@link BlinkConn}.
    /// </summary>
    public class AsyncReceiveDispatcher : AsyncAbsDispatcher
    {
        /// <summary>
        /// Socket receive buffer size
        /// </summary>
        private readonly int mBufferSize;
        /// <summary>
        /// Parser the receive data type
        /// </summary>
        private BlinkParser mParser;
        /// <summary>
        /// Receive Data
        /// </summary>
        private Receiver mReceiver;
        /// <summary>
        /// Posting responses.
        /// </summary>
        private ReceiveDelivery mReceiveDelivery;
        private BlinkDelivery mBlinkDelivery;

        private ReceivePacket mReceivePacket;
        private short mSurplusInfoLen;
        private long mSurplusLen;


        public AsyncReceiveDispatcher(Receiver receiver, BlinkParser parser, ReceiveDelivery receiveDelivery, BlinkDelivery blinkDelivery, float progressPrecision)
            : base(progressPrecision)
        {
            mReceiver = receiver;
            mBufferSize = receiver.GetBufferSize();
            mParser = parser;
            mReceiveDelivery = receiveDelivery;
            mBlinkDelivery = blinkDelivery;

            // Set Buffer
            SetBuffer(new byte[mBufferSize], 0, mBufferSize);

            // Start
            ReceiveAsync(0);
        }

        private void ReceiveAsync(long size)
        {
            int count;
            if (size > mBufferSize)
                count = mBufferSize;
            else if (size <= 0)
                count = HeadSize;
            else
                count = Convert.ToInt32(size);

            ReceiveAsync(0, count);
        }

        private void ReceiveAsync(int offset, int count)
        {
            if (mDisposed)
                return;

            // Set post buffer
            SetBuffer(offset, count);

            // Post a receive to the connection
            mStatus = mReceiver.ReceiveAsync(this);
            if (!mStatus)
            {
                // On sync call
                OnCompleted(this);
            }
        }

        private void ReceiveHead(byte[] buffer)
        {
            mSurplusLen = 0;
            mSurplusInfoLen = 0;
            mProgress = 0;

            byte type = buffer[0];
            long len = BitConverter.ToInt64(buffer, 1);
            short info = BitConverter.ToInt16(buffer, HeadSize - 2);

            if (len > 0)
            {
                // Set Length
                mSurplusLen = len;
                mSurplusInfoLen = info;

                // Parse receive packet
                ReceivePacket packet = mParser.ParseReceive(type, len);

                if (packet != null && packet.StartPacket())
                {
                    mReceivePacket = packet;

                    // Notifly
                    ReceiveDelivery delivery = mReceiveDelivery;
                    if (delivery != null)
                        delivery.PostReceiveStart(packet);

                }
                else
                {
                    // Set Null
                    mReceivePacket = null;
                }
            }

            ReceiveAsync(mSurplusInfoLen > 0 ? mSurplusInfoLen : mSurplusLen);
        }

        private void ReceiveInfo(byte[] buffer, int offset, int count)
        {
            // Set len
            mSurplusInfoLen -= Convert.ToInt16(count);

            if (mSurplusInfoLen > 0)
            {
                // Receive info
                ReceiveAsync(offset + count, mSurplusInfoLen);
            }
            else
            {
                // Set Info
                ReceivePacket packet = mReceivePacket;
                if (packet != null)
                {
                    packet.WriteInfo(buffer, 0, offset + count);
                }

                // Receive entity
                ReceiveAsync(mSurplusLen);
            }
        }

        private void ReceiveEntity(byte[] buffer, int offset, int count)
        {
            // Set len
            mSurplusLen -= count;

            ReceivePacket packet = mReceivePacket;

            if (packet != null)
            {
                packet.Write(buffer, offset, count);


                // Notity progress
                float len = packet.GetLength();
                float progress = (len - mSurplusLen) / len;

                // Post Callback
                ReceiveDelivery delivery = mReceiveDelivery;

                if (IsNotifyProgress(progress))
                {
                    // Notify                    
                    if (delivery != null)
                        delivery.PostReceiveProgress(packet, mProgress);
                }

                if (mSurplusLen <= 0)
                {
                    // End
                    packet.EndPacket();

                    // Notify
                    if (delivery != null)
                        delivery.PostReceiveEnd(packet, mStatus);

                    // Set Null
                    mReceivePacket = null;
                }
            }

            // Receive next entity
            ReceiveAsync(mSurplusLen);
        }

        protected override void OnCompleted(SocketAsyncEventArgs e)
        {
            // Call base
            base.OnCompleted(e);

            // Check if the remote host closed the connection
            if (e.LastOperation == SocketAsyncOperation.Receive
                && e.BytesTransferred > 0
                && e.SocketError == SocketError.Success)
            {
                // Receive Entity
                if (mSurplusInfoLen > 0)
                    ReceiveInfo(e.Buffer, e.Offset, e.BytesTransferred);
                else if (mSurplusLen > 0)
                    ReceiveEntity(e.Buffer, e.Offset, e.BytesTransferred);
                else
                {
                    if (e.BytesTransferred < e.Count)
                        // Full the head
                        ReceiveAsync(e.Offset + e.BytesTransferred, e.Count - e.BytesTransferred);
                    else
                        // Receive Head
                        ReceiveHead(e.Buffer);
                }
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
                mParser = null;

                ReceivePacket packet = mReceivePacket;
                mReceivePacket = null;

                Receiver receiver = mReceiver;
                mReceiver = null;

                ReceiveDelivery receiveDelivery = mReceiveDelivery;
                mReceiveDelivery = null;

                if (packet != null && receiveDelivery != null)
                {
                    if (mSurplusLen > 0)
                    {
                        packet.EndPacket();
                        receiveDelivery.PostReceiveEnd(packet, false);
                    }
                }

                BlinkDelivery blinkDelay = mBlinkDelivery;
                mBlinkDelivery = null;
                if (blinkDelay != null)
                    blinkDelay.PostBlinkDisconnect();

                if (receiver != null)
                    receiver.Dispose();

                SetBuffer(null, 0, 0);

                base.Dispose();
            }
        }
    }
}
