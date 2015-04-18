
namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Blink Data Packet
    /// </summary>
    public abstract class BlinkPacket : Packet
    {
        protected byte mType;
        protected long mLength;
        private bool mSucceed;

        public BlinkPacket(byte type)
        {
            mType = type;
        }

        public byte GetPacketType()
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

        /// <summary>
        /// On Send or Receive start call this
        /// </summary>
        /// <returns>Init status</returns>
        internal abstract bool StartPacket();

        /// <summary>
        /// On Send or Receive end call this
        /// </summary>
        internal abstract void EndPacket();

        /// <summary>
        /// Packet Type
        /// </summary>
        public static class PacketType
        {
            public const byte STRING = 1;
            public const byte BYTES = 2;
            public const byte FILE = 3;
        }
    }
}
