using Net.Qiujuer.Blink.Listener;

namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Receiver delivery interface
    /// </summary>
    public abstract class ReceiveDelivery
    {
        private readonly ReceiveListener mListener;

        public ReceiveDelivery(ReceiveListener listener)
        {
            mListener = listener;
        }


        /**
         * Get ReceiveListener
         *
         * @return ReceiveListener
         */
        protected ReceiveListener GetReceiveListener()
        {
            return mListener;
        }

        /**
         * Parses a start response from the receiver.
         */
        public abstract void PostReceiveStart(ReceivePacket entity);

        /**
         * Parses a end response from the receiver.
         */
        public abstract void PostReceiveEnd(ReceivePacket entity, bool isSuccess);

        /**
         * Parses a progress response from the receiver.
         */
        public abstract void PostReceiveProgress(ReceivePacket entity, int total, int cur);
    }
}
