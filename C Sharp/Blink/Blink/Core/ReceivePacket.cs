using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Receive Packet
    /// </summary>
    public abstract class ReceivePacket : BlinkPacket
    {
        private readonly long mId;
        protected Stream mOutStream;
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

        public Stream GetOutputStream()
        {
            return mOutStream;
        }

        public void SetHash(String hashCode)
        {
            mHash = hashCode;
        }

        public String GetHash()
        {
            return mHash;
        }

        internal abstract void AdjustStream();

        internal abstract void AdjustPacket();
    }
}
