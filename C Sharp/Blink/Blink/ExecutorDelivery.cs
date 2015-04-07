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
        private readonly BlinkListener mListener;
        private readonly Executor mPoster;

        public ExecutorDelivery(Executor executor, BlinkListener listener)
        {
            mPoster = executor;
            mListener = listener;
        }

        public void PostReceiveStart(ReceivePacket entity)
        {
            mPoster.Execute(new ReceiveDeliveryRunnable(GetReceiveListener(), entity, null, false));
        }

        public void PostReceiveEnd(ReceivePacket entity, bool isSuccess)
        {
            entity.SetSuccess(isSuccess);
            mPoster.Execute(new ReceiveDeliveryRunnable(GetReceiveListener(), entity, null, true));
        }

        public void PostReceiveProgress(ReceivePacket entity, int total, int cur)
        {
            ProgressStatus status = new ProgressStatus(total, cur);
            mPoster.Execute(new ReceiveDeliveryRunnable(GetReceiveListener(), entity, status, false));
        }

        public void PostSendStart(SendPacket entity)
        {
            mPoster.Execute(new SendDeliveryRunnable(entity, null, false));
        }

        public void PostSendEnd(SendPacket entity, bool isSuccess)
        {
            entity.SetSuccess(isSuccess);
            mPoster.Execute(new SendDeliveryRunnable(entity, null, true));
        }

        public void PostSendProgress(SendPacket entity, int total, int cur)
        {
            ProgressStatus status = new ProgressStatus(total, cur);
            mPoster.Execute(new SendDeliveryRunnable(entity, status, false));
        }


        /**
         * Get ReceiveListener
         *
         * @return ReceiveListener
         */
        protected BlinkListener GetReceiveListener()
        {
            return mListener;
        }

        private class ReceiveDeliveryRunnable : Runnable
        {
            private BlinkListener listener;
            private ReceivePacket entity;
            private ProgressStatus status;
            private bool isEnd;

            public ReceiveDeliveryRunnable(BlinkListener listener, ReceivePacket entity, ProgressStatus status, bool isEnd)
            {
                this.listener = listener;
                this.entity = entity;
                this.status = status;
                this.isEnd = isEnd;
            }

            public void Run()
            {
                if (listener != null)
                {
                    if (status != null)
                        listener.OnReceiveProgress(entity.GetType(), entity.GetId(), status.total, status.cur);
                    else if (isEnd)
                        listener.OnReceiveEnd(entity);
                    else
                        listener.OnReceiveStart(entity.GetType(), entity.GetId());
                }
                entity = null;
                status = null;
                listener = null;
            }
        }


        private class SendDeliveryRunnable : Runnable
        {
            private SendPacket entity;
            private ProgressStatus status;
            private bool isEnd;

            public SendDeliveryRunnable(SendPacket entity, ProgressStatus status, bool isEnd)
            {
                this.entity = entity;
                this.status = status;
                this.isEnd = isEnd;
            }


            public void Run()
            {
                if (!entity.IsCanceled())
                {
                    if (status != null)
                        entity.DeliverProgress(status.total, status.cur);
                    else if (isEnd)
                        entity.DeliverEnd();
                    else
                        entity.DeliverStart();
                }

                entity = null;
                status = null;
            }
        }

        /**
         * Send progress status
         */
        private class ProgressStatus
        {
            internal int total;
            internal int cur;

            internal ProgressStatus(int total, int cur)
            {
                this.total = total;
                this.cur = cur;
            }
        }
    }
}
