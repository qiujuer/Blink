using System;

namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Receive Packet
    /// </summary>
    public abstract class ReceivePacket : BlinkPacket
    {
        private readonly long mId;
        private String mHash;

        public ReceivePacket(long id, byte type, long len)
            : base(type)
        {
            mId = id;
            mLength = len;
        }

        public long GetId()
        {
            return mId;
        }

        /// <summary>
        /// Set Packet Hash Code
        /// </summary>
        /// <param name="hashCode">HashCode</param>
        public void SetHash(String hashCode)
        {
            mHash = hashCode;
        }

        /// <summary>
        /// Get Packet Hash Code
        /// </summary>
        /// <returns>HashCode</returns>
        public String GetHash()
        {
            return mHash;
        }

        /// <summary>
        /// On Receiver receive some info buffer call this
        /// </summary>
        /// <param name="buffer">Info data</param>
        /// <param name="offset">Buffer offset</param>
        /// <param name="count">Buffer Count</param>
        internal virtual void WriteInfo(byte[] buffer, int offset, int count) { }

        /// <summary>
        /// Receiver write buffer
        /// </summary>
        /// <param name="buffer">Buffer</param>
        /// <param name="offset">Buffer offset</param>
        /// <param name="count">Buffer Count</param>
        internal abstract void Write(byte[] buffer, int offset, int count);
    }
}
