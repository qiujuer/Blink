using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Listener.Delivery
{
    class DelegateDelivery : IReceiveDelivery, ISendDelivery
    {
        private delegate void Progress();

        private BlinkListener mBlinkListener;
        private ConcurrentQueue<Action> mQueue = new ConcurrentQueue<Action>();
        private volatile bool IsNotify = false;

        public DelegateDelivery(BlinkListener listener)
        {
            mBlinkListener = listener;
        }

        private void Run()
        {
            Action action = null;
            while (IsNotify = mQueue.TryDequeue(out action))
            {
                action();
                action -= action;
            }
        }

        private void PostQueue(Action action)
        {
            mQueue.Enqueue(action);

            if (!IsNotify)
            {
                IsNotify = true;

                Task task = new Task(Run);
                task.Start();
            }
        }

        public void PostReceiveStart(ReceivePacket entity)
        {
            BlinkListener listener = mBlinkListener;
            if (listener != null && entity != null)
            {
                PostQueue(() =>
                {
                    listener.OnReceiveStart(entity.GetType(), entity.GetId());
                });
            }
        }

        public void PostReceiveEnd(ReceivePacket entity, bool isSuccess)
        {
            BlinkListener listener = mBlinkListener;
            if (listener != null && entity != null)
            {
                entity.SetSuccess(isSuccess);
                PostQueue(() =>
                {
                    listener.OnReceiveEnd(entity);
                });
            }
        }

        public void PostReceiveProgress(ReceivePacket entity, float progress)
        {
            BlinkListener listener = mBlinkListener;
            if (listener != null && entity != null)
            {
                PostQueue(() =>
                {
                    listener.OnReceiveProgress(entity, progress);
                });
            }
        }

        public void PostBlinkDisconnect()
        {
            BlinkListener listener = mBlinkListener;
            if (listener != null)
            {
                PostQueue(listener.OnBlinkDisconnect);
            }
        }

        public void PostSendProgress(SendPacket entity, float progress)
        {
            if (entity != null && entity.Listener != null)
            {
                PostQueue(() =>
                {
                    entity.Listener.OnSendProgress(progress);
                });

            }
        }

        /// <summary>
        /// Destroy the event
        /// </summary>
        public void Destroy()
        {
            mBlinkListener = null;
            Action action = null;
            while (mQueue.TryDequeue(out action))
            {
                action -= action;
            }
        }
    }
}
