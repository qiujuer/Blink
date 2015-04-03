using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    /**
     * Provides a thread for performing send dispatch from a queue of BinkConn {@link BlinkConn}.
     */
    public class SendDispatcher
    {
        /**
         * The queue of send entity.
         */
        private readonly Queue<SendPacket> mQueue;
        /**
         * The sender interface for processing sender requests.
         */
        private readonly Sender mSender;
        /**
         * For posting send responses.
         */
        private readonly SendDelivery mDelivery;
        /**
         * Used for telling us to die.
         */
        private volatile bool mQuit = false;
        private Thread mWork;

        public SendDispatcher(Queue<SendPacket> queue,
                              Sender sender, SendDelivery delivery)
        {
            mQueue = queue;
            mSender = sender;
            mDelivery = delivery;
        }

        public void Start()
        {
            mWork = new Thread(Run);
            mWork.Start();
        }

        /**
         * Forces this dispatcher to quit immediately.  If any requests are still in
         * the queue, they are not guaranteed to be processed.
         */
        public void Quit()
        {
            mQuit = true;
            mWork.Interrupt();
        }


        public void Run()
        {
            while (true)
            {
                SendPacket entity;
                try
                {
                    // Take a request from the queue.
                    entity = mQueue.Dequeue();
                }
                catch (Exception e)
                {
                    // We may have been interrupted because it was time to quit.
                    if (mQuit)
                    {
                        return;
                    }
                    continue;
                }

                try
                {
                    if (entity.IsCanceled())
                    {
                        continue;
                    }
                    // Post Start
                    mDelivery.PostSendStart(entity);

                    // Send
                    bool status = mSender.SendHead(entity) && mSender.SendEntity(entity, mDelivery);

                    // Post End
                    mDelivery.PostSendEnd(entity, status);

                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                    //mDelivery.postSendError();
                }
            }
        }
    }
}
