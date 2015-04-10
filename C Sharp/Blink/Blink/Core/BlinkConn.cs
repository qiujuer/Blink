using Net.Qiujuer.Blink.Box;
using Net.Qiujuer.Blink.Listener;
using Net.Qiujuer.Blink.Tool;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    public class BlinkConn
    {

        private readonly AutoQueue<SendPacket> mSendQueue = new AutoQueue<SendPacket>();

        private readonly ISender mSender;

        private readonly ISendDelivery mSendDelivery;

        private readonly IReceiver mReceiver;

        private readonly IReceiveDelivery mReceiveDelivery;

        private readonly IResource mResource;

        private SendDispatcher mSendDispatcher;

        private ReceiveDispatcher mReceiveDispatcher;

        public BlinkConn(ISender sender, ISendDelivery sendDelivery, IReceiver receiver, IReceiveDelivery receiveDelivery, IResource resource)
        {
            mSender = sender;
            mReceiver = receiver;
            mResource = resource;

            mSendDelivery = sendDelivery;
            mReceiveDelivery = receiveDelivery;

            // Init this
            Init();
        }

        /**
         * Starts the dispatchers in this queue.
         */
        private void Init()
        {
            // Create the cache dispatcher and start it.
            mSendDispatcher = new SendDispatcher(mSendQueue, mSender, mSendDelivery);
            mSendDispatcher.Start();

            mReceiveDispatcher = new ReceiveDispatcher(mReceiver, mReceiveDelivery, this);
            mReceiveDispatcher.Start();
        }

        /**
         * Stops the cache and network dispatchers.
         */
        public void Destroy()
        {
            if (mResource != null)
                mResource.Clear();

            if (mSender != null)
                mSender.Dispose();

            if (mReceiver != null)
                mReceiver.Dispose();

            if (mSendDispatcher != null)
                mSendDispatcher.Quit();

            if (mReceiveDispatcher != null)
                mReceiveDispatcher.Quit();

        }

        /**
         * Get file resource
         *
         * @return Resource
         */
        public IResource GetResource()
        {
            return mResource;
        }

        /**
         * Send a Entity to queue
         *
         * @param entity SendEntity<T> {@link SendPacket}
         * @param <T>    Extends SendEntity
         * @return SendEntity<T>
         */
        public SendPacket Send(SendPacket entity)
        {
            entity.SetBlinkConn(this);

            mSendQueue.Enqueue(entity);

            return entity;
        }

        /**
         * Send file to queue
         *
         * @param file File
         * @return FileSendEntity {@link FileSendPacket}
         */
        public FileSendPacket Send(FileInfo file)
        {
            return Send(file, null);
        }

        /**
         * Send file to queue
         *
         * @param file     File
         * @param listener Callback listener
         * @return FileSendEntity {@link FileSendPacket}
         */
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

        /**
         * Send byte array to queue
         *
         * @param bytes    Byte array
         * @param listener Callback listener
         * @return ByteSendEntity {@link ByteSendPacket}
         */
        public ByteSendPacket Send(byte[] bytes, SendListener listener)
        {
            ByteSendPacket entity = new ByteSendPacket(bytes, listener);
            Send(entity);
            return entity;
        }

        /**
         * Send string to queue
         *
         * @param str String msg
         * @return StringSendEntity {@link StringSendPacket}
         */
        public StringSendPacket Send(String str)
        {
            return Send(str, null);
        }

        /**
         * Send string to queue
         *
         * @param str      String msg
         * @param listener Callback listener
         * @return StringSendEntity {@link StringSendPacket}
         */
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
            //mSendQueue.(entity);
        }
    }
}
