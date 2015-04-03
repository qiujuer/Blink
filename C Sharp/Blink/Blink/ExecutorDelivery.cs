using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using Net.Qiujuer.Blink.tool;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink
{
    class ExecutorDelivery : ReceiveDelivery, SendDelivery
    {
        private readonly Executor mPoster;

        public ExecutorDelivery(Executor executor, ReceiveListener listener)
            : base(listener)
        {
            mPoster = executor;
        }

        public override void PostReceiveStart(ReceivePacket entity)
        {
            mPoster.execute(new ReceiveDeliveryRunnable(GetReceiveListener(), entity, null, false));
        }

        public override void PostReceiveEnd(ReceivePacket entity, bool isSuccess)
        {
            entity.SetSuccess(isSuccess);
            mPoster.execute(new ReceiveDeliveryRunnable(GetReceiveListener(), entity, null, true));
        }

        public override void PostReceiveProgress(ReceivePacket entity, int total, int cur)
        {
            ProgressStatus status = new ProgressStatus(total, cur);
            mPoster.execute(new ReceiveDeliveryRunnable(GetReceiveListener(), entity, status, false));
        }

        public void PostSendStart(SendPacket entity)
        {
            mPoster.execute(new SendDeliveryRunnable(entity, null, false));
        }

        public void PostSendEnd(SendPacket entity, bool isSuccess)
        {
            entity.SetSuccess(isSuccess);
            mPoster.execute(new SendDeliveryRunnable(entity, null, true));
        }

        public void PostSendProgress(SendPacket entity, int total, int cur)
        {
            ProgressStatus status = new ProgressStatus(total, cur);
            mPoster.execute(new SendDeliveryRunnable(entity, status, false));
        }

        private class ReceiveDeliveryRunnable : Runnable
        {
            private ReceiveListener listener;
            private ReceivePacket entity;
            private ProgressStatus status;
            private bool isEnd;

            public ReceiveDeliveryRunnable(ReceiveListener listener, ReceivePacket entity, ProgressStatus status, bool isEnd)
            {
                this.listener = listener;
                this.entity = entity;
                this.status = status;
                this.isEnd = isEnd;
            }

            public void run()
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


            public void run()
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
