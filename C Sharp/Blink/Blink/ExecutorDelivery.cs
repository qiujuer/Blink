using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using Net.Qiujuer.Blink.Tool;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink
{
    class ExecutorDelivery : IReceiveDelivery, ISendDelivery
    {
        private event EventHandler<Runnable> mBlinkPoster;
        private event EventHandler<Runnable> mSendPoster;
        private readonly BlinkListener mBlinkListener;

        public ExecutorDelivery(BlinkListener listener)
        {
            mBlinkPoster += ExecutorDelivery_BlinkPoster;
            mSendPoster += ExecutorDelivery_SendPoster;

            mBlinkListener = listener;
        }

        void ExecutorDelivery_SendPoster(object sender, Runnable e)
        {
            e.Run();
        }

        void ExecutorDelivery_BlinkPoster(object sender, Runnable e)
        {
            e.Run();
        }

        public void PostReceiveStart(ReceivePacket entity)
        {
            mBlinkPoster.BeginInvoke(null, new ReceiveDeliveryRunnable(GetBlinkListener(), entity, 0, false), null, null);
        }

        public void PostReceiveEnd(ReceivePacket entity, bool isSuccess)
        {
            entity.SetSuccess(isSuccess);
            mBlinkPoster.BeginInvoke(null, new ReceiveDeliveryRunnable(GetBlinkListener(), entity, 0, true), null, null);
        }

        public void PostReceiveProgress(ReceivePacket entity, float progress)
        {
            mBlinkPoster.BeginInvoke(null, new ReceiveDeliveryRunnable(GetBlinkListener(), entity, progress, false), null, null);
        }

        public void PostSendStart(SendPacket entity)
        {
            mSendPoster.BeginInvoke(null, new SendDeliveryRunnable(entity, 0, false), null, null);
        }

        public void PostSendEnd(SendPacket entity, bool isSuccess)
        {
            entity.SetSuccess(isSuccess);
            mSendPoster.BeginInvoke(null, new SendDeliveryRunnable(entity, 0, true), null, null);
        }

        public void PostSendProgress(SendPacket entity, float progress)
        {
            mSendPoster.BeginInvoke(null, new SendDeliveryRunnable(entity, progress, false), null, null);
        }


        /**
         * Get ReceiveListener
         *
         * @return ReceiveListener
         */
        protected BlinkListener GetBlinkListener()
        {
            return mBlinkListener;
        }

        private class ReceiveDeliveryRunnable : Runnable
        {
            private BlinkListener listener;
            private ReceivePacket entity;
            private float progress;
            private bool isEnd;

            public ReceiveDeliveryRunnable(BlinkListener listener, ReceivePacket entity, float progress, bool isEnd)
            {
                this.listener = listener;
                this.entity = entity;
                this.progress = progress;
                this.isEnd = isEnd;
            }

            public void Run()
            {
                if (listener != null)
                {
                    if (progress > 0)
                        listener.OnReceiveProgress(entity, progress);
                    else if (isEnd)
                        listener.OnReceiveEnd(entity);
                    else
                        listener.OnReceiveStart(entity.GetType(), entity.GetId());
                }
                entity = null;
                listener = null;
            }
        }


        private class SendDeliveryRunnable : Runnable
        {
            private SendPacket entity;
            private float progress;
            private bool isEnd;

            public SendDeliveryRunnable(SendPacket entity, float progress, bool isEnd)
            {
                this.entity = entity;
                this.progress = progress;
                this.isEnd = isEnd;
            }


            public void Run()
            {
                if (!entity.IsCanceled())
                {
                    if (progress > 0)
                        entity.DeliverProgress(progress);
                    else if (isEnd)
                        entity.DeliverEnd();
                    else
                        entity.DeliverStart();
                }

                entity = null;
            }
        }
    }
}
