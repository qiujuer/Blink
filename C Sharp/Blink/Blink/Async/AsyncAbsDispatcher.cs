using System.Net.Sockets;

namespace Net.Qiujuer.Blink.Async
{
    public class AsyncAbsDispatcher : SocketAsyncEventArgs
    {
        protected const int HeadSize = 11;

        protected readonly float mProgressPrecision;

        protected volatile bool mDisposed = false;

        protected float mProgress = 0;

        protected bool mStatus = true;




        public AsyncAbsDispatcher(float progressPrecision)
        {
            mProgressPrecision = progressPrecision;
        }


        protected bool IsNotifyProgress(float newProgress)
        {
            if ((newProgress - mProgress) > mProgressPrecision)
            {
                mProgress = newProgress;
                return true;
            }
            else
            {
                return false;
            }
        }

        protected bool IsDisposed()
        {
            if (mDisposed)
                return true;
            else
            {
                mDisposed = true;
                return false;
            }
        }
    }
}
