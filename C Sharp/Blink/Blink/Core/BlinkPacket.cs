
namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Blink Data Packet
    /// </summary>
    public abstract class BlinkPacket : IBlinkPacket
    {
        protected int mType;
        protected long mLength;
        private bool mSucceed;

        public BlinkPacket(int type)
        {
            mType = type;
        }

        public new int GetType()
        {
            return mType;
        }

        public long GetLength()
        {
            return mLength;
        }

        public void SetSuccess(bool isSuccess)
        {
            this.mSucceed = isSuccess;
        }

        public bool IsSucceed()
        {
            return mSucceed;
        }

        internal abstract bool StartPacket();

        internal abstract void EndPacket();

        public static class PacketType
        {
            public const int STRING = 1;
            public const int BYTES = 2;
            public const int FILE = 3;
        }
    }
}
