using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Kit;
using Net.Qiujuer.Blink.Listener;
using System;

namespace Net.Qiujuer.Blink.Kit
{
    class EventDelivery : BlinkDelivery, SendDelivery, ReceiveDelivery
    {
        private event EventHandler<Runnable> mBlinkPoster;
        private event EventHandler<Runnable> mSendPoster;
        private BlinkListener mBlinkListener;
        private ReceiveListener mReceiveListener;


        public EventDelivery(BlinkListener blinkListener, ReceiveListener receiveListener)
        {
            mBlinkPoster += ExecutorDelivery_BlinkPoster;
            mSendPoster += ExecutorDelivery_SendPoster;

            mBlinkListener = blinkListener;
            mReceiveListener = receiveListener;
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
            ReceiveListener listener = mReceiveListener;
            if (listener != null)
                PostBlink(new ReceiveDeliveryRunnable(listener, entity, 0, false));
        }

        public void PostReceiveEnd(ReceivePacket entity, bool isSuccess)
        {
            ReceiveListener listener = mReceiveListener;
            if (listener != null)
            {
                entity.SetSuccess(isSuccess);
                PostBlink(new ReceiveDeliveryRunnable(listener, entity, 0, true));
            }
        }

        public void PostReceiveProgress(ReceivePacket entity, float progress)
        {
            ReceiveListener listener = mReceiveListener;
            if (listener != null)
                PostBlink(new ReceiveDeliveryRunnable(listener, entity, progress, false));
        }

        public void PostBlinkDisconnect()
        {
            BlinkListener listener = mBlinkListener;
            if (listener != null)
                PostBlink(new BlinkDeliveryRunnable(listener));
        }

        private void PostBlink(Runnable runnable)
        {
            EventHandler<Runnable> handler = mBlinkPoster;
            if (handler != null)
            {
                handler.BeginInvoke(null, runnable, null, null);
            }
        }

        private void PostSend(Runnable runnable)
        {
            EventHandler<Runnable> handler = mSendPoster;
            if (handler != null)
            {
                handler.BeginInvoke(null, runnable, null, null);
            }
        }

        public void PostSendStart(SendPacket entity)
        {
            PostSend(new SendDeliveryRunnable(entity, 0, false));
        }

        public void PostSendEnd(SendPacket entity, bool isSuccess)
        {
            entity.SetSuccess(isSuccess);
            PostSend(new SendDeliveryRunnable(entity, 0, true));
        }

        public void PostSendProgress(SendPacket entity, float progress)
        {
            PostSend(new SendDeliveryRunnable(entity, progress, false));
        }

        /// <summary>
        /// Destroy the event
        /// </summary>
        public void Dispose()
        {
            mBlinkListener = null;
            mBlinkPoster -= ExecutorDelivery_BlinkPoster;
            mSendPoster -= ExecutorDelivery_SendPoster;
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

        private class ReceiveDeliveryRunnable : Runnable
        {
            private ReceiveListener listener;
            private ReceivePacket entity;
            private float progress;
            private bool isEnd;

            public ReceiveDeliveryRunnable(ReceiveListener listener, ReceivePacket entity, float progress, bool isEnd)
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
                        listener.OnReceiveStart(entity.GetPacketType(), entity.GetId());
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
                    entity.Listener.OnSendProgress(progress);
                }

                entity = null;
            }
        }
    }
}
