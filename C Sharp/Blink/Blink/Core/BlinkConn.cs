using Net.Qiujuer.Blink.Async;
using Net.Qiujuer.Blink.Box;
using Net.Qiujuer.Blink.Listener;
using System;
using System.IO;

namespace Net.Qiujuer.Blink.Core
{
    public class BlinkConn : IDisposable
    {
        private readonly Sender mSender;
        private readonly Receiver mReceiver;
        private readonly SendDelivery mSendDelivery;
        private readonly ReceiveDelivery mReceiveDelivery;
        private readonly BlinkDelivery mBlinkDelivery;
        private readonly Resource mResource;
        private readonly BlinkParser mParser;
        private AsyncSendDispatcher mSendDispatcher;
        private AsyncReceiveDispatcher mReceiveDispatcher;


        public BlinkConn(Sender sender,
            Receiver receiver,
            SendDelivery sendDelivery,
            ReceiveDelivery receiveDelivery,
            BlinkDelivery blinkDelivery,
            Resource resource,
            BlinkParser parser,
            float progressPrecision)
        {
            mSender = sender;
            mReceiver = receiver;
            mResource = resource;

            mSendDelivery = sendDelivery;
            mReceiveDelivery = receiveDelivery;
            mBlinkDelivery = blinkDelivery;

            mParser = parser;

            // Init this
            Init(progressPrecision);
        }

        /// <summary>
        /// Starts the dispatchers in this queue.
        /// </summary>
        private void Init(float progressPrecision)
        {
            // Create the cache dispatcher and start it.
            mSendDispatcher = new AsyncSendDispatcher(mSender, mSendDelivery, progressPrecision);

            mReceiveDispatcher = new AsyncReceiveDispatcher(mReceiver, mParser, mReceiveDelivery, mBlinkDelivery, progressPrecision);
        }

        /// <summary>
        /// Stops the cache and network dispatchers.
        /// </summary>
        public void Dispose()
        {
            if (mSendDelivery != null)
                mSendDelivery.Dispose();

            if (mBlinkDelivery != null)
                mBlinkDelivery.Dispose();

            if (mSender != null)
                mSender.Dispose();

            if (mReceiver != null)
                mReceiver.Dispose();

            if (mSendDispatcher != null)
                mSendDispatcher.Dispose();

            if (mReceiveDispatcher != null)
                mReceiveDispatcher.Dispose();

        }

        /// <summary>
        /// Get file resource
        /// </summary>
        /// <returns>Resource</returns>
        public Resource GetResource()
        {
            return mResource;
        }

        /// <summary>
        ///  Send a Entity to queue
        /// </summary>
        /// <param name="packet">SendPacket</param>
        /// <returns>SendPacket</returns>
        public SendPacket Send(SendPacket packet)
        {
            packet.SetBlinkConn(this);

            mSendDispatcher.Send(packet);

            return packet;
        }


        /// <summary>
        /// Send file to queue
        /// </summary>
        /// <param name="file">File Info</param>
        /// <returns>FileSendPacket</returns>
        public FileSendPacket Send(FileInfo file)
        {
            return Send(file, null);
        }


        /// <summary>
        /// Send file to queue
        /// </summary>
        /// <param name="file">File Info</param>
        /// <param name="listener">SendListener</param>
        /// <returns>FileSendPacket</returns>
        public FileSendPacket Send(FileInfo file, SendListener listener)
        {
            FileSendPacket entity = new FileSendPacket(file, listener);
            Send(entity);
            return entity;
        }

        /**
         * Send byte array to queue
         *
         * @param bytes Byte array
         * @return ByteSendEntity {@link ByteSendPacket}
         */
        public ByteSendPacket Send(byte[] bytes)
        {
            return Send(bytes, null);
        }


        /// <summary>
        /// Send byte array to queue
        /// </summary>
        /// <param name="bytes"> Byte array</param>
        /// <param name="listener">Callback listener</param>
        /// <returns>ByteSendPacket</returns>
        public ByteSendPacket Send(byte[] bytes, SendListener listener)
        {
            ByteSendPacket entity = new ByteSendPacket(bytes, listener);
            Send(entity);
            return entity;
        }


        /// <summary>
        /// Send string to queue
        /// </summary>
        /// <param name="str">String msg</param>
        /// <returns>StringSendPacket</returns>
        public StringSendPacket Send(String str)
        {
            return Send(str, null);
        }


        /// <summary>
        /// Send string to queue
        /// </summary>
        /// <param name="str">String msg</param>
        /// <param name="listener">SendListener</param>
        /// <returns>StringSendPacket</returns>
        public StringSendPacket Send(String str, SendListener listener)
        {
            StringSendPacket entity = null;
            try
            {
                entity = new StringSendPacket(str, listener);
                Send(entity);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
            return entity;
        }

        internal void Cancel(SendPacket packet)
        {
            mSendDispatcher.Cancel(packet);
        }
    }
}
