using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener.Delivery;
using Net.Qiujuer.Blink.Tool;
using System;
using System.Net.Sockets;

namespace Net.Qiujuer.Blink.Async
{
    public class ReceiveDispatcher : SocketAsyncEventArgs, IDestroy
    {
        private const int HeadSize = 11;
        /// <summary>
        /// Socket receive buffer size
        /// </summary>
        private readonly int mBufferSize;
        /// <summary>
        /// Parser the receive data type
        /// </summary>
        private IBlinkParser mParser;
        /// <summary>
        /// Receive Data
        /// </summary>
        private IReceiver mReceiver;
        /// <summary>
        /// Posting responses.
        /// </summary>
        private IReceiveDelivery mDelivery;

        private volatile bool mDestroied = false;

        private ReceivePacket mReceivePacket;
        private short mSurplusInfoLen;
        private long mSurplusLen;
        private float mProgress;
        private bool mReceiveStatus;


        public ReceiveDispatcher(IReceiver receiver, IBlinkParser parser, IReceiveDelivery delivery)
        {
            mReceiver = receiver;
            mBufferSize = receiver.GetBufferSize();
            mParser = parser;
            mDelivery = delivery;

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
            if (mDestroied)
                return;

            // Set post buffer
            SetBuffer(offset, count);

            // Post a receive to the connection
            mReceiveStatus = mReceiver.ReceiveAsync(this);
            if (!mReceiveStatus)
            {
                // On sync call
                OnCompleted(this);
            }
        }

        private void ReceiveHead(byte[] buffer)
        {
            mSurplusLen = 0;
            mSurplusInfoLen = 0;
            int type = buffer[0];
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
                    IReceiveDelivery delivery = mDelivery;
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
                long len = packet.GetLength();
                float progress = (float)Math.Round((float)(len - mSurplusLen) / len, 8);

                // Post Callback

                IReceiveDelivery delivery = mDelivery;

                if (mProgress != progress)
                {
                    mProgress = progress;

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
                        delivery.PostReceiveEnd(packet, mReceiveStatus);

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
                Destroy();
            }
        }

        public void Destroy()
        {
            if (!mDestroied)
            {
                mDestroied = true;

                mParser = null;

                ReceivePacket packet = mReceivePacket;
                mReceivePacket = null;

                IReceiver receiver = mReceiver;
                mReceiver = null;

                IReceiveDelivery delivery = mDelivery;
                mDelivery = null;

                if (packet != null && delivery != null)
                {
                    if (mSurplusLen > 0)
                    {
                        packet.EndPacket();
                        delivery.PostReceiveEnd(packet, false);
                    }
                }

                if (receiver != null)
                    receiver.Destroy();

                if (delivery != null)
                    delivery.PostBlinkDisconnect();

                SetBuffer(null, 0, 0);
                Dispose();
            }
        }
    }
}
