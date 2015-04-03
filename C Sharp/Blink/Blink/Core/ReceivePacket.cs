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
        private String mHashCode;

        public ReceivePacket(long id, int type, int len)
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

        public void SetHashCode(String hashCode)
        {
            mHashCode = hashCode;
        }

        public String GetHashCode()
        {
            return mHashCode;
        }

        internal abstract void AdjustStream();

        internal abstract void AdjustPacket();
    }
}
