using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Kit;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Listener.Delivery
{
    class TaskDelivery : ReceiveDelivery, SendDelivery
    {
        private BlinkListener mBlinkListener;
        private Queue<Runnable> mQueue = new Queue<Runnable>();
        private volatile bool IsNotify = false;

        public TaskDelivery(BlinkListener listener)
        {
            mBlinkListener = listener;
        }

        private void Run()
        {
            try
            {
                while (true)
                {
                    Runnable runnable = null;
                    lock (mQueue)
                    {
                        runnable = mQueue.Dequeue();
                    }
                    runnable.Run();
                }
            }
            catch (Exception)
            {
                IsNotify = false;
            }

        }

        private void PostQueue(Runnable runnable)
        {
            lock (mQueue)
            {
                mQueue.Enqueue(runnable);
            }

            if (!IsNotify)
            {
                IsNotify = true;

                Task task = new Task(Run);
                task.Start();
            }
        }

        public void PostReceiveStart(ReceivePacket entity)
        {
            BlinkListener listener = GetBlinkListener();
            if (listener != null && entity != null)
                PostQueue(new ReceiveStartDeliveryRunnable(listener, entity));
        }

        public void PostReceiveEnd(ReceivePacket entity, bool isSuccess)
        {
            BlinkListener listener = GetBlinkListener();
            if (listener != null && entity != null)
            {
                entity.SetSuccess(isSuccess);
                PostQueue(new ReceiveEndDeliveryRunnable(listener, entity));
            }
        }

        public void PostReceiveProgress(ReceivePacket entity, float progress)
        {
            BlinkListener listener = GetBlinkListener();
            if (listener != null && entity != null)
                PostQueue(new ReceiveProgressDeliveryRunnable(listener, entity, progress));
        }

        public void PostBlinkDisconnect()
        {
            BlinkListener listener = GetBlinkListener();
            if (listener != null)
                PostQueue(new BlinkDeliveryRunnable(listener));
        }

        public void PostSendProgress(SendPacket entity, float progress)
        {
            if (entity != null && entity.Listener != null)
                PostQueue(new SendDeliveryRunnable(entity, progress));
        }

        /// <summary>
        /// Get ReceiveListener
        /// </summary>
        /// <returns>ReceiveListener</returns>
        protected BlinkListener GetBlinkListener()
        {
            return mBlinkListener;
        }

        /// <summary>
        /// Destroy the event
        /// </summary>
        public void Dispose()
        {
            mBlinkListener = null;
            mQueue.Clear();
        }

        private class BlinkDeliveryRunnable : Runnable
        {
            private BlinkListener listener;
            public BlinkDeliveryRunnable(BlinkListener listener)
            {
                this.listener = listener;
            }
            public void Run()
            {
                if (listener != null)
                {
                    listener.OnBlinkDisconnect();
                    listener = null;
                }
            }
        }

        private class ReceiveStartDeliveryRunnable : Runnable
        {
            private BlinkListener listener;
            private ReceivePacket entity;

            public ReceiveStartDeliveryRunnable(BlinkListener listener, ReceivePacket entity)
            {
                this.listener = listener;
                this.entity = entity;
            }

            public void Run()
            {
                listener.OnReceiveStart(entity.GetType(), entity.GetId());
                entity = null;
                listener = null;
            }
        }

        private class ReceiveEndDeliveryRunnable : Runnable
        {
            private BlinkListener listener;
            private ReceivePacket entity;

            public ReceiveEndDeliveryRunnable(BlinkListener listener, ReceivePacket entity)
            {
                this.listener = listener;
                this.entity = entity;
            }

            public void Run()
            {
                listener.OnReceiveEnd(entity);
                entity = null;
                listener = null;
            }
        }

        private class ReceiveProgressDeliveryRunnable : Runnable
        {
            private BlinkListener listener;
            private ReceivePacket entity;
            private float progress;

            public ReceiveProgressDeliveryRunnable(BlinkListener listener, ReceivePacket entity, float progress)
            {
                this.listener = listener;
                this.entity = entity;
                this.progress = progress;
            }

            public void Run()
            {
                listener.OnReceiveProgress(entity, progress);
                entity = null;
                listener = null;
            }
        }

        private class SendDeliveryRunnable : Runnable
        {
            private SendPacket entity;
            private float progress;

            public SendDeliveryRunnable(SendPacket entity, float progress)
            {
                this.entity = entity;
                this.progress = progress;
            }


            public void Run()
            {
                if (!entity.IsCanceled())
                {
                    entity.Listener.OnSendProgress(progress);
                }

                entity = null;
            }
        }
    }
}
