using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Kit;
using Net.Qiujuer.Blink.Listener;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Kit
{
    class CallBackDelivery : BlinkDelivery, SendDelivery, ReceiveDelivery
    {
        private BlinkListener mBlinkListener;
        private ReceiveListener mReceiveListener;
        private Queue<Runnable> mQueue = new Queue<Runnable>();
        private volatile bool IsNotify = false;

        public CallBackDelivery(BlinkListener blinkListener, ReceiveListener receiveListener)
        {
            mBlinkListener = blinkListener;
            mReceiveListener = receiveListener;
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
            ReceiveListener listener = mReceiveListener;
            if (listener != null && entity != null)
                PostQueue(new ReceiveStartDeliveryRunnable(listener, entity));
        }

        public void PostReceiveEnd(ReceivePacket entity, bool isSuccess)
        {
            ReceiveListener listener = mReceiveListener;
            if (listener != null && entity != null)
            {
                entity.SetSuccess(isSuccess);
                PostQueue(new ReceiveEndDeliveryRunnable(listener, entity));
            }
        }

        public void PostReceiveProgress(ReceivePacket entity, float progress)
        {
            ReceiveListener listener = mReceiveListener;
            if (listener != null && entity != null)
                PostQueue(new ReceiveProgressDeliveryRunnable(listener, entity, progress));
        }

        public void PostBlinkDisconnect()
        {
            BlinkListener listener = mBlinkListener;
            if (listener != null)
                PostQueue(new BlinkDeliveryRunnable(listener));
        }

        public void PostSendProgress(SendPacket entity, float progress)
        {
            if (entity != null && entity.Listener != null)
                PostQueue(new SendDeliveryRunnable(entity, progress));
        }

        /// <summary>
        /// Destroy the event
        /// </summary>
        public void Dispose()
        {
            mBlinkListener = null;
            mReceiveListener = null;
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
            private ReceiveListener listener;
            private ReceivePacket entity;

            public ReceiveStartDeliveryRunnable(ReceiveListener listener, ReceivePacket entity)
            {
                this.listener = listener;
                this.entity = entity;
            }

            public void Run()
            {
                listener.OnReceiveStart(entity.GetPacketType(), entity.GetId());
                entity = null;
                listener = null;
            }
        }

        private class ReceiveEndDeliveryRunnable : Runnable
        {
            private ReceiveListener listener;
            private ReceivePacket entity;

            public ReceiveEndDeliveryRunnable(ReceiveListener listener, ReceivePacket entity)
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
            private ReceiveListener listener;
            private ReceivePacket entity;
            private float progress;

            public ReceiveProgressDeliveryRunnable(ReceiveListener listener, ReceivePacket entity, float progress)
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
