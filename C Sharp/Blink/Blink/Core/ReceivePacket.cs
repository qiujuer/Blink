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

        public ReceivePacket(long id, int type, long len)
            : base(type)
        {
            mId = id;
            mLength = len;
        }

        public long GetId()
        {
            return mId;
        }

        public void SetHash(String hashCode)
        {
            mHash = hashCode;
        }

        public String GetHash()
        {
            return mHash;
        }

        public virtual void WriteInfo(byte[] buffer, int offset, int count) { }

        public abstract void Write(byte[] buffer, int offset, int count);
    }
}
